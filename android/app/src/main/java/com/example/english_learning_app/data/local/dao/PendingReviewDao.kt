package com.example.english_learning_app.data.local.dao

import androidx.room.*
import com.example.english_learning_app.data.local.entity.PendingReviewEntity

@Dao
interface PendingReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: PendingReviewEntity)

    @Query("SELECT * FROM pending_reviews ORDER BY createdAt ASC")
    suspend fun getAll(): List<PendingReviewEntity>

    @Query("DELETE FROM pending_reviews WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM pending_reviews")
    suspend fun deleteAll()
}
