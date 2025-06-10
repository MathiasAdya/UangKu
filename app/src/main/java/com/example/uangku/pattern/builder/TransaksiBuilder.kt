package com.example.uangku.pattern.builder

import com.example.uangku.model.*
import java.util.UUID

/**
 * Builder Pattern - Builder untuk membuat objek Transaksi yang kompleks
 */
class TransaksiBuilder {
    private var id: String = UUID.randomUUID().toString()
    private var deskripsi: String = ""
    private var jumlah: Double = 0.0
    private var tanggal: String = ""
    private var kategoriID: String = ""
    private var userID: String = ""
    private var tipe: String = ""
    private var extraData: MutableMap<String, String> = mutableMapOf()
    
    fun setId(id: String): TransaksiBuilder {
        this.id = id
        return this
    }
    
    fun setDeskripsi(deskripsi: String): TransaksiBuilder {
        this.deskripsi = deskripsi
        return this
    }
    
    fun setJumlah(jumlah: Double): TransaksiBuilder {
        this.jumlah = jumlah
        return this
    }
    
    fun setTanggal(tanggal: String): TransaksiBuilder {
        this.tanggal = tanggal
        return this
    }
    
    fun setKategoriID(kategoriID: String): TransaksiBuilder {
        this.kategoriID = kategoriID
        return this
    }
    
    fun setUserID(userID: String): TransaksiBuilder {
        this.userID = userID
        return this
    }
    
    fun setTipe(tipe: String): TransaksiBuilder {
        this.tipe = tipe
        return this
    }
    
    fun setSumberDana(sumberDana: String): TransaksiBuilder {
        extraData["sumberDana"] = sumberDana
        return this
    }
    
    fun setMetodePembayaran(metodePembayaran: String): TransaksiBuilder {
        extraData["metodePembayaran"] = metodePembayaran
        return this
    }
    
    fun addExtraData(key: String, value: String): TransaksiBuilder {
        extraData[key] = value
        return this
    }
    
    fun build(): Transaksi {
        validateRequiredFields()
        
        return when (tipe.lowercase()) {
            "pemasukan" -> {
                val sumberDana = extraData["sumberDana"] ?: "Unknown"
                TransaksiPemasukan(id, deskripsi, jumlah, tanggal, kategoriID, userID, sumberDana)
            }
            "pengeluaran" -> {
                val metodePembayaran = extraData["metodePembayaran"] ?: "Cash"
                TransaksiPengeluaran(id, deskripsi, jumlah, tanggal, kategoriID, userID, metodePembayaran)
            }
            else -> Transaksi(id, deskripsi, jumlah, tanggal, kategoriID, userID)
        }
    }
    
    private fun validateRequiredFields() {
        require(deskripsi.isNotBlank()) { "Deskripsi tidak boleh kosong" }
        require(jumlah > 0) { "Jumlah harus lebih dari 0" }
        require(tanggal.isNotBlank()) { "Tanggal tidak boleh kosong" }
        require(kategoriID.isNotBlank()) { "Kategori ID tidak boleh kosong" }
        require(userID.isNotBlank()) { "User ID tidak boleh kosong" }
    }
    
    fun reset(): TransaksiBuilder {
        id = UUID.randomUUID().toString()
        deskripsi = ""
        jumlah = 0.0
        tanggal = ""
        kategoriID = ""
        userID = ""
        tipe = ""
        extraData.clear()
        return this
    }
}

/**
 * Builder Pattern - Builder untuk membuat objek Laporan yang kompleks
 */
class LaporanBuilder {
    private var tipeLaporan: String = ""
    private var tanggalMulai: String = ""
    private var tanggalAkhir: String = ""
    private var dataTransaksi: List<Transaksi> = emptyList()
    private var dataGrafik: Map<String, Double> = emptyMap()
    private var dataStatistik: Map<String, Any> = emptyMap()
    private var customTitle: String? = null
    private var includeChart: Boolean = true
    private var includeStatistics: Boolean = true
    
    fun setTipeLaporan(tipeLaporan: String): LaporanBuilder {
        this.tipeLaporan = tipeLaporan
        return this
    }
    
    fun setPeriode(tanggalMulai: String, tanggalAkhir: String): LaporanBuilder {
        this.tanggalMulai = tanggalMulai
        this.tanggalAkhir = tanggalAkhir
        return this
    }
    
    fun setDataTransaksi(dataTransaksi: List<Transaksi>): LaporanBuilder {
        this.dataTransaksi = dataTransaksi
        return this
    }
    
    fun setCustomTitle(title: String): LaporanBuilder {
        this.customTitle = title
        return this
    }
    
    fun includeChart(include: Boolean): LaporanBuilder {
        this.includeChart = include
        return this
    }
    
    fun includeStatistics(include: Boolean): LaporanBuilder {
        this.includeStatistics = include
        return this
    }
    
    fun calculateDataGrafik(): LaporanBuilder {
        if (includeChart) {
            dataGrafik = dataTransaksi.groupBy { it.kategoriID }
                .mapValues { it.value.sumOf { t -> t.jumlah } }
        }
        return this
    }
    
    fun calculateDataStatistik(): LaporanBuilder {
        if (includeStatistics) {
            val total = dataTransaksi.sumOf { it.jumlah }
            val jumlahTransaksi = dataTransaksi.size
            val rataRata = if (jumlahTransaksi > 0) total / jumlahTransaksi else 0.0
            
            val pemasukan = dataTransaksi.filterIsInstance<TransaksiPemasukan>().sumOf { it.jumlah }
            val pengeluaran = dataTransaksi.filterIsInstance<TransaksiPengeluaran>().sumOf { it.jumlah }
            
            dataStatistik = mapOf(
                "Total" to total,
                "Jumlah Transaksi" to jumlahTransaksi,
                "Rata-rata" to rataRata,
                "Total Pemasukan" to pemasukan,
                "Total Pengeluaran" to pengeluaran,
                "Saldo" to (pemasukan - pengeluaran)
            )
        }
        return this
    }
    
    fun build(): Laporan {
        validateRequiredFields()
        
        // Auto-calculate if not set
        if (dataGrafik.isEmpty() && includeChart) {
            calculateDataGrafik()
        }
        if (dataStatistik.isEmpty() && includeStatistics) {
            calculateDataStatistik()
        }
        return when (tipeLaporan.lowercase()) {
            "harian" -> LaporanHarian(tanggalMulai, dataTransaksi, dataGrafik, dataStatistik)
            "mingguan" -> LaporanMingguan(tanggalMulai, tanggalAkhir, dataTransaksi, dataGrafik, dataStatistik)
            "bulanan" -> LaporanBulanan(tanggalMulai.substring(0, 7), tanggalMulai, tanggalAkhir, dataTransaksi, dataGrafik, dataStatistik)
            else -> Laporan(tipeLaporan, tanggalMulai, tanggalAkhir, dataTransaksi, dataGrafik, dataStatistik)
        }
    }
    
    private fun validateRequiredFields() {
        require(tipeLaporan.isNotBlank()) { "Tipe laporan tidak boleh kosong" }
        require(tanggalMulai.isNotBlank()) { "Tanggal mulai tidak boleh kosong" }
        require(tanggalAkhir.isNotBlank()) { "Tanggal akhir tidak boleh kosong" }
    }
    
    fun reset(): LaporanBuilder {
        tipeLaporan = ""
        tanggalMulai = ""
        tanggalAkhir = ""
        dataTransaksi = emptyList()
        dataGrafik = emptyMap()
        dataStatistik = emptyMap()
        customTitle = null
        includeChart = true
        includeStatistics = true
        return this
    }
}
