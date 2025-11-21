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

class MainActivity : AppCompatActivity() {
    private lateinit var titleInput: EditText
    private lateinit var linkInput: EditText
    private lateinit var categoryInput: EditText
    private lateinit var addBookmark: Button
    private lateinit var filter: Button
    private lateinit var showAll: Button
    private lateinit var spinner: Spinner
    private lateinit var dbList: ListView
    private lateinit var bookmarkDatabase: BookmarkDatabase
    private var bookmarks: MutableList<Bookmark> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        titleInput = findViewById(R.id.TitleInput)
        linkInput = findViewById(R.id.LinkInput)
        categoryInput = findViewById(R.id.CategoryInput)
        addBookmark = findViewById(R.id.AddBookmark)
        filter = findViewById(R.id.Filter)
        showAll = findViewById(R.id.ShowAll)
        dbList = findViewById(R.id.DBList)
        spinner = findViewById(R.id.Spinner)

        bookmarkDatabase = BookmarkDatabase.getInstance(this)

        addBookmark.setOnClickListener {
            saveBookmark()
        }

        dbList.setOnItemClickListener { _, _, position, _ ->
            val selectedBookmark = bookmarks[position]
            openLink(selectedBookmark.url)
        }

        filter.setOnClickListener {
            val selectedCategory = spinner.selectedItem.toString()
            if (selectedCategory == "All") {
                loadBookmarks()
            } else {
                filterBookmarks(selectedCategory)
            }
        }

        showAll.setOnClickListener {
            loadBookmarks()
        }

        loadBookmarks()
    }

    private fun saveBookmark() {
        val title = titleInput.text.toString().trim()
        val url = linkInput.text.toString().trim()
        val category = categoryInput.text.toString().trim()

        if (title.isEmpty() || url.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all inputs", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val newBookmark = Bookmark(title = title, url = url, category = category)
            bookmarkDatabase.bookmarkDao().insertBookmark(newBookmark)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Bookmark saved!", Toast.LENGTH_SHORT).show()
                titleInput.text.clear()
                linkInput.text.clear()
                categoryInput.text.clear()
                loadBookmarks()
            }
        }
    }

    private fun loadBookmarks() {
        lifecycleScope.launch(Dispatchers.IO) {
            val loadedBookmarks = bookmarkDatabase.bookmarkDao().getAllBookmarks()
            bookmarks = loadedBookmarks.toMutableList()
            withContext(Dispatchers.Main) {
                updateListView(bookmarks)
                updateCategorySpinner(bookmarks)
            }
        }
    }

    private fun filterBookmarks(categoryName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val filteredBookmarks = bookmarkDatabase.bookmarkDao().getBookmarksByCategory(categoryName)
            bookmarks = filteredBookmarks.toMutableList()
            withContext(Dispatchers.Main) {
                updateListView(bookmarks)
            }
        }
    }

    private fun updateCategorySpinner(bookmarksList: List<Bookmark>) {
        val categories = bookmarksList.map { it.category }.distinct().toMutableList()
        categories.add(0, "All")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun updateListView(bookmarksList: List<Bookmark>) {
        val titles = bookmarksList.map { it.title }.toTypedArray()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            titles
        )
        dbList.adapter = adapter
    }

    private fun openLink(url: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            }
}