package com.valdoang.kateringconnect.view.both.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityLoginBinding
import com.valdoang.kateringconnect.view.both.register.RegisterActivity
import com.valdoang.kateringconnect.view.user.main.UserMainActivity
import com.valdoang.kateringconnect.view.vendor.main.VendorMainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        etEmail = binding.edLoginEmail
        etPassword = binding.edLoginPassword
        btnLogin = binding.loginButton
        progressBar = binding.progressBar

        setupAction()
    }

    private fun setupAction() {
        firebaseAuth = Firebase.auth

        btnLogin.setOnClickListener {
            val sEmail = etEmail.text.toString().trim()
            val sPassword = etPassword.text.toString().trim()

            when {
                sEmail.isEmpty() -> {
                    etEmail.error = getString(R.string.entry_email)
                }
                !Patterns.EMAIL_ADDRESS.matcher(sEmail).matches() -> {
                    etEmail.error = getString(R.string.not_valid_email)
                }
                sPassword.isEmpty() -> {
                    etPassword.error = getString(R.string.entry_password)
                }
                sPassword.length < 8 -> {
                    etPassword.error = getString(R.string.minimum_character)
                }
                else -> {
                    progressBar.visibility = View.VISIBLE
                    firebaseAuth.signInWithEmailAndPassword(sEmail, sPassword)
                        .addOnCompleteListener(this) { task ->
                            if(task.isSuccessful) {
                                checkUser()
                                Toast.makeText(this, R.string.success_login, Toast.LENGTH_SHORT).show()
                            } else {
                                progressBar.visibility = View.GONE
                                Toast.makeText(this, R.string.wrong_email_pass, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        binding.tvToRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
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
                    }
                }
            }
    }
}