package com.example.uangku.pattern.strategy

import com.example.uangku.model.Laporan

/**
 * Strategy Pattern - Interface untuk strategi visualisasi laporan
 */
interface VisualizationStrategy {
    fun visualize(laporan: Laporan): String
}

/**
 * Strategy Pattern - Strategi visualisasi untuk chart
 */
class ChartVisualizationStrategy : VisualizationStrategy {
    override fun visualize(laporan: Laporan): String {
        val result = StringBuilder()
        result.append("=== CHART VISUALIZATION ===\n")
        result.append("Laporan: ${laporan.tipeLaporan}\n")
        result.append("Periode: ${laporan.tanggalMulai} - ${laporan.tanggalAkhir}\n")
        result.append("\nData Grafik:\n")
        
        laporan.dataGrafik.forEach { (kategori, jumlah) ->
            val bar = "█".repeat((jumlah / 100000).toInt().coerceAtMost(20))
            result.append("$kategori: $bar Rp ${String.format("%,.0f", jumlah)}\n")
        }
        
        return result.toString()
    }
}

/**
 * Strategy Pattern - Strategi visualisasi untuk tabel
 */
class TableVisualizationStrategy : VisualizationStrategy {
    override fun visualize(laporan: Laporan): String {
        val result = StringBuilder()
        result.append("=== TABLE VISUALIZATION ===\n")
        result.append("Laporan: ${laporan.tipeLaporan}\n")
        result.append("Periode: ${laporan.tanggalMulai} - ${laporan.tanggalAkhir}\n")
        result.append("\n")
        result.append("| Kategori          | Jumlah           |\n")
        result.append("|-------------------|------------------|\n")
        
        laporan.dataGrafik.forEach { (kategori, jumlah) ->
            result.append("| %-17s | Rp %,12.0f |\n".format(kategori, jumlah))
        }
        
        result.append("\nStatistik:\n")
        laporan.dataStatistik.forEach { (key, value) ->
            result.append("$key: $value\n")
        }
        
        return result.toString()
    }
}

/**
 * Strategy Pattern - Strategi visualisasi untuk summary
 */
class SummaryVisualizationStrategy : VisualizationStrategy {
    override fun visualize(laporan: Laporan): String {
        val result = StringBuilder()
        result.append("=== SUMMARY VISUALIZATION ===\n")
        result.append("Laporan ${laporan.tipeLaporan}\n")
        result.append("Periode: ${laporan.tanggalMulai} - ${laporan.tanggalAkhir}\n\n")
        
        val total = laporan.dataStatistik["Total"] as? Double ?: 0.0
        val jumlahTransaksi = laporan.dataStatistik["Jumlah Transaksi"] as? Int ?: 0
        val rataRata = laporan.dataStatistik["Rata-rata"] as? Double ?: 0.0
        
        result.append("📊 Total Transaksi: $jumlahTransaksi\n")
        result.append("💰 Total Nilai: Rp ${String.format("%,.0f", total)}\n")
        result.append("📈 Rata-rata: Rp ${String.format("%,.0f", rataRata)}\n")
        
        if (laporan.dataGrafik.isNotEmpty()) {
            val topKategori = laporan.dataGrafik.maxByOrNull { it.value }
            result.append("🏆 Kategori Tertinggi: ${topKategori?.key} (Rp ${String.format("%,.0f", topKategori?.value ?: 0.0)})\n")
        }
        
        return result.toString()
    }
}

/**
 * Strategy Pattern - Context class untuk mengelola strategi visualisasi
 */
class LaporanVisualizer {
    private var strategy: VisualizationStrategy = SummaryVisualizationStrategy()
    
    fun setStrategy(strategy: VisualizationStrategy) {
        this.strategy = strategy
    }
    
    fun visualize(laporan: Laporan): String {
        return strategy.visualize(laporan)
    }
    
    fun visualizeAsChart(laporan: Laporan): String {
        setStrategy(ChartVisualizationStrategy())
        return visualize(laporan)
    }
    
    fun visualizeAsTable(laporan: Laporan): String {
        setStrategy(TableVisualizationStrategy())
        return visualize(laporan)
    }
    
    fun visualizeAsSummary(laporan: Laporan): String {
        setStrategy(SummaryVisualizationStrategy())
        return visualize(laporan)
    }
}
