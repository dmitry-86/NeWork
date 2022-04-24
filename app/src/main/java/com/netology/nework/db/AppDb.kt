package com.netology.nework.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.netology.nework.dao.EventDao
import com.netology.nework.dao.JobDao
import com.netology.nework.dao.PostDao
import com.netology.nework.entity.EventEntity
import com.netology.nework.entity.JobEntity
import com.netology.nework.entity.PostEntity

@Database(entities = [PostEntity::class, JobEntity::class, EventEntity::class], version = 6)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun jobDao(): JobDao
    abstract fun eventDao(): EventDao

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