package com.example.uangku.service

import com.example.uangku.model.*
import com.example.uangku.pattern.singleton.DatabaseManager
import com.example.uangku.pattern.factory.*
import com.example.uangku.pattern.repository.*
import com.example.uangku.pattern.observer.*
import com.example.uangku.pattern.builder.*
import com.example.uangku.pattern.command.*
import com.example.uangku.pattern.strategy.*
import com.example.uangku.pattern.adapter.*

/**
 * Service layer yang mendemonstrasikan penggunaan semua design pattern
 */
class UangKuService {
    
    // Singleton instances
    private val databaseManager = DatabaseManager.getInstance()
    
    // Repository pattern
    private val transaksiRepository: TransaksiRepository = CloudTransaksiRepository()
    
    // Observer pattern
    private val transaksiSubject = TransaksiSubject()
    private val saldoObserver = SaldoObserver()
    
    // Command pattern
    private val commandInvoker = TransaksiCommandInvoker()
    
    // Strategy pattern
    private val laporanVisualizer = LaporanVisualizer()
    
    // Adapter pattern
    private val multiSourceAdapter = MultiSourceTransaksiAdapter()
    
    init {
        // Setup observers
        transaksiSubject.addObserver(saldoObserver)
        
        // Setup adapters
        multiSourceAdapter.addSource(BankApiDataSource())
        multiSourceAdapter.addSource(CsvDataSource())
    }
    
    /**
     * Demonstrasi Factory Pattern + Command Pattern + Observer Pattern
     */
    suspend fun addTransaksiPemasukan(
        deskripsi: String,
        jumlah: Double,
        tanggal: String,
        kategoriID: String,
        userID: String,
        sumberDana: String
    ): Result<Boolean> {
        return try {
            // Factory pattern untuk membuat transaksi
            val transaksi = TransaksiFactory.createTransaksiPemasukan(
                deskripsi, jumlah, tanggal, kategoriID, userID, sumberDana
            )
            
            // Command pattern untuk operasi dengan undo/redo
            val command = AddTransaksiCommand(transaksiRepository, transaksi)
            val result = commandInvoker.executeCommand(command)
            
            result.fold(
                onSuccess = {
                    // Observer pattern untuk notifikasi
                    transaksiSubject.addTransaksi(transaksi)
                    Result.success(true)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Demonstrasi Builder Pattern + Strategy Pattern
     */
    suspend fun generateLaporanWithCustomVisualization(
        userId: String,
        tipeLaporan: String,
        tanggalMulai: String,
        tanggalAkhir: String,
        visualizationMode: String
    ): Result<String> {
        return try {
            // Repository pattern untuk data access
            val transaksiResult = transaksiRepository.getTransaksiByDateRange(userId, tanggalMulai, tanggalAkhir)
            
            transaksiResult.fold(
                onSuccess = { transaksiList ->
                    // Builder pattern untuk objek kompleks
                    val laporan = LaporanBuilder()
                        .setTipeLaporan(tipeLaporan)
                        .setPeriode(tanggalMulai, tanggalAkhir)
                        .setDataTransaksi(transaksiList)
                        .calculateDataGrafik()
                        .calculateDataStatistik()
                        .build()
                    
                    // Strategy pattern untuk berbagai cara visualisasi
                    val visualization = when (visualizationMode.lowercase()) {
                        "chart" -> laporanVisualizer.visualizeAsChart(laporan)
                        "table" -> laporanVisualizer.visualizeAsTable(laporan)
                        else -> laporanVisualizer.visualizeAsSummary(laporan)
                    }
                    
                    Result.success(visualization)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Demonstrasi Adapter Pattern
     */
    suspend fun importFromExternalSources(): Result<Int> {
        return try {
            val externalTransaksi = multiSourceAdapter.getAllTransaksi()
            var successCount = 0
            
            externalTransaksi.forEach { transaksi ->
                val result = transaksiRepository.saveTransaksi(transaksi)
                if (result.isSuccess) {
                    successCount++
                    transaksiSubject.addTransaksi(transaksi)
                }
            }
            
            Result.success(successCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Demonstrasi semua pattern dalam satu operasi kompleks
     */
    suspend fun processMonthlyReport(
        userId: String,
        bulan: String,
        tahun: String
    ): Result<MonthlyReportResult> {
        return try {
            val tanggalMulai = "$tahun-$bulan-01"
            val tanggalAkhir = "$tahun-$bulan-31"
            
            // Repository pattern
            val transaksiResult = transaksiRepository.getTransaksiByDateRange(userId, tanggalMulai, tanggalAkhir)
            
            transaksiResult.fold(
                onSuccess = { transaksiList ->
                    // Factory pattern
                    val laporan = LaporanFactory.createLaporan(
                        TipeLaporan.BULANAN, transaksiList, tanggalMulai, tanggalAkhir
                    )
                    
                    // Strategy pattern - multiple visualizations
                    val summaryView = laporanVisualizer.visualizeAsSummary(laporan)
                    val chartView = laporanVisualizer.visualizeAsChart(laporan)
                    val tableView = laporanVisualizer.visualizeAsTable(laporan)
                    
                    // Adapter pattern - export
                    val exportAdapter = TransaksiExportAdapter()
                    val csvData = exportAdapter.exportToCsvFormat(transaksiList)
                    
                    // Observer pattern - setup budget monitoring
                    val userBudgets = databaseManager.getAnggaranByUserId(userId)
                    userBudgets.forEach { anggaran ->
                        val budgetObserver = AnggaranObserver(anggaran)
                        transaksiSubject.addObserver(budgetObserver)
                    }
                    
                    val result = MonthlyReportResult(
                        laporan = laporan,
                        summaryVisualization = summaryView,
                        chartVisualization = chartView,
                        tableVisualization = tableView,
                        csvExport = csvData,
                        canUndo = commandInvoker.canUndo(),
                        canRedo = commandInvoker.canRedo()
                    )
                    
                    Result.success(result)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Batch operation menggunakan Command Pattern
     */
    suspend fun batchUpdateTransaksi(updates: List<TransaksiUpdateData>): Result<Boolean> {
        return try {
            val commands = updates.map { updateData ->
                UpdateTransaksiCommand(
                    repository = transaksiRepository,
                    transaksiId = updateData.transaksiId,
                    newTransaksi = updateData.newTransaksi,
                    oldTransaksi = updateData.oldTransaksi
                )
            }
            
            val batchCommand = BatchTransaksiCommand(commands)
            val result = commandInvoker.executeCommand(batchCommand)
            
            result.fold(
                onSuccess = {
                    // Notify observers of all changes
                    updates.forEach { update ->
                        transaksiSubject.updateTransaksi(update.transaksiId, update.newTransaksi)
                    }
                    Result.success(true)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun undoLastOperation(): Boolean {
        return commandInvoker.canUndo()
    }
    
    fun redoLastOperation(): Boolean {
        return commandInvoker.canRedo()
    }
    
    fun addBudgetMonitoring(anggaran: Anggaran) {
        val budgetObserver = AnggaranObserver(anggaran)
        transaksiSubject.addObserver(budgetObserver)
    }
}

/**
 * Data class untuk hasil laporan bulanan
 */
data class MonthlyReportResult(
    val laporan: Laporan,
    val summaryVisualization: String,
    val chartVisualization: String,
    val tableVisualization: String,
    val csvExport: String,
    val canUndo: Boolean,
    val canRedo: Boolean
)

/**
 * Data class untuk batch update
 */
data class TransaksiUpdateData(
    val transaksiId: String,
    val oldTransaksi: Transaksi,
    val newTransaksi: Transaksi
)
