package com.example.uangku

import org.junit.Test
import org.junit.Assert.*
import kotlinx.coroutines.runBlocking
import com.example.uangku.pattern.factory.*
import com.example.uangku.pattern.singleton.DatabaseManager
import com.example.uangku.pattern.builder.TransaksiBuilder
import com.example.uangku.pattern.observer.*
import com.example.uangku.pattern.command.*
import com.example.uangku.pattern.repository.LocalTransaksiRepository
import com.example.uangku.model.*

/**
 * Unit tests untuk memverifikasi implementasi design patterns
 */
class DesignPatternTest {

    @Test
    fun testSingletonPattern() {
        // Test Singleton Pattern - DatabaseManager
        val instance1 = DatabaseManager.getInstance()
        val instance2 = DatabaseManager.getInstance()
        
        assertEquals("Singleton should return same instance", instance1, instance2)
        assertSame("Singleton instances should be identical", instance1, instance2)
    }

    @Test
    fun testFactoryPattern() {
        // Test Factory Pattern - TransaksiFactory
        val pemasukan = TransaksiFactory.createTransaksiPemasukan(
            deskripsi = "Test Income",
            jumlah = 1000000.0,
            tanggal = "2025-06-10",
            kategoriID = "TEST",
            userID = "user001",
            sumberDana = "Test Source"
        )
        
        assertTrue("Should create TransaksiPemasukan", pemasukan is TransaksiPemasukan)
        assertEquals("Amount should match", 1000000.0, pemasukan.jumlah, 0.01)
        assertEquals("Source should match", "Test Source", pemasukan.sumberDana)
        
        val pengeluaran = TransaksiFactory.createTransaksiPengeluaran(
            deskripsi = "Test Expense",
            jumlah = 500000.0,
            tanggal = "2025-06-10",
            kategoriID = "TEST",
            userID = "user001",
            metodePembayaran = "Cash"
        )
        
        assertTrue("Should create TransaksiPengeluaran", pengeluaran is TransaksiPengeluaran)
        assertEquals("Payment method should match", "Cash", pengeluaran.metodePembayaran)
    }

    @Test
    fun testBuilderPattern() {
        // Test Builder Pattern - TransaksiBuilder
        val transaksi = TransaksiBuilder()
            .setDeskripsi("Builder Test")
            .setJumlah(750000.0)
            .setTanggal("2025-06-10")
            .setKategoriID("TEST")
            .setUserID("user001")
            .setTipe("pemasukan")
            .setSumberDana("Builder Source")
            .build()
        
        assertTrue("Should create TransaksiPemasukan via builder", transaksi is TransaksiPemasukan)
        assertEquals("Description should match", "Builder Test", transaksi.deskripsi)
        assertEquals("Amount should match", 750000.0, transaksi.jumlah, 0.01)
    }

    @Test
    fun testObserverPattern() {
        // Test Observer Pattern
        val transaksiSubject = TransaksiSubject()
        var saldoUpdated = false
        
        val testObserver = object : Observer<List<Transaksi>> {
            override fun onUpdate(data: List<Transaksi>) {
                saldoUpdated = true
            }
        }
        
        transaksiSubject.addObserver(testObserver)
        
        val testTransaksi = TransaksiFactory.createTransaksiPemasukan(
            deskripsi = "Observer Test",
            jumlah = 100000.0,
            tanggal = "2025-06-10",
            kategoriID = "TEST",
            userID = "user001",
            sumberDana = "Test"
        )
        
        transaksiSubject.addTransaksi(testTransaksi)
        
        assertTrue("Observer should be notified", saldoUpdated)
        assertEquals("Transaction list should contain 1 item", 1, transaksiSubject.getTransaksiList().size)
    }

    @Test
    fun testCommandPattern() = runBlocking {
        // Test Command Pattern dengan Undo/Redo
        val repository = LocalTransaksiRepository()
        val commandInvoker = TransaksiCommandInvoker()
        
        val testTransaksi = TransaksiFactory.createTransaksiPemasukan(
            deskripsi = "Command Test",
            jumlah = 200000.0,
            tanggal = "2025-06-10",
            kategoriID = "TEST",
            userID = "user001",
            sumberDana = "Test"
        )
        
        // Execute add command
        val addCommand = AddTransaksiCommand(repository, testTransaksi)
        val result = commandInvoker.executeCommand(addCommand)
        
        assertTrue("Command should execute successfully", result.isSuccess)
        assertTrue("Should be able to undo", commandInvoker.canUndo())
        assertFalse("Should not be able to redo yet", commandInvoker.canRedo())
        
        // Test undo
        val undoResult = commandInvoker.undo()
        assertTrue("Undo should be successful", undoResult.isSuccess)
        assertTrue("Should be able to redo after undo", commandInvoker.canRedo())
        
        // Test redo
        val redoResult = commandInvoker.redo()
        assertTrue("Redo should be successful", redoResult.isSuccess)
    }

