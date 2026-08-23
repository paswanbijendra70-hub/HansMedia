package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.AppDatabase
import com.example.data.MediaEntity
import com.example.data.MediaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application), Player.Listener {

    private val repository: MediaRepository
    val player: ExoPlayer

    // UI state flows
    private val _currentMedia = MutableStateFlow<MediaEntity?>(null)
    val currentMedia: StateFlow<MediaEntity?> = _currentMedia.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    private var progressTrackerJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = MediaRepository(database.mediaDao())

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(application).build().apply {
            addListener(this@MediaViewModel)
        }

        // Populate defaults and initial load
        viewModelScope.launch {
            repository.prePopulateDefaultMedia()
        }
    }

    // Combine all filters to generate a filtered list of media items
    val mediaList: StateFlow<List<MediaEntity>> = combine(
        repository.allMedia,
        _searchQuery,
        _selectedCategory,
        _showFavoritesOnly
    ) { rawList, query, category, favsOnly ->
        rawList.filter { item ->
            val matchesQuery = item.title.contains(query, ignoreCase = true) ||
                    item.artist.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesFav = !favsOnly || item.isFavorite
            matchesQuery && matchesCategory && matchesFav
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleFavoritesFilter() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun playMedia(media: MediaEntity) {
        viewModelScope.launch {
            // Save progress of the previously playing media if any
            _currentMedia.value?.let { prev ->
                repository.updatePlaybackPosition(prev.id, player.currentPosition)
            }

            // Load the new media entity
            _currentMedia.value = media
            
            // Setup ExoPlayer MediaItem (supports local file paths, online urls, and fallback assets)
            val mediaItem = if (media.fileName.startsWith("http://") || media.fileName.startsWith("https://")) {
                MediaItem.fromUri(Uri.parse(media.fileName))
            } else if (media.fileName.startsWith("/") || java.io.File(media.fileName).exists()) {
                MediaItem.fromUri(Uri.fromFile(java.io.File(media.fileName)))
            } else {
                val cleanedFileName = if (media.fileName.endsWith(".mp3")) {
                    media.fileName.substringBeforeLast(".") + ".mp4"
                } else {
                    media.fileName
                }
                MediaItem.fromUri(Uri.parse("asset:///$cleanedFileName"))
            }
            
            player.setMediaItem(mediaItem)
            player.volume = 1.0f // Ensure volume is explicitly maxed
            player.prepare()
            
            // Check if there is saved progress to resume from
            val savedItem = repository.getMediaById(media.id)
            if (savedItem != null && savedItem.playbackPosition > 0 && savedItem.playbackPosition < (player.duration.takeIf { it > 0 } ?: Long.MAX_VALUE)) {
                player.seekTo(savedItem.playbackPosition)
            }
            
            player.play()
            _isPlaying.value = true
            
            // Update active timestamp in DB
            repository.updatePlaybackPosition(media.id, player.currentPosition)
            
            startProgressTracker()
        }
    }

    fun pauseMedia() {
        player.pause()
        _isPlaying.value = false
        stopProgressTracker()
        
        // Save position to database immediately on pause
        viewModelScope.launch {
            _currentMedia.value?.let { media ->
                repository.updatePlaybackPosition(media.id, player.currentPosition)
            }
        }
    }

    fun resumeMedia() {
        player.play()
        _isPlaying.value = true
        startProgressTracker()
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
        _currentPosition.value = position
        
        viewModelScope.launch {
            _currentMedia.value?.let { media ->
                repository.updatePlaybackPosition(media.id, position)
            }
        }
    }

    fun skipNext() {
        val currentList = mediaList.value
        val current = _currentMedia.value ?: return
        val index = currentList.indexOfFirst { it.id == current.id }
        if (index != -1 && index < currentList.size - 1) {
            playMedia(currentList[index + 1])
        } else if (currentList.isNotEmpty()) {
            playMedia(currentList[0]) // loop back to first
        }
    }

    fun skipPrevious() {
        val currentList = mediaList.value
        val current = _currentMedia.value ?: return
        val index = currentList.indexOfFirst { it.id == current.id }
        if (index > 0) {
            playMedia(currentList[index - 1])
        } else if (currentList.isNotEmpty()) {
            playMedia(currentList[currentList.size - 1]) // loop to last
        }
    }

    fun toggleRepeatMode() {
        val newMode = when (_repeatMode.value) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
            else -> Player.REPEAT_MODE_OFF
        }
        player.repeatMode = newMode
        _repeatMode.value = newMode
    }

    fun setPlaybackSpeed(speed: Float) {
        player.playbackParameters = PlaybackParameters(speed)
        _playbackSpeed.value = speed
    }

    fun toggleFavorite(mediaId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(mediaId, isFavorite)
            // Update currentMedia reference if it is the toggled one
            if (_currentMedia.value?.id == mediaId) {
                _currentMedia.value = _currentMedia.value?.copy(isFavorite = isFavorite)
            }
        }
    }

    fun updateNotes(mediaId: String, notes: String) {
        viewModelScope.launch {
            repository.updateUserNotes(mediaId, notes)
            if (_currentMedia.value?.id == mediaId) {
                _currentMedia.value = _currentMedia.value?.copy(userNotes = notes)
            }
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressTrackerJob = viewModelScope.launch {
            while (true) {
                _currentPosition.value = player.currentPosition
                _duration.value = player.duration.coerceAtLeast(0L)
                
                // Periodically save to Room database (every 5 seconds) to prevent losing progress if app crashes
                _currentMedia.value?.let { media ->
                    repository.updatePlaybackPosition(media.id, player.currentPosition)
                }
                delay(1000)
            }
        }
    }

    private fun stopProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = null
    }

    // Player.Listener implementations
    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_READY) {
            _duration.value = player.duration.coerceAtLeast(0L)
        } else if (playbackState == Player.STATE_ENDED) {
            _isPlaying.value = false
            stopProgressTracker()
            skipNext() // auto-play next
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
        if (isPlaying) {
            startProgressTracker()
        } else {
            stopProgressTracker()
        }
    }

    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
        android.util.Log.e("HansMedia", "ExoPlayer Error playing media: ${error.message}", error)
    }

    fun copyMediaFromUri(uri: Uri): String? {
        return try {
            val context = getApplication<Application>()
            val contentResolver = context.contentResolver
            
            // Query for the display name
            val projection = arrayOf(android.provider.OpenableColumns.DISPLAY_NAME)
            var originalName: String? = null
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        originalName = cursor.getString(index)
                    }
                }
            }
            
            val finalFileName = originalName ?: "uploaded_media_${System.currentTimeMillis()}"
            val destFile = java.io.File(context.filesDir, finalFileName)
            
            contentResolver.openInputStream(uri)?.use { inputStream ->
                java.io.FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            android.util.Log.e("HansMedia", "Error copying gallery file: ${e.message}", e)
            null
        }
    }

    fun publishNewMedia(id: String, title: String, artist: String, durationText: String, category: String, fileName: String) {
        viewModelScope.launch {
            val mediaItem = MediaEntity(
                id = id.ifEmpty { java.util.UUID.randomUUID().toString() },
                title = title.ifEmpty { "Untitled Track" },
                artist = artist.ifEmpty { "Unknown Artist" },
                durationText = durationText.ifEmpty { "00:00" },
                category = category.ifEmpty { "Other" },
                fileName = fileName.ifEmpty { "subah_ka_manglacharan.mp4" }
            )
            repository.insertMedia(mediaItem)
        }
    }

    fun deleteMediaItem(id: String) {
        viewModelScope.launch {
            // If the item is currently playing, stop playback first
            if (_currentMedia.value?.id == id) {
                player.stop()
                _currentMedia.value = null
                _isPlaying.value = false
            }
            repository.deleteMedia(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Save final position of currently playing item
        _currentMedia.value?.let { media ->
            player.pause()
            val position = player.currentPosition
            // Launch on GlobalScope or run blocking if needed, but since onCleared is synchronous, we can try to save
            viewModelScope.launch {
                repository.updatePlaybackPosition(media.id, position)
            }
        }
        player.removeListener(this)
        player.release()
    }
}
