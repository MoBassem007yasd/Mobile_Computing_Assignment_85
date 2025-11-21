package com.example.mobile_computing_assignment_85

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookmarkDao {

    @Insert
    suspend fun insertBookmark(bookmark: Bookmark)

    @Query("SELECT * FROM Bookmark_Datatable")
    suspend fun getAllBookmarks(): List<Bookmark>

    @Query("SELECT * FROM Bookmark_Datatable WHERE category = :categoryName")
    fun getBookmarksByCategory(categoryName: String): List<Bookmark>
}