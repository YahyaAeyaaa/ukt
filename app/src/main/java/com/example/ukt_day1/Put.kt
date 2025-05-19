package com.example.ukt_day1

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ukt_day1.api.apiClient
import com.example.ukt_day1.api.sessionManager
import com.example.ukt_day1.request.EditBookRequest

class Put : AppCompatActivity() {

    // Deklarasi variabel UI untuk input data buku
    private lateinit var etIsbn: EditText
    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etPublisher: EditText
    private lateinit var etPublishedDate: EditText
    private lateinit var etGenre: EditText
    private lateinit var etLanguage: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnUpdate: Button
    private lateinit var tvCancel: TextView

    // Variabel untuk menyimpan ID buku yang akan diedit, diterima dari activity sebelumnya
    private var bookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update)  // Pasang layout XML

        // Inisialisasi komponen UI dari layout menggunakan findViewById
        etIsbn = findViewById(R.id.editIsbn)
        etTitle = findViewById(R.id.editTitle)
        etAuthor = findViewById(R.id.editAuthor)
        etPublisher = findViewById(R.id.editPublisher)
        etPublishedDate = findViewById(R.id.editPublishedDate)
        etGenre = findViewById(R.id.editGenre)
        etLanguage = findViewById(R.id.editLanguage)
        etDescription = findViewById(R.id.editDescription)
        btnUpdate = findViewById(R.id.btnUpdate)
        tvCancel = findViewById(R.id.textCancelEdit)

        // Ambil data buku yang dikirim lewat Intent dan isi ke EditText agar bisa diedit user
        val intent = intent
        bookId = intent.getStringExtra("book_id")
        etIsbn.setText(intent.getStringExtra("isbn"))
        etTitle.setText(intent.getStringExtra("title"))
        etAuthor.setText(intent.getStringExtra("author"))
        etPublisher.setText(intent.getStringExtra("publisher"))
        etPublishedDate.setText(intent.getStringExtra("published_date"))
        etGenre.setText(intent.getStringExtra("genre"))
        etLanguage.setText(intent.getStringExtra("language"))
        etDescription.setText(intent.getStringExtra("description"))

        // Pasang event listener untuk tombol Update agar memulai proses update data buku
        btnUpdate.setOnClickListener {
            updateBook()
        }

        // Membuat teks "Cancel" yang bisa diklik dan akan mengakhiri activity ini
        val cancelText = "Cancel"
        val spannable = SpannableString(cancelText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                finish() // Tutup activity ini, kembali ke Home tanpa update
            }
            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false  // Hilangkan garis bawah di teks
                ds.color = Color.BLUE        // Warnai teks jadi biru supaya keliatan seperti link
            }
        }
        spannable.setSpan(clickableSpan, 0, cancelText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvCancel.text = spannable
        tvCancel.movementMethod = LinkMovementMethod.getInstance() // Biar teks bisa diklik
        tvCancel.highlightColor = Color.TRANSPARENT                // Hilangkan warna saat klik
    }

    // Fungsi untuk mengupdate data buku lewat API
    private fun updateBook() {
        // Ambil input user dari masing-masing EditText
        val isbn = etIsbn.text.toString()
        val title = etTitle.text.toString()
        val author = etAuthor.text.toString()
        val publisher = etPublisher.text.toString()
        val publishedDate = etPublishedDate.text.toString()
        val genre = etGenre.text.toString()
        val language = etLanguage.text.toString()
        val description = etDescription.text.toString()

        // Validasi input: pastikan semua field terisi, kalau kosong munculkan Toast dan batalkan update
        if (isbn.isBlank() || title.isBlank() || author.isBlank() || publisher.isBlank() ||
            publishedDate.isBlank() || genre.isBlank() || language.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Buat objek EditBookRequest yang berisi data untuk dikirim ke API
        val request = EditBookRequest(
            isbn = isbn,
            title = title,
            author = author,
            publisher = publisher,
            published_date = publishedDate,
            genre = genre,
            language = language,
            description = description
        )

        // Ambil token autentikasi yang tersimpan di sessionManager (dipakai di header API)
        val token = "Bearer ${sessionManager(this).getAuthToken()}"

        // Konversi bookId ke integer, cek validitas ID
        val id = bookId?.toIntOrNull()
        if (id == null) {
            Toast.makeText(this, "ID buku tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Jalankan coroutine di lifecycleScope agar operasi jaringan tidak blok UI dan otomatis dibatalkan saat Activity selesai
        lifecycleScope.launch {
            try {
                // Panggil API editBook, kirim data update ke backend
                val response = apiClient.authService.editBook(id, token, request)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    // Jika backend memberi status sukses, tampilkan pesan dan kembali ke Home
                    if (responseBody != null && responseBody.status == "success") {
                        Toast.makeText(this@Put, "Buku berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK) // Kirim tanda berhasil ke activity sebelumnya
                        finish()             // Tutup activity edit
                    } else {
                        // Kalau backend respon tapi gagal, tampilkan pesan error dari response
                        Toast.makeText(this@Put, "Gagal update: ${responseBody?.status ?: "Response kosong"}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Jika respon error dari server (misal 403, 500), tampilkan kode error
                    Toast.makeText(this@Put, "Error response: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Tangani error jaringan atau error lain saat panggil API
                Toast.makeText(this@Put, "Error jaringan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Gunakan lifecycleScope untuk menjalankan coroutine agar aman dengan siklus hidup Activity
// Coroutine itu seperti mekanisme untuk menjalankan tugas-tugas berat atau operasi yang butuh waktu (misal: panggilan jaringan)
// secara asynchronous (berjalan di background), tanpa membuat aplikasi menjadi ngelag atau berhenti merespon.
//
// Dengan coroutine, kamu bisa:
// - Menjalankan proses yang lama tanpa ngeblok UI thread (agar aplikasi tetap lancar)
// - Mengelola tugas asynchronous dengan kode yang lebih bersih dan mudah dibaca dibanding callback biasa
//
// Di Android, lifecycleScope terintegrasi dengan Activity atau Fragment, sehingga coroutine otomatis dibatalkan
// jika Activity/Fragment dihancurkan, menghindari memory leak atau crash.
//
// Contoh kasus di sini:
// saat update data buku, coroutine digunakan untuk memanggil API editBook di background,
// lalu setelah selesai, hasilnya diproses di UI thread secara otomatis.
}
