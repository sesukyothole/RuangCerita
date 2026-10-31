package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val genre: String,
    val summary: String,
    val content: String,
    val publishedDate: String,
    val readTimeMinutes: Int = 5,
    val isFavorite: Boolean = false,
    val lastReadPosition: Int = 0,
    val isSyncedFromWeb: Boolean = false,
    val sourceUrl: String = "",
    val isRead: Boolean = false
)
