package com.d3if4503.typewriter

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.aexpress.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore

class SplashActivity : AppCompatActivity() {

    private val layoutDataStore by lazy { SettingDataStore(this.dataStore) }

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Mengatur tampilan splash screen menjadi fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Mengatur tampilan splash screen selama beberapa waktu sebelum pindah ke Activity lain
        Handler().postDelayed({
            // Intent untuk pindah ke Activity lain setelah splash screen selesai
            val intentMainActivity = Intent(this, MainActivity::class.java)
            val intentLoginActivity = Intent(this, AuthActivity::class.java)

            // Jika user auth tidak kosong, maka pindah ke MainActivity
            if (auth.currentUser?.uid != null) {
                startActivity(intentMainActivity)
                finish()
            } else {
                startActivity(intentLoginActivity)
                finish()
            }

        }, SPLASH_DURATION.toLong())
    }

    companion object {
        private const val SPLASH_DURATION =
            2000 // Durasi tampilan splash screen dalam milidetik (misalnya 2000 ms)
    }
}