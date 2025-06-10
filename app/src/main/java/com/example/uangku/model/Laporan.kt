package com.example.uangku.model

data class Laporan(
    val tipeLaporan: String,
    val tanggalMulai: String,
    val tanggalAkhir: String,
    val dataTransaksi: List<Transaksi>,
    val dataGrafik: Map<String, Double>,
    val dataStatistik: Map<String, Any>
) {
    companion object {
        fun generateLaporan(
            transaksiList: List<Transaksi>,
            tipe: String,
            mulai: String,
            akhir: String
        ): Laporan {
            val total = transaksiList.sumOf { it.jumlah }
            val dataGrafik = transaksiList.groupBy { it.kategoriID }
                .mapValues { it.value.sumOf { t -> t.jumlah } }

            val dataStatistik = mapOf(
                "Total" to total,
                "Jumlah Transaksi" to transaksiList.size
            )

            return Laporan(tipe, mulai, akhir, transaksiList, dataGrafik, dataStatistik)
        }
    }

    fun visualisasikan() {
        // misalnya, return grafik dalam bentuk JSON atau object siap render
    }
}
