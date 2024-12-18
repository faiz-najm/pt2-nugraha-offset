package com.d3if4503.typewriter

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.d3if4503.typewriter.activities.MainFragment
import com.d3if4503.typewriter.model.User
import com.example.aexpress.R
import com.example.aexpress.databinding.ActivityVerifyPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import java.util.concurrent.TimeUnit

class VerifyPhoneActivity : AppCompatActivity(), View.OnKeyListener, View.OnFocusChangeListener {

    private val layoutDataStore by lazy { SettingDataStore(this.dataStore) }

    var otpValid = true
    lateinit var email: String
    lateinit var password: String
    lateinit var phone: String
    lateinit var nama: String

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: ForceResendingToken
    private lateinit var callbacks: OnVerificationStateChangedCallbacks

    private lateinit var binding: ActivityVerifyPhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifyPhoneBinding.inflate(layoutInflater)

        setContentView(binding.root)

        email = intent.getStringExtra("email").toString()
        password = intent.getStringExtra("password").toString()
        phone = intent.getStringExtra("phone").toString()
        nama = intent.getStringExtra("nama").toString()

        auth = Firebase.auth
        database = Firebase.database.reference

        binding.verifyPhoneBTn.visibility = View.GONE

        binding.otpNumberOne.setOnKeyListener(this)
        binding.optNumberTwo.setOnKeyListener(this)
        binding.otpNumberThree.setOnKeyListener(this)
        binding.otpNumberFour.setOnKeyListener(this)
        binding.otpNumberFive.setOnKeyListener(this)
        binding.otpNumberSix.setOnKeyListener(this)

