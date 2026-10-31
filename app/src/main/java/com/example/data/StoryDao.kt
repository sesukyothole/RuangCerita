package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories ORDER BY id ASC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE genre = :genre ORDER BY id ASC")
    fun getStoriesByGenre(genre: String): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteStories(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE id = :id LIMIT 1")
    fun getStoryById(id: String): Flow<StoryEntity?>

    @Query("SELECT * FROM stories WHERE id = :id LIMIT 1")
    suspend fun getStoryByIdOnce(id: String): StoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    @Query("UPDATE stories SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("UPDATE stories SET lastReadPosition = :position WHERE id = :id")
    suspend fun updateReadPosition(id: String, position: Int)

    @Query("UPDATE stories SET isRead = :isRead WHERE id = :id")
    suspend fun updateReadStatus(id: String, isRead: Boolean)

    @Query("UPDATE stories SET genre = :genre WHERE id = :id")
    suspend fun updateStoryGenre(id: String, genre: String)

    @Query("SELECT * FROM stories WHERE title LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchStories(query: String): Flow<List<StoryEntity>>

    @Query("SELECT COUNT(*) FROM stories")
    suspend fun getStoryCount(): Int

    @Query("DELETE FROM stories WHERE id NOT LIKE 'blogger_%'")
    suspend fun deleteNonSourceStories()
}
