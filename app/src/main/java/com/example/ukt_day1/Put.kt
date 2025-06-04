package com.example.ukt_day1

import android.app.DatePickerDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ukt_day1.api.apiClient
import com.example.ukt_day1.api.sessionManager
import com.example.ukt_day1.request.EditBookRequest
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class Put : AppCompatActivity() {

    // UI Components
    private lateinit var editIsbn: TextInputEditText
    private lateinit var editTitle: TextInputEditText
    private lateinit var editAuthor: TextInputEditText
    private lateinit var editPublisher: TextInputEditText
    private lateinit var editPublishedDate: TextInputEditText
    private lateinit var editGenre: TextInputEditText
    private lateinit var editLanguage: TextInputEditText
    private lateinit var editDescription: TextInputEditText
    private lateinit var btnUpdate: Button
    private lateinit var loadingOverlay: FrameLayout
    private lateinit var ivBookCover: ImageView
    private lateinit var tvChangeImage: TextView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    // Data
    private var bookId: String? = null
    private var coverImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update)

        // Initialize UI components
        initViews()
        setupListeners()
        populateBookData()
    }

    private fun initViews() {
        // Find views by ID
        editIsbn = findViewById(R.id.editIsbn)
        editTitle = findViewById(R.id.editTitle)
        editAuthor = findViewById(R.id.editAuthor)
        editPublisher = findViewById(R.id.editPublisher)
        editPublishedDate = findViewById(R.id.editPublishedDate)
        editGenre = findViewById(R.id.editGenre)
        editLanguage = findViewById(R.id.editLanguage)
        editDescription = findViewById(R.id.editDescription)
        btnUpdate = findViewById(R.id.btnUpdate)
        loadingOverlay = findViewById(R.id.loadingOverlay)
        ivBookCover = findViewById(R.id.ivBookCover)
        tvChangeImage = findViewById(R.id.tvChangeImage)
        toolbar = findViewById(R.id.toolbar)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupListeners() {
        // Update button click listener
        btnUpdate.setOnClickListener {
            if (validateInputs()) {
                updateBook()
            }
        }

        // Date picker for published date
        editPublishedDate.setOnClickListener {
            showDatePicker()
        }

        // Find the parent TextInputLayout
        val publishedDateLayout = editPublishedDate.parent.parent as? TextInputLayout
        publishedDateLayout?.setEndIconOnClickListener {
            showDatePicker()
        }

        // Change cover image option
        tvChangeImage.setOnClickListener {
            // You can implement image selection here
            // For now, just show a message
            Snackbar.make(
                findViewById(android.R.id.content),
                "Image upload will be implemented in future version",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun populateBookData() {
        // Get book data from intent
        val intent = intent
        bookId = intent.getStringExtra("book_id")
        coverImageUrl = intent.getStringExtra("cover_image")

        // Populate fields with existing data
        editIsbn.setText(intent.getStringExtra("isbn"))
        editTitle.setText(intent.getStringExtra("title"))
        editAuthor.setText(intent.getStringExtra("author"))
        editPublisher.setText(intent.getStringExtra("publisher"))
        editPublishedDate.setText(intent.getStringExtra("published_date"))
        editGenre.setText(intent.getStringExtra("genre"))
        editLanguage.setText(intent.getStringExtra("language"))
        editDescription.setText(intent.getStringExtra("description"))

        // Load book cover image
        loadBookCover(coverImageUrl)
    }

    private fun loadBookCover(imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.no_image_placeholder_svg)
                .error(R.drawable.no_image_placeholder_svg)
                .into(ivBookCover)
        }
    }


//    Jirr, ini buat popup kalender
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        // Parse existing date if available
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDateStr = editPublishedDate.text.toString()
        if (currentDateStr.isNotEmpty()) {
            try {
                val date = dateFormat.parse(currentDateStr)
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                // Use current date if parsing fails
            }
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val formattedDate = dateFormat.format(calendar.time)
                editPublishedDate.setText(formattedDate)
            },
            year, month, day
        ).show()
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Check if all fields are filled
        val fieldsToCheck = mapOf(
            editIsbn to "ISBN",
            editTitle to "Title",
            editAuthor to "Author",
            editPublisher to "Publisher",
            editPublishedDate to "Published date",
            editGenre to "Genre",
            editLanguage to "Language",
            editDescription to "Description"
        )

        for ((field, fieldName) in fieldsToCheck) {
            if (field.text.toString().isBlank()) {
                field.error = "$fieldName is required"
                isValid = false
            } else {
                field.error = null
            }
        }

        return isValid
    }

    private fun updateBook() {
        // Show loading indicator
        loadingOverlay.visibility = View.VISIBLE

        // Get values from input fields
        val isbn = editIsbn.text.toString()
        val title = editTitle.text.toString()
        val author = editAuthor.text.toString()
        val publisher = editPublisher.text.toString()
        val publishedDate = editPublishedDate.text.toString()
        val genre = editGenre.text.toString()
        val language = editLanguage.text.toString()
        val description = editDescription.text.toString()

        // Create request object
        val request = EditBookRequest(
            isbn = isbn,
            title = title,
            author = author,
            publisher = publisher,
            published_date = publishedDate,
            genre = genre,
            language = language,
            description = description
        )

        // Get authentication token
        val token = "Bearer ${sessionManager(this).getAuthToken()}"

        // Convert bookId to integer
        val id = bookId?.toIntOrNull()
        if (id == null) {
            showError("Invalid book ID")
            loadingOverlay.visibility = View.GONE
            return
        }

        // Call API
        lifecycleScope.launch {
            try {
                val response = apiClient.authService.editBook(id, token, request)

                // Hide loading indicator
                loadingOverlay.visibility = View.GONE

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.status == "success") {
                        showSuccess("Book updated successfully")
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        showError("Update failed: ${responseBody?.status ?: "Empty response"}")
                    }
                } else {
                    showError("Error: ${response.message() ?: response.code()}")
                }
            } catch (e: Exception) {
                // Hide loading indicator
                loadingOverlay.visibility = View.GONE
                showError("Network error: ${e.message}")
            }
        }
    }

    private fun showSuccess(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showError(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        ).setBackgroundTint(resources.getColor(android.R.color.holo_red_dark))
            .setTextColor(resources.getColor(android.R.color.white))
            .show()
    }

    override fun onBackPressed() {
        // Ask for confirmation before discarding changes
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Discard Changes")
            .setMessage("Are you sure you want to discard your changes?")
            .setPositiveButton("Discard") { _, _ -> super.onBackPressed() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}