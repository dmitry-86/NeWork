package com.netology.nework.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.netology.nework.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getAll(): Flow<List<EventEntity>>

    @Query("SELECT COUNT(*) == 0 FROM EventEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT COUNT(*) FROM EventEntity")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: List<EventEntity>)

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("""
        UPDATE EventEntity SET
               likeOwnerIds = likeOwnerIds + 1,
               likedByMe = 1
           WHERE id = :id AND likedByMe = 0;
        """)
    suspend fun likeById(id: Long)

    @Query(
        """
           UPDATE EventEntity SET
               likeOwnerIds = likeOwnerIds - 1,
               likedByMe = 0
           WHERE id = :id AND likedByMe = 1;
        """)
    suspend fun dislikeById(id: Long)
}