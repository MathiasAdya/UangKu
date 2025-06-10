package com.example.uangku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uangku.model.*
import com.example.uangku.pattern.factory.LaporanFactory
import com.example.uangku.pattern.factory.TipeLaporan
import com.example.uangku.pattern.strategy.LaporanVisualizer
import com.example.uangku.pattern.builder.LaporanBuilder
import com.example.uangku.pattern.repository.TransaksiRepository
import com.example.uangku.pattern.repository.CloudTransaksiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LaporanViewModel : ViewModel() {
    private val repository: TransaksiRepository = CloudTransaksiRepository()
    private val visualizer = LaporanVisualizer()
    
    private val _currentLaporan = MutableStateFlow<Laporan?>(null)
    val currentLaporan: StateFlow<Laporan?> = _currentLaporan.asStateFlow()
    
    private val _visualization = MutableStateFlow<String>("")
    val visualization: StateFlow<String> = _visualization.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun generateLaporan(
        userId: String,
        tipeLaporan: TipeLaporan,
        tanggalMulai: String,
        tanggalAkhir: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get transactions for the date range
                val result = repository.getTransaksiByDateRange(userId, tanggalMulai, tanggalAkhir)
                
                result.fold(
                    onSuccess = { transaksiList ->
                        val laporan = LaporanFactory.createLaporan(
                            tipeLaporan, transaksiList, tanggalMulai, tanggalAkhir
                        )
                        
                        _currentLaporan.value = laporan
                        _visualization.value = visualizer.visualizeAsSummary(laporan)
                        _error.value = null
                    },
                    onFailure = {
                        _error.value = "Gagal memuat data transaksi: ${it.message}"
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun generateCustomLaporan(
        userId: String,
        tipeLaporan: String,
        tanggalMulai: String,
        tanggalAkhir: String,
        includeChart: Boolean = true,
        includeStatistics: Boolean = true
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getTransaksiByDateRange(userId, tanggalMulai, tanggalAkhir)
                
                result.fold(
                    onSuccess = { transaksiList ->
                        val laporan = LaporanBuilder()
                            .setTipeLaporan(tipeLaporan)
                            .setPeriode(tanggalMulai, tanggalAkhir)
                            .setDataTransaksi(transaksiList)
                            .includeChart(includeChart)
                            .includeStatistics(includeStatistics)
                            .calculateDataGrafik()
                            .calculateDataStatistik()
                            .build()
                        
                        _currentLaporan.value = laporan
                        _visualization.value = visualizer.visualizeAsSummary(laporan)
                        _error.value = null
                    },
                    onFailure = {
                        _error.value = "Gagal memuat data transaksi: ${it.message}"
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun changeVisualization(mode: VisualizationMode) {
        _currentLaporan.value?.let { laporan ->
            _visualization.value = when (mode) {
                VisualizationMode.SUMMARY -> visualizer.visualizeAsSummary(laporan)
                VisualizationMode.CHART -> visualizer.visualizeAsChart(laporan)
                VisualizationMode.TABLE -> visualizer.visualizeAsTable(laporan)
            }
        }
    }
    
    fun getLaporanByKategori(userId: String, kategoriId: String, tanggalMulai: String, tanggalAkhir: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getTransaksiByKategori(userId, kategoriId)
                
                result.fold(
                    onSuccess = { transaksiList ->
                        val filteredTransaksi = transaksiList.filter { 
                            it.tanggal >= tanggalMulai && it.tanggal <= tanggalAkhir 
                        }
                        
                        val laporan = LaporanBuilder()
                            .setTipeLaporan("Kategori")
                            .setPeriode(tanggalMulai, tanggalAkhir)
                            .setDataTransaksi(filteredTransaksi)
                            .calculateDataGrafik()
                            .calculateDataStatistik()
                            .build()
                        
                        _currentLaporan.value = laporan
                        _visualization.value = visualizer.visualizeAsSummary(laporan)
                        _error.value = null
                    },
                    onFailure = {
                        _error.value = "Gagal memuat data kategori: ${it.message}"
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun exportLaporan(): String? {
        return _currentLaporan.value?.let { laporan ->
            visualizer.visualizeAsTable(laporan)
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

enum class VisualizationMode {
    SUMMARY,
    CHART,
    TABLE
}
