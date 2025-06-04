# 📱 Aplikasi UKT

Aplikasi Android yang dibuat untuk mengelola data buku menggunakan autentikasi token. Proyek ini dibangun dengan arsitektur yang sederhana namun fungsional, cocok untuk keperluan pembelajaran maupun pengembangan lebih lanjut.

## 🚀 Fitur Utama

- ✅ **Login & Register** menggunakan token autentikasi
- 🔄 **Refresh token otomatis** saat akses token kadaluarsa
- 📚 **Tampilkan daftar buku** di `RecyclerView`
- ➕ Tambah data buku
- 📝 Edit data buku
- ❌ Hapus data buku
- 🔍 Lihat detail buku
- 🎨 Desain UI dengan icon custom & layout modern

## 🛠️ Teknologi yang Digunakan

- Kotlin
- Android Studio
- Retrofit2 + OkHttp
- RESTful API
- JWT (JSON Web Token)
- SharedPreferences
- Timeout & error handling
- MVVM (opsional jika kamu ingin refactor ke struktur ini)

## 🧪 Struktur Folder

```bash
├── api/
│   ├── AuthService.kt
│   └── apiClient.kt
│
├── Request/
│   └── request.kt
│
├── Response/
│   └── response.kt
│
├── ui/
│   └── UserAdapter.kt
│
├── MainActivity.kt
├── Home.kt
├── Login.kt
├── Post.kt
├── Put.kt
├── Detail.kt
└── res/
    ├── layout/
    ├── drawable/
    └── mipmap/
