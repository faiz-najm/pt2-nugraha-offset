package com.d3if4503.typewriter.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.d3if4503.typewriter.AuthActivity
import com.d3if4503.typewriter.model.User
import com.example.aexpress.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore

class ProfileFragment : Fragment() {

    private val db = Firebase.database
    private val databaseRef = db.reference

    private val layoutDataStore by lazy { SettingDataStore(requireActivity().dataStore) }

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding

    // make tag for log
    private val TAG = ProfileFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        auth = Firebase.auth
        binding = FragmentProfileBinding.inflate(layoutInflater)

        databaseRef.child("users").child(auth.currentUser?.uid.toString()).get()
            .addOnSuccessListener {
                val user = it.getValue(User::class.java)
                binding.textViewDisplayName.setText(auth.currentUser?.displayName)
                binding.textViewEmail.setText(auth.currentUser?.email)
                binding.displayNameEditText.setText(auth.currentUser?.displayName)
                binding.emailEditText.setText(auth.currentUser?.email)
                binding.passwordEditText.setText("")
                binding.addressEditText.setText(user?.alamat ?: "")
                binding.phoneNumberEditText.setText(auth.currentUser?.phoneNumber)
            }.addOnFailureListener {
                Log.e(TAG, "Error getting data", it)
            }

        // Mengatur onClickListener untuk tombol Save
        binding.saveButton.setOnClickListener { saveProfile() }

        // Mengatur onClickListener untuk tombol Logout
        binding.logoutButton.setOnClickListener { logout() }

        // Mengatur onClickListener untuk gambar profil
        binding.imageViewPic.setOnClickListener { openImagePicker() }

        return binding.root
    }

    private fun saveProfile() {
        // Mendapatkan nilai dari EditText
        val displayName = binding.displayNameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val address = binding.addressEditText.text.toString()
        val phoneNumber = binding.phoneNumberEditText.text.toString()

        // Melakukan validasi input (contoh: tidak boleh kosong)
        if (displayName.isEmpty() || email.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Harap lengkapi semua field", Toast.LENGTH_SHORT)
                .show()
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Email tidak valid", Toast.LENGTH_SHORT).show()
            return
        } else if (password.isNotEmpty()) {
            // Data yang akan diupdate
            val userAuth = auth.currentUser
            // password update
            if (password.length >= 8) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Peringatan")
                    .setMessage("Mengubah password akan mengeluarkan Anda dari sesi login. Apakah Anda yakin ingin melanjutkan?")
                    .setPositiveButton("Ya") { _, _ ->
                        userAuth?.updatePassword(password)
                        binding.passwordEditText.setText("")
                        writeUpdateUser(displayName, email, address, phoneNumber)
                    }
                    .setNegativeButton("Tidak") { _, _ ->
                        binding.passwordEditText.setText("")
                    }
                    .show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Password harus lebih dari 8 karakter",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        } else {
            writeUpdateUser(displayName, email, address, phoneNumber)
        }
    }

    private fun logout() {
        // Melakukan logika logout (misalnya membersihkan sesi, menghapus token, dll.)
        databaseRef.child("users").child(auth.uid as String).child("status")
            .setValue("offline")

        auth.signOut()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    layoutDataStore.saveAuthKey("0", requireContext())
                }
            }
        }

        // Kembali ke halaman login
        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            try {
                // Menggunakan Picasso untuk memuat gambar ke ImageView
                Picasso.get().load(imageUri).into(binding.imageViewPic)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun writeUpdateUser(
        displayName: String,
        email: String,
        address: String,
        phoneNumber: String
    ) {
        val userAuth = auth.currentUser
        // Display name update
        userAuth?.updateProfile(
            com.google.firebase.auth.ktx.userProfileChangeRequest {
                this.displayName = displayName
            }
        )

        //email update
        userAuth?.updateEmail(email)

        // alamat update
        databaseRef.child("users").child(auth.uid.toString()).child("alamat").setValue(address)

        Toast.makeText(requireContext(), "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
    }
}