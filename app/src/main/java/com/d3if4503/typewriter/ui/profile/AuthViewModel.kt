package com.d3if4503.typewriter.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {

    private val db = Firebase.database
    private val databaseRef = db.reference

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

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
        else if (value.length < 8)
            error = "Password minimal 8 karakter"

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
        else if (value.length < 10)
            error = "No. HP minimal 10 karakter"
        return error
    }
}
