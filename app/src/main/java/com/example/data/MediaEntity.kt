package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val durationText: String,
    val category: String,
    val fileName: String,
    val isFavorite: Boolean = false,
    val playbackPosition: Long = 0L,
    val lastPlayedTimestamp: Long = 0L,
    val userNotes: String = ""
)
