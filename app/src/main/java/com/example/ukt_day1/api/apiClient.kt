package com.example.ukt_day1.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object apiClient {
    private const val BASE_URL = "https://minilibrary-s8ph.onrender.com/"

    // Menggunakan OkHttpClient tanpa pengaturan timeout `xkustom
    private val client = OkHttpClient.Builder()
        .build() // Default timeout akan digunakan

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
}
