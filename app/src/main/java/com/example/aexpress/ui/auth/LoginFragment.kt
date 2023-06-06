package com.example.aexpress.ui.auth

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

class LoginFragment : Fragment(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: AuthViewModel by lazy {
        val factory = AuthViewModelFactory()
        ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentLoginBinding.inflate(layoutInflater)

        binding.emailInp.onFocusChangeListener = this
        binding.passwordInp.onFocusChangeListener = this

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
            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (view != null) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                // validate form
                validateInputEmail(binding.emailInp.text.toString())
                validateInputPassword(binding.passwordInp.text.toString())
                return true
            }

            when (view.id) {
                binding.emailInp.id -> {
                    if (event?.action == KeyEvent.ACTION_DOWN) {
                        viewModel.validateInputEmail(binding.emailInp.text.toString())
                        return true
                    }
                }

                binding.passwordInp.id -> {
                    if (event?.action == KeyEvent.ACTION_DOWN) {
                        validateInputPassword(binding.passwordInp.text.toString())
                        return true
                    }
                }
            }
        }
        return false
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
}