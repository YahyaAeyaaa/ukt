package com.example.ukt_day1.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Singleton object untuk inisialisasi Retrofit client
object apiClient {
    // Base URL dari server backend yang kamu pakai
    private const val BASE_URL = "https://minilibrary-s8ph.onrender.com/"

    // Inisialisasi OkHttpClient dengan timeout
    // Timeout ini berguna supaya aplikasi tidak langsung crash saat koneksi lambat atau server lambat respon
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Waktu maksimal untuk mencoba koneksi ke server
        .readTimeout(60, TimeUnit.SECONDS)    // Waktu maksimal untuk membaca respons dari server
        .writeTimeout(60, TimeUnit.SECONDS)   // Waktu maksimal untuk mengirim request ke server
        .build()

    // Inisialisasi Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)                    // Set base URL API
        .client(client)                       // Pakai OkHttpClient custom yang kita set di atas
        .addConverterFactory(GsonConverterFactory.create()) // Gunakan GSON buat convert JSON <-> Object
        .build()

    // Instance dari AuthService (interface untuk endpoint API), nanti bisa langsung dipanggil
    val authService: AuthService = retrofit.create(AuthService::class.java)
}