        binding.otpNumberOne.onFocusChangeListener = this
        binding.optNumberTwo.onFocusChangeListener = this
        binding.otpNumberThree.onFocusChangeListener = this
        binding.otpNumberFour.onFocusChangeListener = this
        binding.otpNumberFive.onFocusChangeListener = this
        binding.otpNumberSix.onFocusChangeListener = this

        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.textViewCountdown.text =
                    "Mohon tunggu ${millisUntilFinished / 1000} detik untuk mengirim ulang"
            }

            override fun onFinish() {
            }
        }

        callbacks = object : OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                // called after the OTP has been sent
                storedVerificationId = verificationId
                resendToken = token

                val spannableString = SpannableString(phone)

                spannableString.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    0,
                    phone.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // bold phone number
                binding.textInfoNumber.text = spannableString

                binding.verifyPhoneBTn.visibility = View.VISIBLE
                binding.otpNumberOne.requestFocus()

                Log.d(TAG, "onCodeSent:$verificationId")
                timer.start()
            }

            override fun onCodeAutoRetrievalTimeOut(s: String) {
                super.onCodeAutoRetrievalTimeOut(s)
                // called when the timeout duration has passed without triggering onVerificationCompleted
                Log.d(TAG, "onCodeAutoRetrievalTimeOut:$s")

                val spannableString = SpannableString("Tidak menerima kode? Kirim Ulang")

                setClickableSpan(spannableString, "Kirim Ulang", Color.BLUE) {
                    // Code for the action when "Kirim ulang" is clicked
                    resendVerificationCode(phone, resendToken)
                }

                timer.cancel()
                binding.textViewCountdown.text = spannableString
                binding.textViewCountdown.movementMethod = LinkMovementMethod.getInstance()
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                // called when the device has SMS auto-retrieval capability, or the phone number can be instantly verified without needing a code
                Log.d(TAG, "onVerificationCompleted:Success")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                Log.w(TAG, "onVerificationFailed", e)

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid request
                        // [START_EXCLUDE]
                        Log.d(TAG, "Invalid credential: " + e.message)
                    }

                    is FirebaseTooManyRequestsException -> {
                        // The SMS quota for the project has been exceeded
                        Log.d(TAG, "SMS Quota exceeded.")

                    }

                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        // reCAPTCHA verification attempted with null Activity
                        Log.d(TAG, "Missing ReCaptcha Activity")
                    }
                }

                // Go back to the previous activity
                startActivity(Intent(this@VerifyPhoneActivity, AuthActivity::class.java))
            }
        }

        binding.verifyPhoneBTn.setOnClickListener {

            Toast.makeText(
                this,
                "Button Clicked",
                Toast.LENGTH_SHORT
            ).show()

            validateField(binding.otpNumberOne)
            validateField(binding.optNumberTwo)
            validateField(binding.otpNumberThree)
            validateField(binding.otpNumberFour)
            validateField(binding.otpNumberFive)
            validateField(binding.otpNumberSix)

            if (otpValid) {
                // send otp to the user
                val otp =
                    binding.otpNumberOne.text.toString() + binding.optNumberTwo.text.toString() + binding.otpNumberThree.text.toString() + binding.otpNumberFour.text.toString() + binding.otpNumberFive.text.toString() + binding.otpNumberSix.text.toString()
                verifyPhoneNumberWithCode(storedVerificationId!!, otp)
            }
        }

        startPhoneNumberVerification(phone)
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId!!, code)

        // check credential here
        verifyUserEmailAndPassword(phoneAuthCredential)
        // [END verify_with_code]
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: ForceResendingToken?,
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // (optional) Activity for callback binding
            // If no activity is passed, reCAPTCHA verification can not be used.
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]

    fun validateField(field: EditText) {
        if (field.text.toString().isEmpty()) {
            field.error = "Required"
            field.requestFocus()
            otpValid = false
        } else {
            otpValid = true
        }
    }

    private fun verifyUserEmailAndPassword(phoneAuthCredential: PhoneAuthCredential) {

        // start phone auth activity
        auth.signInWithCredential(phoneAuthCredential)
            .addOnSuccessListener {

                verifyLinkAuthentication(EmailAuthProvider.getCredential(email, password))

                Log.d(TAG, "verifyUserEmailAndPassword:Success")
            }.addOnFailureListener {

                Log.d(
                    TAG, "verifyUserEmailAndPassword: ${it.message}"
                )

                when (it) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        Toast.makeText(
                            this@VerifyPhoneActivity,
                            "Invalid OTP",
                            Toast.LENGTH_SHORT
                        ).show()

                    }


                    is FirebaseAuthInvalidUserException -> {
                        Toast.makeText(
                            this@VerifyPhoneActivity,
                            "Invalid User",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        Toast.makeText(
                            this@VerifyPhoneActivity,
                            "Something went wrong, please try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun verifyLinkAuthentication(credential: AuthCredential) {

        auth.currentUser?.linkWithCredential(credential)
            ?.addOnSuccessListener {

                writeNewUser(
                    auth.currentUser?.uid.toString(),
                    "",
                    "online"
                )

                auth.currentUser?.updateProfile(
                    com.google.firebase.auth.ktx.userProfileChangeRequest {
                        this.displayName = nama
                    }
                )

                Toast.makeText(
                    this@VerifyPhoneActivity,
                    "Acccount Created and Linked.",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
                this.finish()

                // send to dashboard.
            }?.addOnFailureListener {

                // if account already exists.
                when (it) {
                    is FirebaseAuthUserCollisionException -> {

                        Toast.makeText(
                            this@VerifyPhoneActivity,
                            "Account already exists.",
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.d(TAG, "verifyAuthentication: ${it.message}")

                        val intent = Intent(this, MainFragment::class.java)
                        this.startActivity(intent)
                        this.finish()
                    }

                    is FirebaseAuthInvalidCredentialsException -> {
                        Log.d(TAG, "verifyAuthentication: ${it.message}")
                    }

                    else -> {
                        Log.d(TAG, "verifyAuthentication: ${it.message}")

                        Toast.makeText(
                            this@VerifyPhoneActivity,
                            "Something went wrong, please try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }


    private fun updateUI(user: FirebaseUser? = auth.currentUser) {
        // count down timer for resend otp

    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {

        // if user input not click backspace then move to next edit text
        if (view != null && view is EditText) {

            if (keyCode != KeyEvent.KEYCODE_DEL && view.text.isNotEmpty()) {
                when (view) {
                    binding.otpNumberOne -> binding.optNumberTwo.requestFocus()
                    binding.optNumberTwo -> binding.otpNumberThree.requestFocus()
                    binding.otpNumberThree -> binding.otpNumberFour.requestFocus()
                    binding.otpNumberFour -> binding.otpNumberFive.requestFocus()
                    binding.otpNumberFive -> binding.otpNumberSix.requestFocus()
                    binding.otpNumberSix -> binding.otpNumberSix.requestFocus()
                }
            }

            // if user input click backspace then move to previous edit text
            if (keyCode == KeyEvent.KEYCODE_DEL && view.text.isEmpty()) {
                when (view) {
                    binding.optNumberTwo -> binding.otpNumberOne.requestFocus()
                    binding.otpNumberThree -> binding.optNumberTwo.requestFocus()
                    binding.otpNumberFour -> binding.otpNumberThree.requestFocus()
                    binding.otpNumberFive -> binding.otpNumberFour.requestFocus()
                    binding.otpNumberSix -> binding.otpNumberFive.requestFocus()
                }
            }
        }
        return false
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        /*if (view != null && view is EditText) {
            if (binding.otpNumberOne.text.isEmpty()) {
                binding.otpNumberOne.requestFocus()
            } else if (binding.optNumberTwo.text.isEmpty()) {
                binding.optNumberTwo.requestFocus()
            } else if (binding.otpNumberThree.text.isEmpty()) {
                binding.otpNumberThree.requestFocus()
            } else if (binding.otpNumberFour.text.isEmpty()) {
                binding.otpNumberFour.requestFocus()
            } else if (binding.otpNumberFive.text.isEmpty()) {
                binding.otpNumberFive.requestFocus()
            } else if (binding.otpNumberSix.text.isEmpty()) {
                binding.otpNumberSix.requestFocus()
            }
        }*/
    }

    private fun writeNewUser(
        userId: String,
        alamat: String? = "",
        status: String?,
    ) {
        val user = User(alamat!!, status!!)
        database.child("users").child(userId).setValue(user).addOnSuccessListener {
            Log.d("TAG", "writeNewUser: Success")
        }.addOnFailureListener {
            Log.d("TAG", "writeNewUser: ${it.message}")
        }
    }

    private fun setClickableSpan(
        spannableString: SpannableString,
        clickableText: String,
        color: Int,
        onClick: () -> Unit
    ) {
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClick.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = color
                ds.isUnderlineText = false
            }
        }
        val startIndexOfClickableText = spannableString.indexOf(clickableText)
        spannableString.setSpan(
            clickableSpan,
            startIndexOfClickableText,
            startIndexOfClickableText + clickableText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
    }


}