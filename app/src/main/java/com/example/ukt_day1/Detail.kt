package com.example.ukt_day1

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.ukt_day1.api.apiClient
import com.example.ukt_day1.api.sessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Detail : AppCompatActivity() {

    private lateinit var ivCoverDetail: ImageView
    private lateinit var tvTitleDetail: TextView
    private lateinit var tvAuthorDetail: TextView
    private lateinit var tvPublisherDetail: TextView
    private lateinit var tvIsbnDetail: TextView
    private lateinit var tvPublishedDateDetail: TextView
    private lateinit var tvGenreDetail: TextView
    private lateinit var tvLanguageDetail: TextView
    private lateinit var tvDescriptionDetail: TextView
    private lateinit var btnBack: android.widget.ImageButton

    private lateinit var sessionManager: sessionManager
    private var bookId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Inisialisasi UI components
        initUI()

        // Inisialisasi session manager
        sessionManager = sessionManager(this)

        // Ambil book ID dari intent
        bookId = intent.getIntExtra("BOOK_ID", 0)

        if (bookId != 0) {
            fetchBookDetail(bookId)
        } else {
            Toast.makeText(this, "ID buku tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initUI() {
        ivCoverDetail = findViewById(R.id.ivCoverDetail)
        tvTitleDetail = findViewById(R.id.tvTitleDetail)
        tvAuthorDetail = findViewById(R.id.tvAuthorDetail)
        tvPublisherDetail = findViewById(R.id.tvPublisherDetail)
        tvIsbnDetail = findViewById(R.id.tvIsbnDetail)
        tvPublishedDateDetail = findViewById(R.id.tvPublishedDateDetail)
        tvGenreDetail = findViewById(R.id.tvGenreDetail)
        tvLanguageDetail = findViewById(R.id.tvLanguageDetail)
        tvDescriptionDetail = findViewById(R.id.tvDescriptionDetail)
        btnBack = findViewById(R.id.btnBack)

        // Setup back button listener
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchBookDetail(id: Int) {
        val token = sessionManager.getAuthToken()

        if (token == null) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiClient.authService.getBookById("Bearer $token", id)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val bookDetail = response.body()?.data
                        if (bookDetail != null) {
                            displayBookDetail(bookDetail)
                        } else {
                            Toast.makeText(this@Detail, "Data buku tidak ditemukan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        when (response.code()) {
                            401 -> {
                                Toast.makeText(this@Detail, "Sesi berakhir, silakan login kembali", Toast.LENGTH_SHORT).show()
                                sessionManager.clearSession()
                                finish()
                            }
                            404 -> {
                                Toast.makeText(this@Detail, "Buku tidak ditemukan", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            else -> {
                                Toast.makeText(this@Detail, "Gagal mengambil detail buku", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Detail, "Terjadi kesalahan jaringan: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun displayBookDetail(bookDetail: com.example.ukt_day1.response.BookDetail) {
        // Load cover image dengan Glide
        Glide.with(this)
            .load(bookDetail.cover_image.large)
            .apply(RequestOptions().transform(RoundedCorners(16)))
            .placeholder(R.drawable.no_image_placeholder_svg)
            .error(R.drawable.no_image_placeholder_svg)
            .into(ivCoverDetail)

        // Set text untuk semua field
        tvTitleDetail.text = bookDetail.title
        tvAuthorDetail.text = "By ${bookDetail.author}"
        tvPublisherDetail.text = "Publisher: ${bookDetail.publisher}"
        tvIsbnDetail.text = "ISBN: ${bookDetail.isbn}"
        tvPublishedDateDetail.text = "Published Date: ${bookDetail.published_date}"
        tvGenreDetail.text = bookDetail.genre
        tvLanguageDetail.text = "Language: ${bookDetail.language}"
        tvDescriptionDetail.text = bookDetail.description
    }
}