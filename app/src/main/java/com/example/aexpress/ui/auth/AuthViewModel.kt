package com.example.aexpress.ui.auth

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    fun validateInputNama(value: String): String? {
        var error: String? = null

        if (value.isEmpty())
            error = "Nama lengkap tidak boleh kosong"
        else if (value.length < 3)
            error = "Nama lengkap minimal 3 karakter"

        return error
    }

    fun validateInputEmail(value: String): String? {
        var error: String? = null

        if (value.isEmpty())
            error = "Email tidak boleh kosong"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches())
            error = "Email tidak valid"

        return error
    }

    fun validateInputPassword(value: String): String? {
        var error: String? = null

        if (value.isEmpty())
            error = "Password tidak boleh kosong"
        else if (value.length < 6)
            error = "Password minimal 6 karakter"

        return error
    }

    fun validateInputKonfirmasiPassword(value: String, password: String): String? {
        var error: String? = null

        if (value.isEmpty())
            error = "Konfirmasi password tidak boleh kosong"
        else if (value != password)
            error = "Konfirmasi password tidak sama dengan password"

        return error
    }

    fun validateInputNoHp(value: String): String? {
        var error: String? = null

        if (value.isEmpty())
            error = "No. HP tidak boleh kosong"
        return error
    }
}