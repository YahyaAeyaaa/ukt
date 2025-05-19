package com.example.ukt_day1.api

import com.example.ukt_day1.request.EditBookRequest
import com.example.ukt_day1.request.LoginRequest
import com.example.ukt_day1.request.RegisterRequest
import com.example.ukt_day1.request.RefreshTokenRequest
import com.example.ukt_day1.response.BooksResponse
import com.example.ukt_day1.response.EditResponse
import com.example.ukt_day1.response.LoginResponse
import com.example.ukt_day1.response.RegisterResponse
import com.example.ukt_day1.response.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path


interface AuthService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Void>

    @GET("books?page={page}&limit={limit}")
    suspend fun getBooks(@Header("Authorization") token: String): Response<BooksResponse>

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
        @Body request: EditBookRequest
    ): Response<EditResponse>
}

