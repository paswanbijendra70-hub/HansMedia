package com.example.data

import kotlinx.coroutines.flow.Flow

class MediaRepository(private val mediaDao: MediaDao) {
    val allMedia: Flow<List<MediaEntity>> = mediaDao.getAllMedia()

    suspend fun getMediaById(id: String): MediaEntity? {
        return mediaDao.getMediaById(id)
    }

    fun getMediaByIdFlow(id: String): Flow<MediaEntity?> {
        return mediaDao.getMediaByIdFlow(id)
    }

    suspend fun insertMedia(media: MediaEntity) {
        mediaDao.insertMedia(media)
    }

    suspend fun updatePlaybackPosition(id: String, position: Long) {
        mediaDao.updatePlaybackPosition(id, position, System.currentTimeMillis())
    }

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        mediaDao.updateFavorite(id, isFavorite)
    }

    suspend fun updateUserNotes(id: String, notes: String) {
        mediaDao.updateUserNotes(id, notes)
    }

    suspend fun deleteMedia(id: String) {
        mediaDao.deleteMediaById(id)
    }

    suspend fun prePopulateDefaultMedia() {
        // App starts clean; no mock or pre-populated media files loaded
    }
}
