

package com.example.ukt_day1.response

import com.google.gson.annotations.SerializedName

// === Untuk response login ===
data class LoginResponse(
    val message: String?,         // Pesan dari server (misal: "Login success")
    val accessToken: String?,     // Token akses buat autentikasi
    val refreshToken: String?     // Token penyegar jika accessToken expired
)

// === Untuk response register ===
data class RegisterResponse(
    val userId : Int,             // ID user yang baru dibuat
    val message: String?          // Pesan dari server
)

// === Untuk response refresh token ===
data class RefreshTokenResponse(
    val message: String?,         // Biasanya pesan "Token refreshed"
    val accessToken: String?      // Token baru yang bisa dipakai
)



data class BooksResponse(
    val status: String,           // Contoh: "success"
    val message: String,          // Pesan tambahan
    val data: BookList            // Data utama berupa list buku
)

data class BookList(
    val books: List<BookItem>     // Daftar semua buku
)

data class BookItem(
    val id: Int,                  // ID unik buku
    val isbn: String,
    val title: String,
    val author: String,
    val publisher: String,
    val published_date: String,
    val genre: String,
    val language: String,
    val description: String,
    val cover_image: Size,        // Ukuran gambar cover (small, medium, large)
    @SerializedName("uploaded_by")
    val uploadedBy: String        // Nama atau ID pengunggah buku
)

data class Size(
    val small: String,            // URL gambar ukuran kecil
    val medium: String,           // URL ukuran sedang
    val large: String             // URL ukuran besar
)

data class EditResponse(
    val status: String,           // Contoh: "success"
    val message: String,          // Pesan dari server (misal: "Book updated")
    val data: EditItem            // Data buku setelah diupdate
)

data class EditItem(
    val id: String,
    val title: String,
    val author: String
)

//Post Data Library

data class PostResponse(
    val status: String,
    val message: String,
    val data: PostItem            // Data buku yang baru ditambahkan
)

data class PostItem(
    val id: String,
    val title: String,
    val author: String
)



//Get book by id

data class BookByIdResponse(
    val status: String,           // Contoh: "success"
    val message: String,          // Misalnya: "Book found"
    val data: BookDetail          // Detail lengkap dari buku yang dipilih
)

data class BookDetail(
    val id: String,
    val isbn: String,
    val title: String,
    val author: String,
    val publisher: String,
    val published_date: String,
    val genre: String,
    val language: String,
    val description: String,
    val cover_image: Size,        // Gambar cover lengkap
    @SerializedName("uploaded_by") val uploadedBy: String
)

