package com.example.ukt_day1.api

import com.example.ukt_day1.request.*
import com.example.ukt_day1.response.*
import retrofit2.Response
import retrofit2.http.*

interface AuthService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Void>

    @GET("books")
    suspend fun getBooks(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 500
    ): Response<BooksResponse>

    @DELETE("books/{id}")
    suspend fun deleteBook(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Void>

    @PUT("books/{id}")
    suspend fun editBook(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body request: EditBookRequest
    ): Response<EditResponse>

    @POST("books")
    suspend fun createBook(
        @Header("Authorization") token: String,
        @Body request: PostBookRequest
    ): Response<PostResponse>

    @GET("books/{id}")
    suspend fun getBookById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BookByIdResponse>
}
