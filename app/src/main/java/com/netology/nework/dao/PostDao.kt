package com.netology.nework.dao

import androidx.room.*
import com.netology.nework.entity.PostEntity
import com.netology.nework.enumeration.AttachmentType
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT COUNT(*) FROM PostEntity")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("SELECT * FROM PostEntity WHERE authorId = :id")
    fun getUserPostsById(id: Long): Flow<List<PostEntity>>

    @Query("""
        UPDATE PostEntity SET
               likeOwnerIds = likeOwnerIds + 1,
               likedByMe = 1
           WHERE id = :id AND likedByMe = 0;
        """)
    suspend fun likeById(id: Long)

    @Query(
        """
           UPDATE PostEntity SET
               likeOwnerIds = likeOwnerIds - 1,
               likedByMe = 0
           WHERE id = :id AND likedByMe = 1;
        """)
    suspend fun dislikeById(id: Long)

}

class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}