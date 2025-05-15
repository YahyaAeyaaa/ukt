package com.example.ukt_day1

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.ukt_day1.api.apiClient
import com.example.ukt_day1.request.LoginRequest
import com.example.ukt_day1.api.sessionManager

class Login : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: View
    private lateinit var sessionManager: sessionManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.LoginEmail)
        passwordEditText = findViewById(R.id.LoginPassword)
        loginButton = findViewById(R.id.BtnLogin)
        sessionManager = sessionManager(this)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                login(email, password)
            }
        }

        setupRegisterSpan()
    }

    private fun setupRegisterSpan() {
        val tv = findViewById<TextView>(R.id.tvDontHaveAccount)
        val text = "Don't have an account? Register"
        val spannable = SpannableString(text)

        val start = text.indexOf("Register")
        val end   = start + "Register".length

        val clickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@Login, MainActivity::class.java))
                finish()
            }
            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.BLUE
            }
        }

        spannable.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv.text = spannable
        tv.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun login(email: String, password: String) {
        val request = LoginRequest(email, password)

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiClient.authService.login(request)

            runOnUiThread {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val sharedPreferences =
                            getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("Pesan", it.message)
                            putString("akses", it.accessToken)
                            putString("refresh", it.refreshToken)
                            apply()
                        }

                        sessionManager.saveAuthToken(it.accessToken ?: "")
                        sessionManager.saveRefreshToken(it.refreshToken ?: "")

                        Toast.makeText(this@Login, "Login sukses", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Login, Home::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this@Login, "Login gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

