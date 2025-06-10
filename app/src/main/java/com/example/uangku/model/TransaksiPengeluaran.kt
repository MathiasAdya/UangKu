// file: model/TransaksiPengeluaran.kt
package com.example.uangku.model

class TransaksiPengeluaran(
    id: String,
    deskripsi: String,
    jumlah: Double,
    tanggal: String,
    kategoriID: String,
    userID: String,
    val metodePembayaran: String // Atribut tambahan untuk pengeluaran
) : Transaksi(id, deskripsi, jumlah, tanggal, kategoriID, userID) {

    /**
     * Metode untuk melakukan kategorisasi otomatis pada pengeluaran.
     * Logika bisa ditambahkan di sini, misalnya menentukan kategori
     * berdasarkan deskripsi atau metode pembayaran.
     */
    fun kategorisasiOtomatis() {
        println("Kategorisasi otomatis untuk pengeluaran $deskripsi telah dilakukan.")
    }
}