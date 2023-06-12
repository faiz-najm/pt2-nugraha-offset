package com.d3if4503.typewriter.ui.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.telephony.PhoneNumberUtils
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
        ViewModelProvider(this)[AuthViewModel::class.java]
    }

    private var _binding: FragmentRegisterBinding? = null
    internal val binding get() = _binding!!

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

        binding.nohpInp.setOnKeyListener(this)

        binding.btnPunyaAkun.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnDaftar.setOnClickListener {
            if (validasiAllData()) {
                val nama = binding.nameInp.text.toString()
                val email = binding.emailInp.text.toString()
                val password = binding.passwordInp.text.toString()
                val nohp = binding.nohpInp.text.toString()

                viewModel.register(this, nama, email, password, nohp)
            }
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        /*if (view != null) {
            when (view.id) {
                binding.emailHint.id -> {
                    binding.emailInp.error = null
                }
            }
        }*/
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
                        val formattedNumber =
                            PhoneNumberUtils.formatNumber(binding.nohpInp.text.toString(), "ID")
                        binding.nohpInp.setText(formattedNumber)
                        binding.nohpInp.setSelection(formattedNumber.length)
                    }
                }
            }
        }
    }


    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {

        if (view != null) {
            when (view.id) {
                binding.nohpInp.id -> {
                    // jika phone number berasal dari indonesia
                    var formattedNumber =
                        PhoneNumberUtils.formatNumber(binding.nohpInp.text.toString(), "ID")

                    if (binding.nohpInp.text.toString().length == 10) {
                        // validasi nomor hp ke format indonesia
                        if (formattedNumber.startsWith("0")) {
                            formattedNumber = formattedNumber.replaceFirst("0", "+62 ")
                        } else if (!formattedNumber.startsWith("+62")) {
                            formattedNumber = "+62 $formattedNumber"
                        } else if (formattedNumber.startsWith("+62")) {
                            formattedNumber = formattedNumber.replaceFirst("+62", "+62 ")
                        } else if (formattedNumber.startsWith("62")) {
                            formattedNumber = formattedNumber.replaceFirst("62", "+62 ")
                        }
                        binding.nohpInp.setText(formattedNumber)
                        binding.nohpInp.setSelection(binding.nohpInp.text.toString().length)
                    }
                }
            }
        }

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

    private fun validasiAllData() = validateInputNama(binding.nameInp.text.toString()) &&
            validateInputEmail(binding.emailInp.text.toString()) &&
            validateInputPassword(binding.passwordInp.text.toString()) &&
            validateInputKonfirmasiPassword(
                binding.konfirmasiPasswordInp.text.toString(),
                binding.passwordInp.text.toString()
            ) &&
            validateInputNoHp(binding.nohpInp.text.toString())

}