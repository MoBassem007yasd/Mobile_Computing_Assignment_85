package com.example.mobile_computing_assignment_85
import androidx.room.*

@Dao
interface BookmarkDao {

    @Insert
    suspend fun insertBookmark(bookmark: Bookmark)

    @Query("SELECT * FROM bookmark_Datatable")
    suspend fun getAllBookmarks(): List<Bookmark>
    @Query("SELECT * FROM bookmark_Datatable WHERE category = :categoryName")
    fun getBookmarksByCategory(categoryName: String): List<Bookmark>
}