package com.example.ukt_day1

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ukt_day1.api.apiClient
import com.example.ukt_day1.api.sessionManager
import com.example.ukt_day1.databinding.PostBinding
import com.example.ukt_day1.request.PostBookRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Post : AppCompatActivity() {

    private lateinit var binding: PostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Buat "Cancel" menjadi clickable dengan SpannableString
        val span = SpannableString("Batal")
        span.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Batal posting, kembalikan RESULT_CANCELED
                setResult(RESULT_CANCELED)
                finish()
            }
            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.RED
            }
        }, 0, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvCancelPost.text = span
        binding.tvCancelPost.movementMethod = LinkMovementMethod.getInstance()

        // Set up post button
        binding.btnPost.setOnClickListener { createBook() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(RESULT_CANCELED)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createBook() {
        // Mengambil data dari TextInputEditText
        val isbn = binding.etPostIsbn.text.toString().trim()
        val title = binding.etPostTitle.text.toString().trim()
        val author = binding.etPostAuthor.text.toString().trim()
        val publisher = binding.etPostPublisher.text.toString().trim()
        val pubDate = binding.etPostPublishedDate.text.toString().trim()
        val genre = binding.etPostGenre.text.toString().trim()
        val language = binding.etPostLanguage.text.toString().trim()
        val desc = binding.etPostDescription.text.toString().trim()

        // Validasi input
        if (listOf(isbn, title, author, publisher, pubDate, genre, language, desc).any { it.isEmpty() }) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Menampilkan loading state
        binding.btnPost.isEnabled = false
        binding.btnPost.text = "Menyimpan..."

        val request = PostBookRequest(isbn, title, author, publisher, pubDate, genre, language, desc)
        val token = "Bearer ${sessionManager(this).getAuthToken()}"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resp = apiClient.authService.createBook(token, request)
                withContext(Dispatchers.Main) {
                    binding.btnPost.isEnabled = true
                    binding.btnPost.text = "Simpan"

                    if (resp.isSuccessful) {
                        val body = resp.body()
                        Toast.makeText(
                            this@Post,
                            "Buku berhasil ditambahkan: ${body?.data?.title}",
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(
                            this@Post,
                            "Gagal: kode ${resp.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.btnPost.isEnabled = true
                    binding.btnPost.text = "Simpan"

                    Toast.makeText(
                        this@Post,
                        "Error jaringan: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}