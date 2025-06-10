package com.example.uangku.model

class LaporanMingguan(
    tanggalMulai: String,
    tanggalAkhir: String,
    dataTransaksi: List<Transaksi>,
    dataGrafik: Map<String, Double>,
    dataStatistik: Map<String, Any>
) : Laporan("Mingguan", tanggalMulai, tanggalAkhir, dataTransaksi, dataGrafik, dataStatistik) {

    override fun visualisasikan() {
        println("--- Laporan Mingguan dari $tanggalMulai hingga $tanggalAkhir ---")
        println("Statistik: $dataStatistik")
        println("Grafik: Menampilkan total pengeluaran per hari dalam seminggu.")
        // Logika spesifik untuk membuat grafik mingguan (misal: line chart pengeluaran harian)
    }
}