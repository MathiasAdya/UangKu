package com.example.uangku.model

data class Anggaran(
    val anggaranID: String,
    val periode: String,
    val jumlahAnggaran: Double,
    val kategoriID: String?,
    val userID: String
) {
    fun cekStatusAnggaran(transaksiList: List<Transaksi>): Boolean {
        val total = transaksiList.filter {
            kategoriID == null || it.kategoriID == kategoriID
        }.sumOf { it.jumlah }
        return total <= jumlahAnggaran
    }
}
