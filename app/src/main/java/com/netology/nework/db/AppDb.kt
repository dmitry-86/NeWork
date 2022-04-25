package com.netology.nework.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.netology.nework.dao.EventDao
import com.netology.nework.dao.JobDao
import com.netology.nework.dao.PostDao
import com.netology.nework.dao.UserDao
import com.netology.nework.entity.EventEntity
import com.netology.nework.entity.JobEntity
import com.netology.nework.entity.PostEntity
import com.netology.nework.entity.UserEntity

@Database(entities = [PostEntity::class, JobEntity::class, EventEntity::class, UserEntity::class], version = 7)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun jobDao(): JobDao
    abstract fun eventDao(): EventDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}