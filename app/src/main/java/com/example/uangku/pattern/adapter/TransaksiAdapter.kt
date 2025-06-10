package com.example.uangku.pattern.adapter

import com.example.uangku.model.Transaksi
import com.example.uangku.model.TransaksiPemasukan
import com.example.uangku.model.TransaksiPengeluaran

/**
 * Adapter Pattern - Interface untuk external data source
 */
interface ExternalTransaksiSource {
    fun getTransaksiData(): List<ExternalTransaksi>
}

/**
 * Adapter Pattern - Data class untuk external transaction format
 */
data class ExternalTransaksi(
    val transactionId: String,
    val description: String,
    val amount: Double,
    val date: String,
    val category: String,
    val userId: String,
    val type: String, // "income" atau "expense"
    val paymentMethod: String? = null,
    val source: String? = null
)

/**
 * Adapter Pattern - Mock external data source (e.g., bank API)
 */
class BankApiDataSource : ExternalTransaksiSource {
    override fun getTransaksiData(): List<ExternalTransaksi> {
        return listOf(
            ExternalTransaksi(
                transactionId = "BANK001",
                description = "Gaji Bulanan",
                amount = 5000000.0,
                date = "2025-06-01",
                category = "SALARY",
                userId = "user001",
                type = "income",
                source = "Bank Transfer"
            ),
            ExternalTransaksi(
                transactionId = "BANK002",
                description = "Belanja Supermarket",
                amount = 250000.0,
                date = "2025-06-02",
                category = "FOOD",
                userId = "user001",
                type = "expense",
                paymentMethod = "Debit Card"
            )
        )
    }
}

/**
 * Adapter Pattern - Mock CSV data source
 */
class CsvDataSource : ExternalTransaksiSource {
    override fun getTransaksiData(): List<ExternalTransaksi> {
        return listOf(
            ExternalTransaksi(
                transactionId = "CSV001",
                description = "Freelance Project",
                amount = 1500000.0,
                date = "2025-06-03",
                category = "WORK",
                userId = "user001",
                type = "income",
                source = "Client Payment"
            ),
            ExternalTransaksi(
                transactionId = "CSV002",
                description = "Bensin Motor",
                amount = 50000.0,
                date = "2025-06-04",
                category = "TRANSPORT",
                userId = "user001",
                type = "expense",
                paymentMethod = "Cash"
            )
        )
    }
}

/**
 * Adapter Pattern - Adapter untuk mengkonversi external data ke internal format
 */
class TransaksiAdapter(private val externalSource: ExternalTransaksiSource) {
    
    fun getAdaptedTransaksi(): List<Transaksi> {
        return externalSource.getTransaksiData().map { external ->
            adaptToInternalFormat(external)
        }
    }
    
    private fun adaptToInternalFormat(external: ExternalTransaksi): Transaksi {
        return when (external.type.lowercase()) {
            "income" -> TransaksiPemasukan(
                id = external.transactionId,
                deskripsi = external.description,
                jumlah = external.amount,
                tanggal = external.date,
                kategoriID = external.category,
                userID = external.userId,
                sumberDana = external.source ?: "Unknown"
            )
            "expense" -> TransaksiPengeluaran(
                id = external.transactionId,
                deskripsi = external.description,
                jumlah = external.amount,
                tanggal = external.date,
                kategoriID = external.category,
                userID = external.userId,
                metodePembayaran = external.paymentMethod ?: "Cash"
            )
            else -> Transaksi(
                id = external.transactionId,
                deskripsi = external.description,
                jumlah = external.amount,
                tanggal = external.date,
                kategoriID = external.category,
                userID = external.userId
            )
        }
    }
}

/**
 * Adapter Pattern - Aggregate adapter untuk multiple sources
 */
class MultiSourceTransaksiAdapter {
    private val adapters = mutableListOf<TransaksiAdapter>()
    
    fun addSource(source: ExternalTransaksiSource) {
        adapters.add(TransaksiAdapter(source))
    }
    
    fun getAllTransaksi(): List<Transaksi> {
        return adapters.flatMap { it.getAdaptedTransaksi() }
    }
    
    fun getTransaksiFromBankApi(): List<Transaksi> {
        val bankAdapter = TransaksiAdapter(BankApiDataSource())
        return bankAdapter.getAdaptedTransaksi()
    }
    
    fun getTransaksiFromCsv(): List<Transaksi> {
        val csvAdapter = TransaksiAdapter(CsvDataSource())
        return csvAdapter.getAdaptedTransaksi()
    }
}

/**
 * Adapter Pattern - Reverse adapter untuk export ke external format
 */
class TransaksiExportAdapter {
    
    fun adaptToExternalFormat(transaksi: Transaksi): ExternalTransaksi {
        return when (transaksi) {
            is TransaksiPemasukan -> ExternalTransaksi(
                transactionId = transaksi.id,
                description = transaksi.deskripsi,
                amount = transaksi.jumlah,
                date = transaksi.tanggal,
                category = transaksi.kategoriID,
                userId = transaksi.userID,
                type = "income",
                source = transaksi.sumberDana
            )
            is TransaksiPengeluaran -> ExternalTransaksi(
                transactionId = transaksi.id,
                description = transaksi.deskripsi,
                amount = transaksi.jumlah,
                date = transaksi.tanggal,
                category = transaksi.kategoriID,
                userId = transaksi.userID,
                type = "expense",
                paymentMethod = transaksi.metodePembayaran
            )
            else -> ExternalTransaksi(
                transactionId = transaksi.id,
                description = transaksi.deskripsi,
                amount = transaksi.jumlah,
                date = transaksi.tanggal,
                category = transaksi.kategoriID,
                userId = transaksi.userID,
                type = "general"
            )
        }
    }
    
    fun exportToCsvFormat(transaksiList: List<Transaksi>): String {
        val csvBuilder = StringBuilder()
        csvBuilder.append("ID,Description,Amount,Date,Category,UserID,Type,PaymentMethod,Source\n")
        
        transaksiList.forEach { transaksi ->
            val external = adaptToExternalFormat(transaksi)
            csvBuilder.append("${external.transactionId},")
            csvBuilder.append("\"${external.description}\",")
            csvBuilder.append("${external.amount},")
            csvBuilder.append("${external.date},")
            csvBuilder.append("${external.category},")
            csvBuilder.append("${external.userId},")
            csvBuilder.append("${external.type},")
            csvBuilder.append("\"${external.paymentMethod ?: ""}\",")
            csvBuilder.append("\"${external.source ?: ""}\"\n")
        }
        
        return csvBuilder.toString()
    }
}
