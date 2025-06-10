package com.example.uangku.pattern.singleton

import com.example.uangku.model.*

/**
 * Singleton Pattern - DatabaseManager
 * Mengelola koneksi database dan operasi CRUD secara terpusat
 */
class DatabaseManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: DatabaseManager? = null
        
        fun getInstance(): DatabaseManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseManager().also { INSTANCE = it }
            }
        }
    }
    
    // Simulasi storage
    private val transaksiList = mutableListOf<Transaksi>()
    private val penggunaList = mutableListOf<Pengguna>()
    private val kategoriList = mutableListOf<KategoriTransaksi>()
    private val anggaranList = mutableListOf<Anggaran>()
    
    // Transaction operations
    fun saveTransaksi(transaksi: Transaksi): Boolean {
        return try {
            transaksiList.add(transaksi)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getTransaksiByUserId(userId: String): List<Transaksi> {
        return transaksiList.filter { it.userID == userId }
    }
    
    fun updateTransaksi(transaksiId: String, updatedTransaksi: Transaksi): Boolean {
        val index = transaksiList.indexOfFirst { it.id == transaksiId }
        return if (index != -1) {
            transaksiList[index] = updatedTransaksi
            true
        } else false
    }
    
    fun deleteTransaksi(transaksiId: String): Boolean {
        return transaksiList.removeIf { it.id == transaksiId }
    }
    
    // User operations
    fun savePengguna(pengguna: Pengguna): Boolean {
        return try {
            penggunaList.add(pengguna)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getPenggunaByEmail(email: String): Pengguna? {
        return penggunaList.find { it.email == email }
    }
    
    // Category operations
    fun saveKategori(kategori: KategoriTransaksi): Boolean {
        return try {
            kategoriList.add(kategori)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getKategoriByUserId(userId: String): List<KategoriTransaksi> {
        return kategoriList.filter { it.userID == userId }
    }
    
    // Budget operations
    fun saveAnggaran(anggaran: Anggaran): Boolean {
        return try {
            anggaranList.add(anggaran)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getAnggaranByUserId(userId: String): List<Anggaran> {
        return anggaranList.filter { it.userID == userId }
    }
    
    fun clearAllData() {
        transaksiList.clear()
        penggunaList.clear()
        kategoriList.clear()
        anggaranList.clear()
    }
}
