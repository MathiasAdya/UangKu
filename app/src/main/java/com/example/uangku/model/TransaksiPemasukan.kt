// file: model/TransaksiPemasukan.kt
package com.example.uangku.model

class TransaksiPemasukan(
    id: String,
    deskripsi: String,
    jumlah: Double,
    tanggal: String,
    kategoriID: String,
    userID: String,
    val sumberDana: String // Atribut tambahan untuk pemasukan
) : Transaksi(id, deskripsi, jumlah, tanggal, kategoriID, userID) {

    /**
     * Metode untuk melakukan validasi pemasukan.
     * Logika bisa ditambahkan di sini, misalnya memeriksa apakah sumber dana valid.
     */
    fun validasi() {
        println("Validasi untuk pemasukan dari $sumberDana sejumlah $jumlah telah dilakukan.")
    }
}