    @Test
    fun testRepositoryPattern() = runBlocking {
        // Test Repository Pattern
        val repository = LocalTransaksiRepository()
        
        val testTransaksi = TransaksiFactory.createTransaksiPemasukan(
            deskripsi = "Repository Test",
            jumlah = 300000.0,
            tanggal = "2025-06-10",
            kategoriID = "TEST",
            userID = "user001",
            sumberDana = "Test"
        )
        
        // Test save
        val saveResult = repository.saveTransaksi(testTransaksi)
        assertTrue("Save should be successful", saveResult.isSuccess)
        
        // Test retrieve
        val retrieveResult = repository.getTransaksiByUserId("user001")
        assertTrue("Retrieve should be successful", retrieveResult.isSuccess)
        
        val transaksiList = retrieveResult.getOrNull()
        assertNotNull("Transaction list should not be null", transaksiList)
        assertEquals("Should have 1 transaction", 1, transaksiList?.size)
        assertEquals("Transaction should match", testTransaksi.id, transaksiList?.first()?.id)
    }

    @Test
    fun testLaporanFactory() {
        // Test LaporanFactory
        val transaksiList = listOf(
            TransaksiFactory.createTransaksiPemasukan(
                deskripsi = "Test Income",
                jumlah = 1000000.0,
                tanggal = "2025-06-10",
                kategoriID = "SALARY",
                userID = "user001",
                sumberDana = "Bank"
            ),
            TransaksiFactory.createTransaksiPengeluaran(
                deskripsi = "Test Expense",
                jumlah = 500000.0,
                tanggal = "2025-06-10",
                kategoriID = "FOOD",
                userID = "user001",
                metodePembayaran = "Cash"
            )
        )
        
        val laporan = LaporanFactory.createLaporan(
            TipeLaporan.BULANAN,
            transaksiList,
            "2025-06-01",
            "2025-06-30"
        )
        
        assertTrue("Should create LaporanBulanan", laporan is LaporanBulanan)
        assertEquals("Should have correct transaction count", 2, laporan.dataTransaksi.size)
        assertEquals("Should have correct total", 1500000.0, laporan.dataStatistik["Total"] as Double, 0.01)
    }

    @Test
    fun testIntegrationScenario() = runBlocking {
        // Test integrasi beberapa pattern sekaligus
        val repository = LocalTransaksiRepository()
        val commandInvoker = TransaksiCommandInvoker()
        val transaksiSubject = TransaksiSubject()
        var observerNotified = false
        
        // Setup observer
        val testObserver = object : Observer<List<Transaksi>> {
            override fun onUpdate(data: List<Transaksi>) {
                observerNotified = true
            }
        }
        transaksiSubject.addObserver(testObserver)
        
        // Create transaction using Builder
        val transaksi = TransaksiBuilder()
            .setDeskripsi("Integration Test")
            .setJumlah(1500000.0)
            .setTanggal("2025-06-10")
            .setKategoriID("INTEGRATION")
            .setUserID("integration-user")
            .setTipe("pemasukan")
            .setSumberDana("Integration Source")
            .build()
        
        // Execute command
        val command = AddTransaksiCommand(repository, transaksi)
        val result = commandInvoker.executeCommand(command)
        
        // Notify observer
        transaksiSubject.addTransaksi(transaksi)
        
        // Verify integration
        assertTrue("Command should succeed", result.isSuccess)
        assertTrue("Observer should be notified", observerNotified)
        assertTrue("Should be able to undo", commandInvoker.canUndo())
        
        // Verify data persistence
        val retrieveResult = repository.getTransaksiByUserId("integration-user")
        assertTrue("Should retrieve data successfully", retrieveResult.isSuccess)
        assertEquals("Should have 1 transaction", 1, retrieveResult.getOrNull()?.size)
    }
}
