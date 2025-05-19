package com.example.ukt_day1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ukt_day1.api.apiClient
import com.example.ukt_day1.request.RefreshTokenRequest
import com.example.ukt_day1.ui.UserAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class Home : AppCompatActivity() {
    private lateinit var logoutButton: Button
    private lateinit var rvUsers: RecyclerView
    private lateinit var adapter: UserAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val checkTokenInterval = TimeUnit.MINUTES.toMillis(1)  // ubah jadi 1 menit supaya lebih efisien

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        logoutButton = findViewById(R.id.logoutButton)
        rvUsers = findViewById(R.id.rvUsers)

        adapter = UserAdapter(
            mutableListOf(),
            onEdit = { user ->
                val intent = Intent(this, Put::class.java).apply {
                    putExtra("book_id", user.id.toString())
                    putExtra("isbn", user.isbn)
                    putExtra("title", user.title)
                    putExtra("author", user.author)
                    putExtra("publisher", user.publisher)
                    putExtra("published_date", user.published_date)
                    putExtra("genre", user.genre)
                    putExtra("language", user.language)
                    putExtra("description", user.description)
                }
                startActivity(intent)
            },
            onDelete = { user -> deleteBook(user.id) }
        )

        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter

        logoutButton.setOnClickListener { logoutAndRedirectToLogin() }

        startTokenChecker()
        fetchAllBooks()
    }

    override fun onResume() {
        super.onResume()
        fetchAllBooks()  // refresh data setiap kali balik ke Home
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // bersihkan handler biar gak memory leak
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
                val exp = JSONObject(payload).getLong("exp") * 1000
                System.currentTimeMillis() > exp
            } else true
        } catch (e: Exception) {
            true
        }
    }

    private fun startTokenChecker() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                checkAndRefreshToken()
                handler.postDelayed(this, checkTokenInterval)
            }
        }, checkTokenInterval)
    }

    private fun checkAndRefreshToken() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val access = prefs.getString("akses", null)
        val refresh = prefs.getString("refresh", null)

        if (!access.isNullOrEmpty() && isTokenExpired(access)) {
            if (!refresh.isNullOrEmpty() && !isTokenExpired(refresh)) {
                refreshAccessToken(refresh)
            } else {
                logoutAndRedirectToLogin()
            }
        }
    }

    private fun refreshAccessToken(refreshToken: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiClient.authService.refreshToken(RefreshTokenRequest(refreshToken))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val newToken = response.body()?.accessToken ?: return@withContext
                        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        prefs.edit().putString("akses", newToken).apply()
                    } else {
                        Toast.makeText(this@Home, "Refresh token gagal", Toast.LENGTH_SHORT).show()
                        logoutAndRedirectToLogin()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    logoutAndRedirectToLogin()
                }
            }
        }
    }

    private fun logoutAndRedirectToLogin() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val token = prefs.getString("akses", null)

        lifecycleScope.launch(Dispatchers.IO) {
            val message = if (!token.isNullOrEmpty()) {
                try {
                    val res = apiClient.authService.logout("Bearer $token")
                    if (res.isSuccessful) "Logout berhasil" else "Logout gagal di server"
                } catch (e: Exception) {
                    "Gagal logout: masalah jaringan"
                }
            } else {
                "Langsung logout, token tidak ditemukan"
            }

            withContext(Dispatchers.Main) {
                prefs.edit().clear().apply()
                Toast.makeText(this@Home, message, Toast.LENGTH_SHORT).show()
                handler.removeCallbacksAndMessages(null)
                startActivity(Intent(this@Home, Login::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
        }
    }

    private fun fetchAllBooks() {
        val token = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("akses", null) ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiClient.authService.getBooks("Bearer $token")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val bookList = response.body()?.data?.books
                        if (bookList != null) {
                            adapter.setData(bookList)
                        } else {
                            Toast.makeText(this@Home, "Data buku kosong", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Home, "Gagal mengambil data buku", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Home, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteBook(id: Int) {
        val token = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("akses", null) ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiClient.authService.deleteBook("Bearer $token", id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@Home, "Buku berhasil dihapus", Toast.LENGTH_SHORT).show()
                        fetchAllBooks()
                    } else {
                        Toast.makeText(this@Home, "Gagal menghapus buku", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Home, "Kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
