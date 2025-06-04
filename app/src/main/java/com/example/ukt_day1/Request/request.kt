package com.example.ukt_day1.request

import com.google.gson.annotations.SerializedName

// === Digunakan untuk login ===
data class LoginRequest(
    val email: String,       // Email user
    val password: String     // Password user
)

// === Digunakan untuk registrasi akun ===
data class RegisterRequest(
    @SerializedName("name") val name: String,  // Nama lengkap user (gunakan SerializedName biar sinkron dengan backend)
    val email: String,                         // Email user
    val password: String,                      // Password user
    val phone: String                          // Nomor telepon user
)

// === Digunakan untuk permintaan refresh token ===
data class RefreshTokenRequest(
    val refreshToken: String  // Token refresh dari server yang akan dikirim untuk dapetin accessToken baru
)

// === Digunakan untuk permintaan EDIT data buku ===
data class EditBookRequest(
    val isbn: String,          // ISBN buku
    val title: String,         // Judul buku
    val author: String,        // Nama penulis
    val publisher: String,     // Nama penerbit
    val published_date: String,// Tanggal terbit (format: yyyy-mm-dd)
    val genre: String,         // Genre buku
    val language: String,      // Bahasa
    val description: String    // Deskripsi buku
)

// === Digunakan untuk permintaan TAMBAH (POST) data buku ===
data class PostBookRequest(
    val isbn: String,
    val title: String,
    val author: String,
    val publisher: String,
    val published_date: String,
    val genre: String,
    val language: String,
    val description: String
)
