package com.example.mobile_computing_assignment_85
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Bookmark_Datatable")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val url: String,
    val category: String
)