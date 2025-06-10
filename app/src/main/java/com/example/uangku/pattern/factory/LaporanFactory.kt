package com.example.uangku.pattern.factory

import com.example.uangku.model.*

/**
 * Factory Pattern - LaporanFactory
 * Membuat instance laporan berdasarkan tipe yang diminta
 */
object LaporanFactory {
    
    fun createLaporan(
        tipeLaporan: TipeLaporan,
        transaksiList: List<Transaksi>,
        tanggalMulai: String,
        tanggalAkhir: String
    ): Laporan {
        val dataGrafik = transaksiList.groupBy { it.kategoriID }
            .mapValues { it.value.sumOf { t -> t.jumlah } }
        
        val dataStatistik = mapOf(
            "Total" to transaksiList.sumOf { it.jumlah },
            "Jumlah Transaksi" to transaksiList.size,
            "Rata-rata" to if (transaksiList.isNotEmpty()) 
                transaksiList.sumOf { it.jumlah } / transaksiList.size else 0.0
        )
        return when (tipeLaporan) {
            TipeLaporan.HARIAN -> LaporanHarian(
                tanggalMulai, transaksiList, dataGrafik, dataStatistik
            )
            TipeLaporan.MINGGUAN -> LaporanMingguan(
                tanggalMulai, tanggalAkhir, transaksiList, dataGrafik, dataStatistik
            )
            TipeLaporan.BULANAN -> LaporanBulanan(
                tanggalMulai.substring(0, 7), tanggalMulai, tanggalAkhir, transaksiList, dataGrafik, dataStatistik
            )
        }
    }
}

enum class TipeLaporan {
    HARIAN,
    MINGGUAN,
    BULANAN
}
