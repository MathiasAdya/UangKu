package com.example.uangku.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uangku.model.Transaksi
import java.text.SimpleDateFormat
import java.util.*

class TransaksiViewModel : ViewModel() {
    private val _transaksi = MutableLiveData<List<Transaksi>>(emptyList())
    val transaksi: LiveData<List<Transaksi>> get() = _transaksi

    fun tambahTransaksi(
        deskripsi: String,
        jumlah: Double,
        jenis: String,
        kategoriID: String,
        userID: String
    ) {
        val id = UUID.randomUUID().toString()
        val now = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val t = Transaksi(
            id = id,
            deskripsi = deskripsi,
            jumlah = jumlah,
            tanggal = now,
            kategoriID = kategoriID,
            userID = userID
        )
        _transaksi.value = _transaksi.value?.plus(t)
    }

    fun hapusTransaksi(id: String) {
        _transaksi.value = _transaksi.value?.filterNot { it.id == id }
    }

    fun editTransaksi(id: String, newData: Transaksi) {
        _transaksi.value = _transaksi.value?.map {
            if (it.id == id) newData else it
        }
    }
}
