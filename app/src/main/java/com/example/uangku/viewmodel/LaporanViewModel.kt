package com.example.uangku.viewmodel

import androidx.lifecycle.ViewModel
import com.example.uangku.model.Laporan
import com.example.uangku.model.Transaksi

class LaporanViewModel : ViewModel() {
    fun buatLaporan(transaksiList: List<Transaksi>, dari: String, sampai: String): Laporan {
        return Laporan.generateLaporan(transaksiList, "Bulanan", dari, sampai)
    }
}
