package com.example.uangku.model

data class Pengguna(
    val userID: String,
    val nama: String,
    val email: String,
    val passwordHash: String
) {
    fun registrasi(nama: String, email: String, password: String): Pengguna {
        return Pengguna(
            userID = generateId(),
            nama = nama,
            email = email,
            passwordHash = hash(password)
        )
    }

    fun login(email: String, password: String): Boolean {
        return this.email == email && this.passwordHash == hash(password)
    }

    fun logout() {
        // sesi logout dummy
    }

    fun tambahTransaksi(transaksi: Transaksi) {
        // tambah transaksi ke DB (dummy)
    }

    fun editTransaksi(transaksiID: String, dataUpdate: Transaksi) {
        // update transaksi di DB
    }

    fun hapusTransaksi(transaksiID: String) {
        // hapus transaksi
    }

    fun lihatRiwayatTransaksi(): List<Transaksi> {
        return emptyList() // dummy
    }

    fun aturAnggaran(data: Anggaran) {
        // simpan anggaran
    }

    fun lihatLaporanKeuangan(rentangWaktu: Pair<String, String>): Laporan {
        return Laporan.generateLaporan(emptyList(), "Bulanan", rentangWaktu.first, rentangWaktu.second)
    }

    fun buatPengingat(p: Pengingat) {
        // simpan pengingat
    }

    fun simpanKeCloud() {
        // dummy simpan ke cloud
    }

    private fun hash(password: String): String = password.reversed() // mock hash
    private fun generateId(): String = java.util.UUID.randomUUID().toString()
}
