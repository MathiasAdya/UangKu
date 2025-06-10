package com.example.uangku.model

data class Pengingat(
    val pengingatID: String,
    val deskripsiPengingat: String,
    val tanggalJatuhTempo: String,
    val frekuensi: String,
    val userID: String,
    var sudahDiberitahukan: Boolean = false
)
