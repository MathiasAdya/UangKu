package com.example.uangku.pattern.repository

import com.example.uangku.model.*
import com.example.uangku.pattern.singleton.DatabaseManager

/**
 * Repository Pattern - Interface untuk operasi data transaksi
 */
interface TransaksiRepository {
    suspend fun saveTransaksi(transaksi: Transaksi): Result<Boolean>
    suspend fun getTransaksiByUserId(userId: String): Result<List<Transaksi>>
    suspend fun updateTransaksi(transaksiId: String, transaksi: Transaksi): Result<Boolean>
    suspend fun deleteTransaksi(transaksiId: String): Result<Boolean>
    suspend fun getTransaksiByKategori(userId: String, kategoriId: String): Result<List<Transaksi>>
    suspend fun getTransaksiByDateRange(userId: String, startDate: String, endDate: String): Result<List<Transaksi>>
}

/**
 * Repository Pattern - Implementasi lokal untuk data transaksi
 */
class LocalTransaksiRepository : TransaksiRepository {
    private val dbManager = DatabaseManager.getInstance()
    
    override suspend fun saveTransaksi(transaksi: Transaksi): Result<Boolean> {
        return try {
            val success = dbManager.saveTransaksi(transaksi)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransaksiByUserId(userId: String): Result<List<Transaksi>> {
        return try {
            val transaksiList = dbManager.getTransaksiByUserId(userId)
            Result.success(transaksiList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateTransaksi(transaksiId: String, transaksi: Transaksi): Result<Boolean> {
        return try {
            val success = dbManager.updateTransaksi(transaksiId, transaksi)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteTransaksi(transaksiId: String): Result<Boolean> {
        return try {
            val success = dbManager.deleteTransaksi(transaksiId)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransaksiByKategori(userId: String, kategoriId: String): Result<List<Transaksi>> {
        return try {
            val allTransaksi = dbManager.getTransaksiByUserId(userId)
            val filtered = allTransaksi.filter { it.kategoriID == kategoriId }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransaksiByDateRange(userId: String, startDate: String, endDate: String): Result<List<Transaksi>> {
        return try {
            val allTransaksi = dbManager.getTransaksiByUserId(userId)
            // Simplified date filtering - in real app, use proper date parsing
            val filtered = allTransaksi.filter { 
                it.tanggal >= startDate && it.tanggal <= endDate 
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Repository Pattern - Repository yang menggabungkan lokal dan cloud
 */
class CloudTransaksiRepository : TransaksiRepository {
    private val localRepo = LocalTransaksiRepository()
    private val cloudStorage = CloudStorage()
    
    override suspend fun saveTransaksi(transaksi: Transaksi): Result<Boolean> {
        return try {
            // Save to local first
            val localResult = localRepo.saveTransaksi(transaksi)
            
            if (localResult.isSuccess) {
                // Try to sync to cloud
                try {
                    cloudStorage.simpanTransaksi(transaksi)
                } catch (e: Exception) {
                    // Cloud save failed, but local succeeded
                    println("Warning: Failed to sync to cloud - ${e.message}")
                }
            }
            
            localResult
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransaksiByUserId(userId: String): Result<List<Transaksi>> {
        return try {
            // Try cloud first, fallback to local
            try {
                val cloudData = cloudStorage.ambilSemuaTransaksi(userId)
                Result.success(cloudData)
            } catch (e: Exception) {
                println("Cloud unavailable, using local data")
                localRepo.getTransaksiByUserId(userId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateTransaksi(transaksiId: String, transaksi: Transaksi): Result<Boolean> {
        val localResult = localRepo.updateTransaksi(transaksiId, transaksi)
        
        if (localResult.isSuccess) {
            try {
                cloudStorage.simpanTransaksi(transaksi)
            } catch (e: Exception) {
                println("Warning: Failed to sync update to cloud")
            }
        }
        
        return localResult
    }
    
    override suspend fun deleteTransaksi(transaksiId: String): Result<Boolean> {
        val localResult = localRepo.deleteTransaksi(transaksiId)
        
        if (localResult.isSuccess) {
            try {
                //cloudStorage.hapusTransaksi(transaksiId)
            } catch (e: Exception) {
                println("Warning: Failed to sync deletion to cloud")
            }
        }
        
        return localResult
    }
    
    override suspend fun getTransaksiByKategori(userId: String, kategoriId: String): Result<List<Transaksi>> {
        return localRepo.getTransaksiByKategori(userId, kategoriId)
    }
    
    override suspend fun getTransaksiByDateRange(userId: String, startDate: String, endDate: String): Result<List<Transaksi>> {
        return localRepo.getTransaksiByDateRange(userId, startDate, endDate)
    }
}
