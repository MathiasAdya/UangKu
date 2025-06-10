package com.example.uangku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uangku.model.*
import com.example.uangku.pattern.repository.TransaksiRepository
import com.example.uangku.pattern.repository.CloudTransaksiRepository
import com.example.uangku.pattern.factory.TransaksiFactory
import com.example.uangku.pattern.factory.TipeTransaksi
import com.example.uangku.pattern.command.*
import com.example.uangku.pattern.observer.TransaksiSubject
import com.example.uangku.pattern.builder.TransaksiBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransaksiViewModel : ViewModel() {
    private val repository: TransaksiRepository = CloudTransaksiRepository()
    private val commandInvoker = TransaksiCommandInvoker()
    private val transaksiSubject = TransaksiSubject()
    
    private val _transaksiList = MutableStateFlow<List<Transaksi>>(emptyList())
    val transaksiList: StateFlow<List<Transaksi>> = _transaksiList.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadTransaksi(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getTransaksiByUserId(userId)
                result.fold(
                    onSuccess = { 
                        _transaksiList.value = it
                        _error.value = null
                    },
                    onFailure = { 
                        _error.value = it.message
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addTransaksi(
        tipe: TipeTransaksi,
        deskripsi: String,
        jumlah: Double,
        tanggal: String,
        kategoriID: String,
        userID: String,
        extraData: Map<String, String> = emptyMap()
    ) {
        viewModelScope.launch {
            try {
                val transaksi = TransaksiFactory.createTransaksi(
                    tipe, deskripsi, jumlah, tanggal, kategoriID, userID, extraData
                )
                
                val command = AddTransaksiCommand(repository, transaksi)
                val result = commandInvoker.executeCommand(command)
                
                result.fold(
                    onSuccess = {
                        transaksiSubject.addTransaksi(transaksi)
                        loadTransaksi(userID)
                        _error.value = null
                    },
                    onFailure = {
                        _error.value = it.message
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun updateTransaksi(
        transaksiId: String,
        oldTransaksi: Transaksi,
        newDeskripsi: String,
        newJumlah: Double,
        newTanggal: String,
        newKategoriID: String
    ) {
        viewModelScope.launch {
            try {
                val newTransaksi = TransaksiBuilder()
                    .setId(transaksiId)
                    .setDeskripsi(newDeskripsi)
                    .setJumlah(newJumlah)
                    .setTanggal(newTanggal)
                    .setKategoriID(newKategoriID)
                    .setUserID(oldTransaksi.userID)
                    .build()
                
                val command = UpdateTransaksiCommand(repository, transaksiId, newTransaksi, oldTransaksi)
                val result = commandInvoker.executeCommand(command)
                
                result.fold(
                    onSuccess = {
                        transaksiSubject.updateTransaksi(transaksiId, newTransaksi)
                        loadTransaksi(oldTransaksi.userID)
                        _error.value = null
                    },
                    onFailure = {
                        _error.value = it.message
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun deleteTransaksi(transaksi: Transaksi) {
        viewModelScope.launch {
            try {
                val command = DeleteTransaksiCommand(repository, transaksi.id, transaksi)
                val result = commandInvoker.executeCommand(command)
                
                result.fold(
                    onSuccess = {
                        transaksiSubject.removeTransaksi(transaksi.id)
                        loadTransaksi(transaksi.userID)
                        _error.value = null
                    },
                    onFailure = {
                        _error.value = it.message
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun undoLastAction() {
        viewModelScope.launch {
            if (commandInvoker.canUndo()) {
                val result = commandInvoker.undo()
                result.fold(
                    onSuccess = {
                        // Reload current user's transactions
                        val currentUserId = _transaksiList.value.firstOrNull()?.userID
                        currentUserId?.let { loadTransaksi(it) }
                    },
                    onFailure = {
                        _error.value = "Gagal membatalkan aksi: ${it.message}"
                    }
                )
            }
        }
    }
    
    fun redoLastAction() {
        viewModelScope.launch {
            if (commandInvoker.canRedo()) {
                val result = commandInvoker.redo()
                result.fold(
                    onSuccess = {
                        // Reload current user's transactions
                        val currentUserId = _transaksiList.value.firstOrNull()?.userID
                        currentUserId?.let { loadTransaksi(it) }
                    },
                    onFailure = {
                        _error.value = "Gagal mengulangi aksi: ${it.message}"
                    }
                )
            }
        }
    }
    
    fun canUndo(): Boolean = commandInvoker.canUndo()
    fun canRedo(): Boolean = commandInvoker.canRedo()
    
    fun getTransaksiSubject(): TransaksiSubject = transaksiSubject
    
    fun clearError() {
        _error.value = null
    }
}
