package com.example.uangku.model

/**
 * Laporan Harian
 * Menampilkan visualisasi data transaksi per jam.
 */
class LaporanHarian(
    tanggal: String,
    dataTransaksi: List<Transaksi>,
    dataGrafik: Map<String, Double>,
    dataStatistik: Map<String, Any>
) : Laporan("Harian", tanggal, tanggal, dataTransaksi, dataGrafik, dataStatistik) {

    override fun visualisasikan() {
        println("--- Laporan Harian untuk tanggal $tanggalMulai ---")
        println("Statistik: $dataStatistik")
        println("Grafik: Menampilkan tren pengeluaran per jam.")
        // Logika spesifik untuk membuat grafik harian (misal: bar chart per jam)
    }
}