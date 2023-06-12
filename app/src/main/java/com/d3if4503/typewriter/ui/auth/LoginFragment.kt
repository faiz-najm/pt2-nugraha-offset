package com.d3if4503.typewriter.ui.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.aexpress.R
import com.example.aexpress.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore

class LoginFragment : Fragment(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener {

    internal val layoutDataStore by lazy { SettingDataStore(requireActivity().dataStore) }

    private val viewModel: AuthViewModel by lazy {
        ViewModelProvider(this)[AuthViewModel::class.java]
    }

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentLoginBinding.inflate(layoutInflater)

        auth = Firebase.auth

        val user = auth.currentUser?.email

        binding.emailOrPhoneInp.onFocusChangeListener = this
        binding.passwordInp.onFocusChangeListener = this

        binding.btnLogin.setOnClickListener {
            if (validateInputEmail(binding.emailOrPhoneInp.text.toString()) &&
                validateInputPassword(binding.passwordInp.text.toString())
            ) {
                viewModel.login(
                    this@LoginFragment,
                    binding.emailOrPhoneInp.text.toString(),
                    binding.passwordInp.text.toString()
                )

            } else {
                validateInputEmail(binding.emailOrPhoneInp.text.toString())
                validateInputPassword(binding.passwordInp.text.toString())
            }
        }

        binding.btnBuatAkun.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }
        return binding.root
    }

    override fun onClick(view: View?) {
        TODO("Not yet implemented")
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                binding.emailOrPhoneInp.id -> {
                    if (hasFocus) {
                        binding.emailHint.error = null
                    }
                }

                binding.passwordInp.id -> {
                    if (hasFocus) {
                        binding.passwordHint.error = null
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (view != null) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                // validate form
                validateInputEmail(binding.emailOrPhoneInp.text.toString())
                validateInputPassword(binding.passwordInp.text.toString())
                return true
            }
        }
        return false
    }

    private fun validateInputEmail(value: String): Boolean {
        if (value.isEmpty()) {
            binding.emailHint.error = "Tidak boleh kosong"
            return false
        }
        return true
    }

    fun validateInputPassword(value: String): Boolean {
        viewModel.validateInputEmail(value).apply {
            if (value.isEmpty()) {
                binding.passwordHint.error = "Password tidak boleh kosong"
                return false
            }
        }
        return true
    }
}