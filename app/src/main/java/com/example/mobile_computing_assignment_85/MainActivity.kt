package com.example.mobile_computing_assignment_85

import android.os.Bundle
import android.widget.*
import android.content.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.net.Uri
import android.util.Log

class MainActivity : AppCompatActivity() {
    private lateinit var TitleInput: EditText
    private lateinit var LinkInput: EditText
    private lateinit var CategoryInput: EditText
    private lateinit var AddBookmark: Button
    private lateinit var Filter: Button
    private lateinit var ShowAll: Button
    private lateinit var Spinner: Spinner
    private lateinit var DBList: ListView
    private lateinit var BookmarkDatabase: BookmarkDatabase
    private var Bookmarks: MutableList<Bookmark> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        TitleInput = findViewById(R.id.TitleInput)
        LinkInput = findViewById(R.id.LinkInput)
        CategoryInput = findViewById(R.id.CategoryInput)
        AddBookmark = findViewById(R.id.AddBookmark)
        Filter = findViewById(R.id.Filter)
        ShowAll = findViewById(R.id.ShowAll)
        DBList = findViewById(R.id.DBList)
        Spinner = findViewById(R.id.Spinner)
        BookmarkDatabase = BookmarkDatabase.getInstance(this)
        AddBookmark.setOnClickListener {
            addBookmark()
        }
        DBList.setOnItemClickListener { _, _, position, _ ->
            val selectedBookmark = Bookmarks[position]
            openInBrowser(selectedBookmark.url)
        }
        Filter.setOnClickListener {
            val selectedCategory = Spinner.selectedItem.toString()
            if (selectedCategory == "All") {
                loadBookmarks()
            } else {
                filterBookmarks(selectedCategory)
            }
        }

        ShowAll.setOnClickListener {
            loadBookmarks()
        }
        loadBookmarks()
    }
    private fun addBookmark() {
        val title = TitleInput.text.toString().trim()
        val url = LinkInput.text.toString().trim()
        val category = CategoryInput.text.toString().trim()

        if (title.isEmpty() || url.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all inputs", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val newBookmark = Bookmark(title = title, url = url, category = category)
            BookmarkDatabase.bookmarkDao().insertBookmark(newBookmark)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Bookmark saved!", Toast.LENGTH_SHORT).show()
                TitleInput.text.clear()
                LinkInput.text.clear()
                CategoryInput.text.clear()
                loadBookmarks()
            }
        }
    }
    private fun loadBookmarks() {
        lifecycleScope.launch(Dispatchers.IO) {
            val bookmarks = BookmarkDatabase.bookmarkDao().getAllBookmarks()
            Bookmarks = bookmarks.toMutableList()
            withContext(Dispatchers.Main) {
                updateListView(Bookmarks)
                updateCategorySpinner(Bookmarks)
            }
        }
    }

    /**
     * Coroutine function to filter bookmarks by a selected category (Objective 3).
     */
    private fun filterBookmarks(categoryName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Get filtered bookmarks from the database
            val filteredBookmarks = BookmarkDatabase.bookmarkDao().getBookmarksByCategory(categoryName)
            Bookmarks = filteredBookmarks.toMutableList() // Use 'Bookmarks' list

            // Update UI on the Main Thread
            withContext(Dispatchers.Main) {
                updateListView(Bookmarks) // Pass 'Bookmarks' list
            }
        }
    }


    /**
     * Objective 3: Filter by Category - Populates the Spinner.
     */
    private fun updateCategorySpinner(bookmarks: List<Bookmark>) {
        // Extract unique categories (no duplicates)
        val categories = bookmarks.map { it.category }.distinct().toMutableList()
        categories.add(0, "All") // Add a default option at the start

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        Spinner.adapter = adapter // Use 'Spinner' property
    }


    /**
     * Updates the DBList ListView with the provided list of bookmarks (Objective 2).
     */
    private fun updateListView(bookmarks: List<Bookmark>) {
        // Use the title as the display text for the list item (Lab 5, Page 27)
        val titles = bookmarks.map { it.title }.toTypedArray()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, // Built-in layout for simple text
            titles
        )
        DBList.adapter = adapter
    }

    /**
     * Objective 4: Open in Browser - Opens the URL using an implicit Intent.
     */
    private fun openInBrowser(url: String) {
        try {
            // Simple validation to ensure URL starts with a protocol
            val validatedUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                url
            } else {
                "http://$url"
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validatedUrl))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open URL: $url", Toast.LENGTH_LONG).show()
            Log.e("MainActivity", "Error opening browser", e)
        }
    }

    /**
     * Ensures the list is reloaded when returning to the activity (like onRestart in Lab 5, Page 27).
     */
    override fun onResume() {
        super.onResume()
        loadBookmarks()
    }
}