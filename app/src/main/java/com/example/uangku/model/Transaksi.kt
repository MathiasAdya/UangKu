package com.example.uangku.model

data class Transaksi(
    val id: String,
    val deskripsi: String,
    val jumlah: Double,
    val tanggal: String,
    val kategoriID: String,
    val userID: String
)
