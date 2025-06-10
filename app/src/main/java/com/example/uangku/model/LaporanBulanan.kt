package com.example.uangku.model

class LaporanBulanan(
    bulan: String, // Misal: "2024-05"
    tanggalMulai: String,
    tanggalAkhir: String,
    dataTransaksi: List<Transaksi>,
    dataGrafik: Map<String, Double>,
    dataStatistik: Map<String, Any>
) : Laporan("Bulanan", tanggalMulai, tanggalAkhir, dataTransaksi, dataGrafik, dataStatistik) {

    override fun visualisasikan() {
        println("--- Laporan Bulanan untuk $bulan ---")
        println("Statistik: $dataStatistik")
        println("Grafik: Menampilkan persentase pengeluaran per kategori.")
        // Logika spesifik untuk membuat grafik bulanan (misal: pie chart berdasarkan kategori)
    }
}