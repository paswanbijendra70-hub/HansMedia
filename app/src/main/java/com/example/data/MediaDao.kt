package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY lastPlayedTimestamp DESC, title ASC")
    fun getAllMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE id = :id LIMIT 1")
    suspend fun getMediaById(id: String): MediaEntity?

    @Query("SELECT * FROM media_items WHERE id = :id LIMIT 1")
    fun getMediaByIdFlow(id: String): Flow<MediaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMediaList(mediaList: List<MediaEntity>)

    @Update
    suspend fun updateMedia(media: MediaEntity)

    @Query("UPDATE media_items SET playbackPosition = :position, lastPlayedTimestamp = :timestamp WHERE id = :id")
    suspend fun updatePlaybackPosition(id: String, position: Long, timestamp: Long)

    @Query("UPDATE media_items SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE media_items SET userNotes = :notes WHERE id = :id")
    suspend fun updateUserNotes(id: String, notes: String)

    @Query("UPDATE media_items SET fileName = REPLACE(fileName, '.mp3', '.mp4') WHERE fileName LIKE '%.mp3'")
    suspend fun updateAllMp3ToMp4()

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun deleteMediaById(id: String)
}
