package com.example.uangku.viewmodel

import androidx.lifecycle.ViewModel
import com.example.uangku.model.Pengguna

class LoginViewModel : ViewModel() {
    private val dummyUser = Pengguna(
        userID = "user001",
        nama = "Admin",
        email = "admin@example.com",
        passwordHash = hash("admin123")
    )

    fun login(email: String, password: String): Boolean {
        return dummyUser.login(email, password)
    }

    private fun hash(pass: String) = pass.reversed() // mock hash
}
