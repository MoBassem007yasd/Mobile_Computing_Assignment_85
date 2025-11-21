package com.example.mobile_computing_assignment_85

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Bookmark::class],
    // Database version.
    version = 1,
    exportSchema = false
)
abstract class BookmarkDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        private var INSTANCE: BookmarkDatabase? = null
        fun getInstance(context: Context): BookmarkDatabase {
            return INSTANCE ?: synchronized(lock = this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BookmarkDatabase::class.java,
                    name = "bookmark_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}