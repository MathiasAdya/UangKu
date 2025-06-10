package com.example.uangku.pattern.observer

/**
 * Observer Pattern - Interface untuk observer
 */
interface Observer<T> {
    fun onUpdate(data: T)
}

/**
 * Observer Pattern - Interface untuk subject yang dapat diobservasi
 */
interface Subject<T> {
    fun addObserver(observer: Observer<T>)
    fun removeObserver(observer: Observer<T>)
    fun notifyObservers(data: T)
}

/**
 * Observer Pattern - Implementasi konkret untuk mengelola data transaksi
 */
class TransaksiSubject : Subject<List<com.example.uangku.model.Transaksi>> {
    private val observers = mutableListOf<Observer<List<com.example.uangku.model.Transaksi>>>()
    private var transaksiList = mutableListOf<com.example.uangku.model.Transaksi>()
    
    override fun addObserver(observer: Observer<List<com.example.uangku.model.Transaksi>>) {
        observers.add(observer)
    }
    
    override fun removeObserver(observer: Observer<List<com.example.uangku.model.Transaksi>>) {
        observers.remove(observer)
    }
    
    override fun notifyObservers(data: List<com.example.uangku.model.Transaksi>) {
        observers.forEach { it.onUpdate(data) }
    }
    
    fun addTransaksi(transaksi: com.example.uangku.model.Transaksi) {
        transaksiList.add(transaksi)
        notifyObservers(transaksiList.toList())
    }
    
    fun updateTransaksi(transaksiId: String, updatedTransaksi: com.example.uangku.model.Transaksi) {
        val index = transaksiList.indexOfFirst { it.id == transaksiId }
        if (index != -1) {
            transaksiList[index] = updatedTransaksi
            notifyObservers(transaksiList.toList())
        }
    }
    
    fun removeTransaksi(transaksiId: String) {
        if (transaksiList.removeIf { it.id == transaksiId }) {
            notifyObservers(transaksiList.toList())
        }
    }
    
    fun getTransaksiList(): List<com.example.uangku.model.Transaksi> = transaksiList.toList()
}

/**
 * Observer Pattern - Observer untuk UI yang menampilkan total saldo
 */
class SaldoObserver : Observer<List<com.example.uangku.model.Transaksi>> {
    override fun onUpdate(data: List<com.example.uangku.model.Transaksi>) {
        val totalPemasukan = data.filterIsInstance<com.example.uangku.model.TransaksiPemasukan>()
            .sumOf { it.jumlah }
        val totalPengeluaran = data.filterIsInstance<com.example.uangku.model.TransaksiPengeluaran>()
            .sumOf { it.jumlah }
        val saldo = totalPemasukan - totalPengeluaran
        
        println("Saldo diupdate: Rp ${String.format("%,.2f", saldo)}")
        // Disini bisa update UI komponen yang menampilkan saldo
    }
}

/**
 * Observer Pattern - Observer untuk mendeteksi peringatan anggaran
 */
class AnggaranObserver(private val anggaran: com.example.uangku.model.Anggaran) : Observer<List<com.example.uangku.model.Transaksi>> {
    override fun onUpdate(data: List<com.example.uangku.model.Transaksi>) {
        val totalPengeluaran = data.filterIsInstance<com.example.uangku.model.TransaksiPengeluaran>()
            .filter { anggaran.kategoriID == null || it.kategoriID == anggaran.kategoriID }
            .sumOf { it.jumlah }
        
        val persentaseAnggaran = (totalPengeluaran / anggaran.jumlahAnggaran) * 100
        
        when {
            persentaseAnggaran >= 100 -> {
                println("⚠️ PERINGATAN: Anggaran terlampaui! ${String.format("%.1f", persentaseAnggaran)}%")
            }
            persentaseAnggaran >= 80 -> {
                println("⚠️ PERINGATAN: Anggaran hampir habis! ${String.format("%.1f", persentaseAnggaran)}%")
            }
            persentaseAnggaran >= 50 -> {
                println("ℹ️ INFO: Anggaran terpakai ${String.format("%.1f", persentaseAnggaran)}%")
            }
        }
    }
}
