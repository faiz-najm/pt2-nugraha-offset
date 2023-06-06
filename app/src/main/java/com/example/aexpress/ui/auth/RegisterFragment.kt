package com.example.aexpress.ui.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.aexpress.R
import com.example.aexpress.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private val viewModel: AuthViewModel by lazy {
        val factory = AuthViewModelFactory()
        ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.nameInp.onFocusChangeListener = this
        binding.emailInp.onFocusChangeListener = this
        binding.passwordInp.onFocusChangeListener = this
        binding.konfirmasiPasswordInp.onFocusChangeListener = this
        binding.nohpInp.onFocusChangeListener = this

        binding.btnPunyaAkun.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                binding.nameInp.id -> {
                    if (hasFocus) {
                        binding.nameHint.error = null
                    } else {
                        validateInputNama(binding.nameInp.text.toString())
                    }
                }

                binding.emailInp.id -> {
                    if (hasFocus) {
                        binding.emailHint.error = null
                    } else {
                        validateInputEmail(binding.emailInp.text.toString())
                    }
                }

                binding.passwordInp.id -> {
                    if (hasFocus) {
                        binding.passwordHint.error = null
                    } else {
                        validateInputPassword(binding.passwordInp.text.toString())
                    }
                }

                binding.konfirmasiPasswordInp.id -> {
                    if (hasFocus) {
                        binding.konfirmasiPasswordHint.error = null
                    } else {
                        validateInputKonfirmasiPassword(
                            binding.konfirmasiPasswordInp.text.toString(),
                            binding.passwordInp.text.toString()
                        )
                    }
                }

                binding.nohpInp.id -> {
                    if (hasFocus) {
                        binding.nohpHint.error = null
                    } else {
                        validateInputNoHp(binding.nohpInp.text.toString())
                    }
                }
            }
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {

        return false
    }

    private fun validateInputNama(value: String): Boolean {
        viewModel.validateInputNama(value).apply {
            if (this != null) {
                binding.nameHint.error = this
                return false
            }
        }
        return true
    }

    private fun validateInputEmail(value: String): Boolean {
        viewModel.validateInputEmail(binding.emailInp.text.toString()).apply {
            if (this != null) {
                binding.emailHint.error = this
                return false
            }
        }
        return true
    }

    private fun validateInputPassword(value: String): Boolean {
        viewModel.validateInputPassword(value).apply {
            if (this != null) {
                binding.passwordHint.error = this
                return false
            }
        }
        return true
    }

    private fun validateInputKonfirmasiPassword(value: String, password: String): Boolean {
        viewModel.validateInputKonfirmasiPassword(value, password).apply {
            if (this != null) {
                binding.konfirmasiPasswordHint.error = this
                return false
            }
        }
        return true
    }

    private fun validateInputNoHp(value: String): Boolean {
        viewModel.validateInputNoHp(value).apply {
            if (this != null) {
                binding.nohpHint.error = this
                return false
            }
        }
        return true
    }
}