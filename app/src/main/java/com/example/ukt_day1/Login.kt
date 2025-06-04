package com.example.ukt_day1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.ukt_day1.api.apiClient
import com.example.ukt_day1.api.sessionManager
import com.example.ukt_day1.request.LoginRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var googleLoginButton: CardView
    private lateinit var githubLoginButton: CardView
    private lateinit var forgotPasswordText: TextView
    private lateinit var sessionManager: sessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI components
        emailEditText = findViewById(R.id.email_login)
        passwordEditText = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.BtnLogin)
        googleLoginButton = findViewById(R.id.googleLoginButton)
        githubLoginButton = findViewById(R.id.githubLoginButton)
        forgotPasswordText = findViewById(R.id.tvForgotPassword)
        sessionManager = sessionManager(this)

        // Setup click listeners
        loginButton.setOnClickListener {
            // Get user input
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                // Show loading state
                loginButton.isEnabled = false
                loginButton.text = "PROCESSING..."

                // Proceed with login
                login(email, password)
            }
        }

        googleLoginButton.setOnClickListener {
            // Social login implementation would go here
            Toast.makeText(this, "Google login fitur akan segera hadir", Toast.LENGTH_SHORT).show()
        }

        githubLoginButton.setOnClickListener {
            // Social login implementation would go here
            Toast.makeText(this, "GitHub login fitur akan segera hadir", Toast.LENGTH_SHORT).show()
        }

        forgotPasswordText.setOnClickListener {
            // Forgot password implementation would go here
            Toast.makeText(this, "Fitur lupa password akan segera hadir", Toast.LENGTH_SHORT).show()
        }

        // Setup register text with clickable span
        setupRegisterSpan()
    }

    private fun setupRegisterSpan() {
        val tv = findViewById<TextView>(R.id.tvNoAccount)
        val text = "Don't have an account? Register"
        val spannable = SpannableString(text)

        val start = text.indexOf("Register")
        val end = start + "Register".length

        val clickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@Login, MainActivity::class.java))
                finish()
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = resources.getColor(R.color.colorPrimary)
                ds.isFakeBoldText = true
            }
        }

        spannable.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv.text = spannable
        tv.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun login(email: String, password: String) {
        val request = LoginRequest(email, password)

        lifecycleScope.launch(Dispatchers.IO) { //kerja tak dibayar
            try {
                val response = apiClient.authService.login(request)

                withContext(Dispatchers.Main) {
                    // Reset button state
                    loginButton.isEnabled = true
                    loginButton.text = "LOGIN"

                    if (response.isSuccessful && response.body() != null) {
                        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("Pesan", response.body()?.message)
                            putString("akses", response.body()?.accessToken)
                            putString("refresh", response.body()?.refreshToken)
                            apply()
                        }

                        sessionManager.saveAuthToken(response.body()?.accessToken ?: "")
                        sessionManager.saveRefreshToken(response.body()?.refreshToken ?: "")

                        Toast.makeText(this@Login, "Login sukses", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Login, Home::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@Login,
                            "Login gagal: " + (response.errorBody()?.string() ?: "Unknown error"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    loginButton.isEnabled = true
                    loginButton.text = "LOGIN"

                    Toast.makeText(
                        this@Login,
                        "Terjadi kesalahan: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}



//package com.example.ukt_day1
//
//import android.content.Intent
//import android.graphics.Color
//import android.os.Bundle
//import android.text.SpannableString
//import android.text.Spanned
//import android.text.method.LinkMovementMethod
//import android.text.style.ClickableSpan
//import android.view.View
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.cardview.widget.CardView
//import com.example.ukt_day1.api.apiClient
//import com.example.ukt_day1.api.sessionManager
//import com.example.ukt_day1.request.LoginRequest
//import com.google.android.material.button.MaterialButton
//import com.google.android.material.textfield.TextInputEditText
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//class Login : AppCompatActivity() {
//    private lateinit var emailEditText: TextInputEditText   // Input email dari user
//    private lateinit var passwordEditText: TextInputEditText // Input password dari user
//    private lateinit var loginButton: MaterialButton         // Tombol login utama
//    private lateinit var googleLoginButton: CardView         // Tombol login Google (sementara placeholder)
//    private lateinit var githubLoginButton: CardView         // Tombol login GitHub (sementara placeholder)
//    private lateinit var forgotPasswordText: TextView        // Text untuk lupa password
//    private lateinit var sessionManager: sessionManager      // Untuk simpan token login di session
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login) // Menghubungkan activity dengan layout XML
//
//        // Menghubungkan variabel dengan elemen UI di layout
//        emailEditText = findViewById(R.id.email_login)
//        passwordEditText = findViewById(R.id.login_password)
//        loginButton = findViewById(R.id.BtnLogin)
//        googleLoginButton = findViewById(R.id.googleLoginButton)
//        githubLoginButton = findViewById(R.id.githubLoginButton)
//        forgotPasswordText = findViewById(R.id.tvForgotPassword)
//        sessionManager = sessionManager(this) // Inisialisasi sessionManager dengan konteks activity
//
//        // Listener ketika tombol login ditekan
//        loginButton.setOnClickListener {
//            val email = emailEditText.text.toString().trim()      // Ambil input email dan hapus spasi
//            val password = passwordEditText.text.toString().trim()// Ambil input password dan hapus spasi
//
//            if (email.isEmpty() || password.isEmpty()) {
//                // Jika email atau password kosong, tampilkan pesan error
//                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
//            } else {
//                // Jika valid, disable tombol supaya user tidak klik berulang dan ubah teks tombol
//                loginButton.isEnabled = false
//                loginButton.text = "PROCESSING..."
//
//                // Panggil fungsi login dengan input email dan password
//                login(email, password)
//            }
//        }
//
//        // Placeholder untuk fitur login dengan Google
//        googleLoginButton.setOnClickListener {
//            Toast.makeText(this, "Google login fitur akan segera hadir", Toast.LENGTH_SHORT).show()
//        }
//
//        // Placeholder untuk fitur login dengan GitHub
//        githubLoginButton.setOnClickListener {
//            Toast.makeText(this, "GitHub login fitur akan segera hadir", Toast.LENGTH_SHORT).show()
//        }
//
//        // Placeholder untuk fitur lupa password
//        forgotPasswordText.setOnClickListener {
//            Toast.makeText(this, "Fitur lupa password akan segera hadir", Toast.LENGTH_SHORT).show()
//        }
//
//        // Setup teks "Register" yang bisa diklik di bawah login form
//        setupRegisterSpan()
//    }
//
//    // Membuat teks "Don't have an account? Register" dimana "Register" bisa diklik
//    private fun setupRegisterSpan() {
//        val tv = findViewById<TextView>(R.id.tvNoAccount)
//        val text = "Don't have an account? Register"
//        val spannable = SpannableString(text)
//
//        val start = text.indexOf("Register") // Cari posisi awal kata "Register"
//        val end = start + "Register".length  // Cari posisi akhir kata "Register"
//
//        val clickable = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                // Saat "Register" diklik, pindah ke MainActivity (halaman register)
//                startActivity(Intent(this@Login, MainActivity::class.java))
//                finish() // Tutup activity login agar tidak bisa kembali ke sini dengan back button
//            }
//            override fun updateDrawState(ds: android.text.TextPaint) {
//                super.updateDrawState(ds)
//                ds.isUnderlineText = false         // Hilangkan garis bawah pada teks "Register"
//                ds.color = resources.getColor(R.color.colorPrimary) // Warna teks sesuai tema utama app
//                ds.isFakeBoldText = true           // Teks dibuat tebal agar menonjol
//            }
//        }
//
//        spannable.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        tv.text = spannable
//        tv.movementMethod = LinkMovementMethod.getInstance() // Agar link bisa diklik
//    }
//
//    // Fungsi login, panggil API dengan email dan password
//    private fun login(email: String, password: String) {
//        val request = LoginRequest(email, password) // Buat request body sesuai API
//
//        CoroutineScope(Dispatchers.IO).launch { // Jalankan proses login di thread background supaya UI tidak ngehang
//            try {
//                val response = apiClient.authService.login(request) // Panggil API login
//
//                runOnUiThread {
//                    // Kembalikan tombol ke keadaan semula walaupun gagal atau sukses
//                    loginButton.isEnabled = true
//                    loginButton.text = "LOGIN"
//
//                    if (response.isSuccessful && response.body() != null) {
//                        // Simpan data token dan pesan dari response ke SharedPreferences
//                        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
//                        with(sharedPreferences.edit()) {
//                            putString("Pesan", response.body()?.message)          // Pesan sukses dari server
//                            putString("akses", response.body()?.accessToken)      // Access token
//                            putString("refresh", response.body()?.refreshToken)   // Refresh token
//                            apply() // Simpan perubahan
//                        }
//
//                        // Simpan token juga di sessionManager agar mudah diakses di seluruh app
//                        sessionManager.saveAuthToken(response.body()?.accessToken ?: "")
//                        sessionManager.saveRefreshToken(response.body()?.refreshToken ?: "")
//
//                        Toast.makeText(this@Login, "Login sukses", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this@Login, Home::class.java)) // Pindah ke halaman Home setelah login sukses
//                        finish() // Tutup activity login agar tidak bisa kembali
//                    } else {
//                        // Jika login gagal, tampilkan pesan error dari server atau unknown error
//                        Toast.makeText(this@Login, "Login gagal: " + (response.errorBody()?.string() ?: "Unknown error"), Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } catch (e: Exception) {
//                runOnUiThread {
//                    // Jika terjadi error saat request (misal koneksi gagal), tampilkan pesan error dan kembalikan tombol
//                    loginButton.isEnabled = true
//                    loginButton.text = "LOGIN"
//
//                    Toast.makeText(this@Login, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//}
