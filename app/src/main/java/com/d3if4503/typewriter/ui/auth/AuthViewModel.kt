package com.d3if4503.typewriter.ui.auth

import android.content.Intent
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.d3if4503.typewriter.MainActivity
import com.d3if4503.typewriter.VerifyPhoneActivity
import com.d3if4503.typewriter.activities.MainFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

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

    fun login(
        fragment: LoginFragment,
        email: String,
        password: String
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val uid = auth.currentUser?.uid

                    Log.d(TAG, "signInWithEmail:success")

                    databaseRef.child("users").child(uid!!).child("status").setValue("online")

                    fragment.viewLifecycleOwner.lifecycleScope.launch {
                        fragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            launch {
                                fragment.layoutDataStore.saveAuthKey(
                                    uid,
                                    fragment.requireContext()
                                )
                            }
                        }
                    }

                    // main activity intent
                    val intent = Intent(fragment.requireContext(), MainActivity::class.java)

                    fragment.startActivity(intent)
                    fragment.requireActivity().finish()

                    Toast.makeText(
                        fragment.requireContext(),
                        "Login berhasil",
                        Toast.LENGTH_SHORT,
                    ).show()

                } else {

                    // If sign in fails, display a message to the user.
                    when (task.exception?.message) {
                        "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> {
                            Toast.makeText(
                                fragment.requireContext(),
                                "Tidak ada koneksi internet",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }

                        "The password is invalid or the user does not have a password." -> {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)

                            Toast.makeText(
                                fragment.requireContext(),
                                "Email atau password salah",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }

                        "There is no user record corresponding to this identifier. The user may have been deleted." -> {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)

                            Toast.makeText(
                                fragment.requireContext(),
                                "Email atau password salah",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                fragment.requireContext(),
                                "Terjadi kesalahan",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                    /*Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        fragment.requireContext(),
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()*/
                }
            }
        // [END sign_in_with_email]
    }

    private fun loginWithPhoneNumber(
        fragment: LoginFragment,
        phoneNumber: String,
        password: String
    ) {
        val formattedPhone = PhoneNumberUtils.formatNumber(phoneNumber, "ID")

        auth.fetchSignInMethodsForEmail(formattedPhone)
            .addOnSuccessListener { result ->
                if (result.signInMethods?.size == 0) {
                    Toast.makeText(
                        fragment.requireContext(),
                        "No. HP belum terdaftar",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    auth.signInWithEmailAndPassword(formattedPhone, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success")

                                // main activity intent
                                val intent =
                                    Intent(fragment.requireContext(), MainFragment::class.java)
                                auth.currentUser?.uid?.let {
                                    databaseRef.child("users").child(it).child("status")
                                        .setValue("online")
                                }

                                // passing data to main activity
                                intent.putExtra("email", formattedPhone)
                                intent.putExtra("password", password)

                                fragment.startActivity(intent)
                                fragment.activity?.finish()

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(
                                    fragment.requireContext(),
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(
                    fragment.requireContext(),
                    it.message,
                    Toast.LENGTH_SHORT,
                ).show()
            }

    }

    fun register(
        fragment: RegisterFragment,
        nama: String,
        email: String,
        password: String,
        nohp: String
    ) {
        var formattedPhone = nohp

        if (formattedPhone.startsWith("0")) {
            formattedPhone = formattedPhone.replaceFirst("0", "+62 ")
        } else if (!formattedPhone.startsWith("+62")) {
            formattedPhone = "+62 $formattedPhone"
        } else if (formattedPhone.startsWith("+62")) {
            formattedPhone = formattedPhone.replaceFirst("+62", "+62 ")
        } else if (formattedPhone.startsWith("62")) {
            formattedPhone = formattedPhone.replaceFirst("62", "+62 ")
        }

        formattedPhone = PhoneNumberUtils.formatNumber(formattedPhone, "ID")

        auth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener {
                if (it.signInMethods?.size == 0) {
                    val intent = Intent(fragment.requireContext(), VerifyPhoneActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("password", password)
                    intent.putExtra("phone", formattedPhone)
                    intent.putExtra("nama", nama)
                    fragment.startActivity(intent)
                    fragment.activity?.finish()
                } else {
                    fragment.binding.emailHint.error = "Email sudah terdaftar"
                }
            }.addOnFailureListener {
                when (it.message) {
                    "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> {
                        Toast.makeText(
                            fragment.requireContext(),
                            "Tidak ada koneksi internet",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }

                    else -> {
                        Toast.makeText(
                            fragment.requireContext(),
                            it.message,
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }

        fun isPhoneNumber(input: String): Boolean {
            if (input.startsWith("08")) {
                return true
            } else if (input.startsWith("+62")) {
                return true
            } else if (input.startsWith("+")) {
                return true
            }
            return false
        }
    }
}
