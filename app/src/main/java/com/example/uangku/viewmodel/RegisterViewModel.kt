package com.example.uangku.viewmodel

import androidx.lifecycle.ViewModel
import com.example.uangku.model.Pengguna

class RegisterViewModel : ViewModel() {
    fun register(nama: String, email: String, password: String): Pengguna {
        return Pengguna(
            userID = java.util.UUID.randomUUID().toString(),
            nama = nama,
            email = email,
            passwordHash = hash(password)
        )
    }

    private fun hash(pass: String) = pass.reversed()
}
