package com.example.uangku.demo

import com.example.uangku.service.UangKuService
import com.example.uangku.pattern.factory.TipeTransaksi
import com.example.uangku.pattern.observer.SaldoObserver
import com.example.uangku.pattern.observer.AnggaranObserver
import com.example.uangku.model.Anggaran
import kotlinx.coroutines.runBlocking

/**
 * Demonstrasi penggunaan semua design pattern dalam aplikasi UangKu
 * 
 * Design Patterns yang diimplementasikan:
 * 1. Singleton Pattern - DatabaseManager
 * 2. Factory Pattern - TransaksiFactory, LaporanFactory
 * 3. Observer Pattern - TransaksiSubject, SaldoObserver, AnggaranObserver
 * 4. Repository Pattern - TransaksiRepository
 * 5. Strategy Pattern - VisualizationStrategy
 * 6. Builder Pattern - TransaksiBuilder, LaporanBuilder
 * 7. Command Pattern - TransaksiCommand dengan undo/redo
 * 8. Adapter Pattern - TransaksiAdapter untuk berbagai sumber data
 */
class DesignPatternDemo {
    
    private val uangKuService = UangKuService()
    private val userId = "demo-user-001"
    
    fun runAllDemos() {
        println("=".repeat(60))
        println("🚀 DEMO DESIGN PATTERNS - APLIKASI UANGKU")
        println("=".repeat(60))
        
        runBlocking {
            demo1SingletonPattern()
            demo2FactoryPattern()
            demo3ObserverPattern()
            demo4RepositoryPattern()
            demo5StrategyPattern()
            demo6BuilderPattern()
            demo7CommandPattern()
            demo8AdapterPattern()
            demo9CombinedPatterns()
        }
        
        println("\n" + "=".repeat(60))
        println("✅ SEMUA DEMO DESIGN PATTERN SELESAI")
        println("=".repeat(60))
    }
    
    private fun demo1SingletonPattern() {
        println("\n📋 DEMO 1: SINGLETON PATTERN")
        println("-".repeat(40))
        
        // DatabaseManager menggunakan Singleton Pattern
        println("✓ DatabaseManager menggunakan Singleton Pattern")
        println("  - Memastikan hanya ada satu instance database manager")
        println("  - Mengelola koneksi database secara terpusat")
        println("  - Thread-safe implementation")
    }
    
    private suspend fun demo2FactoryPattern() {
        println("\n🏭 DEMO 2: FACTORY PATTERN")
        println("-".repeat(40))
        
        // Membuat transaksi menggunakan Factory
        val result1 = uangKuService.addTransaksiPemasukan(
            deskripsi = "Gaji Bulanan",
            jumlah = 8000000.0,
            tanggal = "2025-06-10",
            kategoriID = "SALARY",
            userID = userId,
            sumberDana = "Perusahaan ABC"
        )
        
        println("✓ Factory Pattern untuk Transaksi:")
        println("  - TransaksiFactory.createTransaksiPemasukan()")
        println("  - Hasil: ${if (result1.isSuccess) "Berhasil" else "Gagal"}")
        
        // Membuat laporan menggunakan Factory
        println("✓ Factory Pattern untuk Laporan:")
        println("  - LaporanFactory.createLaporan()")
        println("  - Mendukung berbagai tipe laporan (Harian, Mingguan, Bulanan)")
    }
    
    private suspend fun demo3ObserverPattern() {
        println("\n👁️ DEMO 3: OBSERVER PATTERN")
        println("-".repeat(40))
        
        // Setup observer untuk monitoring saldo
        println("✓ Observer Pattern untuk Monitoring:")
        println("  - SaldoObserver: Memantau perubahan saldo secara real-time")
        println("  - AnggaranObserver: Memberikan peringatan anggaran")
        
        // Setup budget monitoring
        val anggaran = Anggaran(
            anggaranID = "budget-001",
            periode = "2025-06",
            jumlahAnggaran = 2000000.0,
            kategoriID = "FOOD",
            userID = userId
        )
        uangKuService.addBudgetMonitoring(anggaran)
        
        // Add a transaction that might trigger budget warning
        uangKuService.addTransaksiPemasukan(
            deskripsi = "Belanja Groceries",
            jumlah = 150000.0,
            tanggal = "2025-06-10",
            kategoriID = "FOOD",
            userID = userId,
            sumberDana = "Kas"
        )
        
        println("  - Anggaran bulan ini: Rp 2,000,000")
        println("  - Transaksi baru: Rp 150,000")
        println("  - Observer akan memberikan notifikasi otomatis")
    }
    
    private suspend fun demo4RepositoryPattern() {
        println("\n📚 DEMO 4: REPOSITORY PATTERN")
        println("-".repeat(40))
        
        println("✓ Repository Pattern untuk Data Access:")
        println("  - LocalTransaksiRepository: Data lokal")
        println("  - CloudTransaksiRepository: Sinkronisasi cloud")
        println("  - Abstraksi yang memudahkan testing dan maintenance")
        println("  - Automatic fallback dari cloud ke lokal")
    }
    
