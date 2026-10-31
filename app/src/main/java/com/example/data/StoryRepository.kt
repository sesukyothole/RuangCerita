package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(val message: String, val updatedCount: Int) : SyncState()
    data class Error(val message: String) : SyncState()
}

class StoryRepository(
    private val storyDao: StoryDao,
    private val fetcher: BloggerFetcher = BloggerFetcher()
) {
    val allStories: Flow<List<StoryEntity>> = storyDao.getAllStories()
    val favoriteStories: Flow<List<StoryEntity>> = storyDao.getFavoriteStories()

    suspend fun checkAndPreloadInitialData() = withContext(Dispatchers.IO) {
        // Clean out any old mock data from database completely
        storyDao.deleteNonSourceStories()
        val count = storyDao.getStoryCount()
        if (count == 0) {
            val initial = PreloadedStories.getInitialStories()
            if (initial.isNotEmpty()) {
                storyDao.insertStories(initial)
            }
        }
    }

    fun getStoriesByGenre(genre: String): Flow<List<StoryEntity>> {
        return if (genre == "Semua") {
            storyDao.getAllStories()
        } else {
            storyDao.getStoriesByGenre(genre)
        }
    }

    fun searchStories(query: String): Flow<List<StoryEntity>> {
        return storyDao.searchStories(query)
    }

    fun getStoryById(id: String): Flow<StoryEntity?> {
        return storyDao.getStoryById(id)
    }

    suspend fun toggleFavorite(id: String, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        storyDao.updateFavoriteStatus(id, !currentFavorite)
    }

    suspend fun updateReadPosition(id: String, position: Int) = withContext(Dispatchers.IO) {
        storyDao.updateReadPosition(id, position)
    }

    suspend fun markStoryAsRead(id: String, isRead: Boolean = true) = withContext(Dispatchers.IO) {
        storyDao.updateReadStatus(id, isRead)
    }

    suspend fun updateStoryGenre(id: String, newGenre: String) = withContext(Dispatchers.IO) {
        storyDao.updateStoryGenre(id, newGenre.trim())
    }

    suspend fun syncStoriesFromWeb(
        feedUrl: String = "https://iseng-isex.blogspot.com/feeds/posts/default?alt=json",
        fetchAll: Boolean = true
    ): SyncState = withContext(Dispatchers.IO) {
        // Ensure all non-source stories are cleaned out
        storyDao.deleteNonSourceStories()
        val fetchResult = if (fetchAll) {
            fetcher.fetchAllAvailableStories(feedUrl = feedUrl)
        } else {
            fetcher.fetchLatestStories(feedUrl = feedUrl)
        }
        if (fetchResult.isSuccess) {
            val fetchedList = fetchResult.getOrDefault(emptyList())
            if (fetchedList.isEmpty()) {
                return@withContext SyncState.Success("Data cerita lokal sudah yang terbaru.", 0)
            }

            var newAdded = 0
            for (story in fetchedList) {
                val existing = storyDao.getStoryByIdOnce(story.id)
                if (existing != null) {
                    // Retain user's favorite, read position, read status & custom genre
                    val updated = story.copy(
                        isFavorite = existing.isFavorite,
                        lastReadPosition = existing.lastReadPosition,
                        isRead = existing.isRead,
                        genre = if (existing.genre.isNotBlank()) existing.genre else story.genre
                    )
                    storyDao.insertStory(updated)
                } else {
                    storyDao.insertStory(story)
                    newAdded++
                }
            }

            SyncState.Success(
                message = if (newAdded > 0) "Berhasil memuat $newAdded cerita dari server!" else "Seluruh data cerita server sudah terload (${fetchedList.size} cerita).",
                updatedCount = newAdded
            )
        } else {
            val errorMsg = fetchResult.exceptionOrNull()?.localizedMessage ?: "Tidak terhubung ke server"
            SyncState.Error("Mode Offline Active: $errorMsg")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(context: Context): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val instance = StoryRepository(db.storyDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
