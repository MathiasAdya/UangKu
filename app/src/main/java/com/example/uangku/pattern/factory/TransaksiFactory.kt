package com.example.uangku.pattern.factory

import com.example.uangku.model.*
import java.util.UUID

/**
 * Factory Pattern - TransaksiFactory
 * Membuat instance transaksi berdasarkan tipe yang diminta
 */
object TransaksiFactory {
    
    fun createTransaksi(
        tipe: TipeTransaksi,
        deskripsi: String,
        jumlah: Double,
        tanggal: String,
        kategoriID: String,
        userID: String,
        extraData: Map<String, String> = emptyMap()
    ): Transaksi {
        val id = UUID.randomUUID().toString()
        
        return when (tipe) {
            TipeTransaksi.PEMASUKAN -> {
                val sumberDana = extraData["sumberDana"] ?: "Unknown"
                TransaksiPemasukan(id, deskripsi, jumlah, tanggal, kategoriID, userID, sumberDana)
            }
            TipeTransaksi.PENGELUARAN -> {
                val metodePembayaran = extraData["metodePembayaran"] ?: "Cash"
                TransaksiPengeluaran(id, deskripsi, jumlah, tanggal, kategoriID, userID, metodePembayaran)
            }
        }
    }
    
    fun createTransaksiPemasukan(
        deskripsi: String,
        jumlah: Double,
        tanggal: String,
        kategoriID: String,
        userID: String,
        sumberDana: String
    ): TransaksiPemasukan {
        val id = UUID.randomUUID().toString()
        return TransaksiPemasukan(id, deskripsi, jumlah, tanggal, kategoriID, userID, sumberDana)
    }
    
    fun createTransaksiPengeluaran(
        deskripsi: String,
        jumlah: Double,
        tanggal: String,
        kategoriID: String,
        userID: String,
        metodePembayaran: String
    ): TransaksiPengeluaran {
        val id = UUID.randomUUID().toString()
        return TransaksiPengeluaran(id, deskripsi, jumlah, tanggal, kategoriID, userID, metodePembayaran)
    }
}

enum class TipeTransaksi {
    PEMASUKAN,
    PENGELUARAN
}