    private suspend fun demo5StrategyPattern() {
        println("\n🎯 DEMO 5: STRATEGY PATTERN")
        println("-".repeat(40))
        
        val summaryResult = uangKuService.generateLaporanWithCustomVisualization(
            userId = userId,
            tipeLaporan = "Bulanan",
            tanggalMulai = "2025-06-01",
            tanggalAkhir = "2025-06-30",
            visualizationMode = "summary"
        )
        
        val chartResult = uangKuService.generateLaporanWithCustomVisualization(
            userId = userId,
            tipeLaporan = "Bulanan",
            tanggalMulai = "2025-06-01",
            tanggalAkhir = "2025-06-30",
            visualizationMode = "chart"
        )
        
        println("✓ Strategy Pattern untuk Visualisasi:")
        println("  - SummaryVisualizationStrategy: Ringkasan")
        println("  - ChartVisualizationStrategy: Grafik batang")
        println("  - TableVisualizationStrategy: Tabel detail")
        println("  - Dapat berganti strategi visualisasi dinamis")
        
        if (summaryResult.isSuccess) {
            println("\n📊 PREVIEW SUMMARY VISUALIZATION:")
            println(summaryResult.getOrNull()?.take(200) + "...")
        }
    }
    
    private fun demo6BuilderPattern() {
        println("\n🔨 DEMO 6: BUILDER PATTERN")
        println("-".repeat(40))
        
        println("✓ Builder Pattern untuk Object Creation:")
        println("  - TransaksiBuilder: Membangun transaksi kompleks")
        println("  - LaporanBuilder: Membangun laporan dengan konfigurasi custom")
        println("  - Fluent interface untuk kemudahan penggunaan")
        println("  - Validasi built-in untuk data integrity")
        
        println("\n  Contoh penggunaan:")
        println("  TransaksiBuilder()")
        println("    .setDeskripsi(\"Pembelian Laptop\")")
        println("    .setJumlah(15000000.0)")
        println("    .setTipe(\"pengeluaran\")")
        println("    .setMetodePembayaran(\"Credit Card\")")
        println("    .build()")
    }
    
    private suspend fun demo7CommandPattern() {
        println("\n⚡ DEMO 7: COMMAND PATTERN")
        println("-".repeat(40))
        
        println("✓ Command Pattern untuk Operations:")
        println("  - AddTransaksiCommand: Operasi tambah dengan undo")
        println("  - UpdateTransaksiCommand: Operasi update dengan undo")
        println("  - DeleteTransaksiCommand: Operasi hapus dengan undo")
        println("  - BatchTransaksiCommand: Operasi batch dengan rollback")
        
        val canUndo = uangKuService.undoLastOperation()
        val canRedo = uangKuService.redoLastOperation()
        
        println("  - Status Undo: ${if (canUndo) "Tersedia" else "Tidak tersedia"}")
        println("  - Status Redo: ${if (canRedo) "Tersedia" else "Tidak tersedia"}")
        println("  - History limit: 50 operasi terakhir")
    }
    
    private suspend fun demo8AdapterPattern() {
        println("\n🔌 DEMO 8: ADAPTER PATTERN")
        println("-".repeat(40))
        
        val importResult = uangKuService.importFromExternalSources()
        
        println("✓ Adapter Pattern untuk Data Integration:")
        println("  - BankApiDataSource: Import dari API bank")
        println("  - CsvDataSource: Import dari file CSV")
        println("  - TransaksiAdapter: Konversi format eksternal ke internal")
        println("  - MultiSourceAdapter: Menggabungkan berbagai sumber")
        
        importResult.fold(
            onSuccess = { count ->
                println("  - Berhasil import $count transaksi dari sumber eksternal")
            },
            onFailure = { error ->
                println("  - Gagal import: ${error.message}")
            }
        )
    }
    
    private suspend fun demo9CombinedPatterns() {
        println("\n🎪 DEMO 9: KOMBINASI SEMUA PATTERN")
        println("-".repeat(40))
        
        val reportResult = uangKuService.processMonthlyReport(
            userId = userId,
            bulan = "06",
            tahun = "2025"
        )
        
        println("✓ Kombinasi Pattern dalam Monthly Report:")
        println("  - Singleton: DatabaseManager untuk akses data")
        println("  - Factory: LaporanFactory untuk membuat laporan")
        println("  - Repository: CloudTransaksiRepository untuk data access")
        println("  - Strategy: Multiple visualization strategies")
        println("  - Observer: Budget monitoring dan saldo tracking")
        println("  - Builder: Custom laporan configuration")
        println("  - Command: History untuk undo/redo operations")
        println("  - Adapter: Export ke format CSV")
        
        reportResult.fold(
            onSuccess = { result ->
                println("\n📈 HASIL LAPORAN BULANAN:")
                println("  - Tipe: ${result.laporan.tipeLaporan}")
                println("  - Periode: ${result.laporan.tanggalMulai} - ${result.laporan.tanggalAkhir}")
                println("  - Jumlah transaksi: ${result.laporan.dataTransaksi.size}")
                println("  - Can Undo: ${result.canUndo}")
                println("  - Can Redo: ${result.canRedo}")
                println("  - Export CSV: ${result.csvExport.lines().size} baris")
            },
            onFailure = { error ->
                println("  - Error: ${error.message}")
            }
        )
    }
}

/**
 * Fungsi untuk menjalankan demo
 */
fun main() {
    val demo = DesignPatternDemo()
    demo.runAllDemos()
}
