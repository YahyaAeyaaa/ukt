package com.example.ukt_day1.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)


data class RegisterRequest(
    @SerializedName("name") val name: String,
    val email: String,
    val password: String,
    val phone: String
)


data class RefreshTokenRequest(
    val refreshToken: String
)
