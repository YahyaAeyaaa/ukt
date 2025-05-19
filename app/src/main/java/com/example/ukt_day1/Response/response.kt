package com.example.ukt_day1.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val message: String?,
    val accessToken: String?,
    val refreshToken: String?
)

data class RegisterResponse(
    val userId : Int,
    val message: String?
)

data class RefreshTokenResponse(
    val message: String?,
    val accessToken: String?
)

data class BooksResponse(
    val status: String,
    val message: String,
    val data: BookList
)

data class BookList(
    val books: List<BookItem>
)

data class BookItem(
    val id: Int,
    val isbn: String,
    val title: String,
    val author: String,
    val publisher: String,
    val published_date: String,
    val genre: String,
    val language: String,
    val description: String,
    val cover_image: Size,
    @SerializedName("uploaded_by")
    val uploadedBy: String
)

data class Size(
    val small: String,
    val medium: String,
    val large: String
)

data class EditResponse(
    val status: String,
    val message: String,
    val data: EditItem
)

data class EditItem(
    val id: String,
    val title: String,
    val author: String
)

