package com.example.ukt_day1

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
import com.example.ukt_day1.api.apiClient
import com.example.ukt_day1.request.RegisterRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var inputName: TextInputEditText
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputPassword: TextInputEditText
    private lateinit var inputPhone: TextInputEditText
    private lateinit var btnRegister: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        inputName = findViewById(R.id.RegisterName)
        inputEmail = findViewById(R.id.RegisterEmail)
        inputPassword = findViewById(R.id.RegisterPassword)
        inputPhone = findViewById(R.id.RegisterPhone)
        btnRegister = findViewById(R.id.BtnRegister)

        // Register button click listener
        btnRegister.setOnClickListener {
            // Get user input
            val name = inputName.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            val phone = inputPhone.text.toString().trim()

            // Validate input
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                // Proceed with registration
                doRegister(name, email, password, phone)
            }
        }

        // Setup login text with clickable span
        setupLoginSpan()
    }

    private fun setupLoginSpan() {
        val tv = findViewById<TextView>(R.id.tvHaveAccount)
        val text = "Have an account? Login"
        val spannable = SpannableString(text)

        val start = text.indexOf("Login")
        val end = start + "Login".length

        val clickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@MainActivity, Login::class.java))
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

    private fun doRegister(name: String, email: String, password: String, phone: String) {
        val req = RegisterRequest(name, email, password, phone)

        // Show loading indicator (if available)
        btnRegister.isEnabled = false
        btnRegister.text = "Processing..."

        CoroutineScope(Dispatchers.IO).launch { // Kerja Paksa
            try {
                val res = apiClient.authService.register(req)
                withContext(Dispatchers.Main) {
                    // Reset button state
                    btnRegister.isEnabled = true
                    btnRegister.text = "REGISTER"

                    if (res.isSuccessful && res.body() != null) {
                        // Save user data
                        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("name", name)
                            .apply()

                        // Show success message
                        Toast.makeText(
                            this@MainActivity,
                            res.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate to login screen
                        startActivity(Intent(this@MainActivity, Login::class.java))
                        finish()
                    } else {
                        // Show error message
                        Toast.makeText(
                            this@MainActivity,
                            "Register gagal: ${res.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Reset button state
                    btnRegister.isEnabled = true
                    btnRegister.text = "REGISTER"

                    // Show error message
                    Toast.makeText(
                        this@MainActivity,
                        "Terjadi kesalahan: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}


//package com.example.ukt_day1 // Paket aplikasi utama
//
//import android.content.Intent // Import untuk berpindah Activity
//import android.graphics.Color
//import android.os.Bundle // Untuk lifecycle Activity
//import android.text.SpannableString // Membuat teks dengan bagian yang bisa diklik
//import android.text.Spanned
//import android.text.method.LinkMovementMethod // Membuat link dalam TextView bisa diklik
//import android.text.style.ClickableSpan // Membuat bagian teks jadi clickable
//import android.view.View
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast // Untuk menampilkan pesan popup singkat
//import androidx.appcompat.app.AppCompatActivity // Activity dengan support library
//import com.example.ukt_day1.api.apiClient // Client Retrofit API
//import com.example.ukt_day1.request.RegisterRequest // Request data register
//import com.google.android.material.button.MaterialButton // Tombol Material Design
//import com.google.android.material.textfield.TextInputEditText // Input text Material Design
//import kotlinx.coroutines.CoroutineScope // Scope coroutine untuk async task
//import kotlinx.coroutines.Dispatchers // Menentukan thread untuk coroutine
//import kotlinx.coroutines.launch // Meluncurkan coroutine
//import kotlinx.coroutines.withContext // Berpindah ke thread lain di coroutine
//
//// MainActivity sebagai layar pendaftaran (register)
//class MainActivity : AppCompatActivity() {
//
//    // Variabel UI yang akan diinisialisasi nanti
//    private lateinit var inputName: TextInputEditText // Input nama pengguna
//    private lateinit var inputEmail: TextInputEditText // Input email pengguna
//    private lateinit var inputPassword: TextInputEditText // Input password pengguna
//    private lateinit var inputPhone: TextInputEditText // Input nomor telepon pengguna
//    private lateinit var btnRegister: MaterialButton // Tombol daftar/register
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main) // Pasang layout activity_main.xml
//
//        // Inisialisasi komponen UI dari layout dengan ID yang sudah ditentukan
//        inputName = findViewById(R.id.RegisterName)
//        inputEmail = findViewById(R.id.RegisterEmail)
//        inputPassword = findViewById(R.id.RegisterPassword)
//        inputPhone = findViewById(R.id.RegisterPhone)
//        btnRegister = findViewById(R.id.BtnRegister)
//
//        // Set listener klik tombol register
//        btnRegister.setOnClickListener {
//            // Ambil isi inputan dari user, dan hapus spasi depan-belakang
//            val name = inputName.text.toString().trim()
//            val email = inputEmail.text.toString().trim()
//            val password = inputPassword.text.toString().trim()
//            val phone = inputPhone.text.toString().trim()
//
//            // Validasi input, jika ada yang kosong tampilkan Toast
//            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
//                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
//            } else {
//                // Jika valid, panggil fungsi register dengan data user
//                doRegister(name, email, password, phone)
//            }
//        }
//
//        // Setup teks "Have an account? Login" agar "Login" bisa diklik
//        setupLoginSpan()
//    }
//
//    private fun setupLoginSpan() {
//        val tv = findViewById<TextView>(R.id.tvHaveAccount) // TextView tempat tulisan
//        val text = "Have an account? Login" // Kalimat teks
//        val spannable = SpannableString(text) // Ubah jadi SpannableString untuk klik
//
//        val start = text.indexOf("Login") // Mulai posisi "Login"
//        val end = start + "Login".length // Akhir posisi "Login"
//
//        // Buat clickable span yang menangani klik pada teks "Login"
//        val clickable = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                // Saat "Login" diklik, pindah ke activity Login dan tutup MainActivity
//                startActivity(Intent(this@MainActivity, Login::class.java))
//                finish()
//            }
//
//            override fun updateDrawState(ds: android.text.TextPaint) {
//                super.updateDrawState(ds)
//                ds.isUnderlineText = false // Hilangkan garis bawah teks
//                ds.color = resources.getColor(R.color.colorPrimary) // Warnai teks sesuai tema utama
//                ds.isFakeBoldText = true // Buat teks tebal palsu
//            }
//        }
//
//        // Pasang clickable span ke kata "Login" dalam teks
//        spannable.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        tv.text = spannable // Set teks ke TextView
//        tv.movementMethod = LinkMovementMethod.getInstance() // Aktifkan klik teks
//    }
//
//    // Fungsi untuk proses registrasi dengan API
//    private fun doRegister(name: String, email: String, password: String, phone: String) {
//        val req = RegisterRequest(name, email, password, phone) // Buat request register
//
//        // Nonaktifkan tombol dan ubah teks jadi "Processing..." supaya user tahu sedang berjalan
//        btnRegister.isEnabled = false
//        btnRegister.text = "Processing..."
//
//        // Jalankan coroutine di thread background (IO) agar tidak blocking UI
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val res = apiClient.authService.register(req) // Panggil API register
//                withContext(Dispatchers.Main) {
//                    // Kembali ke thread UI untuk update tampilan
//                    btnRegister.isEnabled = true // Aktifkan tombol lagi
//                    btnRegister.text = "REGISTER" // Kembalikan teks tombol
//
//                    if (res.isSuccessful && res.body() != null) {
//                        // Jika berhasil, simpan nama user ke SharedPreferences lokal
//                        getSharedPreferences("UserPrefs", MODE_PRIVATE)
//                            .edit()
//                            .putString("name", name)
//                            .apply()
//
//                        // Tampilkan pesan sukses dari API ke user
//                        Toast.makeText(
//                            this@MainActivity,
//                            res.body()!!.message,
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        // Pindah ke halaman Login dan tutup halaman register
//                        startActivity(Intent(this@MainActivity, Login::class.java))
//                        finish()
//                    } else {
//                        // Jika API gagal (misal 400, 500), tampilkan pesan error
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Register gagal: ${res.message()}",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                // Jika ada error jaringan atau lain-lain, tangani exception di sini
//                withContext(Dispatchers.Main) {
//                    btnRegister.isEnabled = true // Aktifkan tombol lagi
//                    btnRegister.text = "REGISTER" // Kembalikan teks tombol
//
//                    // Tampilkan pesan error ke user
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Terjadi kesalahan: ${e.message}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//        }
//    }
//}
