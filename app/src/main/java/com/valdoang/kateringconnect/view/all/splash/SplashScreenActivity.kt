package com.valdoang.kateringconnect.view.all.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivitySplashScreenBinding
import com.valdoang.kateringconnect.view.admin.AdminMainActivity
import com.valdoang.kateringconnect.view.all.login.LoginActivity
import com.valdoang.kateringconnect.view.user.main.UserMainActivity
import com.valdoang.kateringconnect.view.vendor.main.VendorMainActivity

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
    }

    private fun checkUser() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        var intent: Intent

        db.collection("user").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    when(document.data?.get("jenis")?.toString()) {
                        getString(R.string.pembeli) -> {
                            intent = Intent(this, UserMainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        getString(R.string.vendor) -> {
                            intent = Intent(this, VendorMainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        getString(R.string.admin) -> {
                            intent = Intent(this, AdminMainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                checkUser()
            }, 500)
        }
        else {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, 500)
        }
    }
}