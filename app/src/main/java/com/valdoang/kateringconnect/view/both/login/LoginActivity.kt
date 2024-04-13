package com.valdoang.kateringconnect.view.both.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.view.user.main.UserMainActivity
import com.valdoang.kateringconnect.databinding.ActivityLoginBinding
import com.valdoang.kateringconnect.view.both.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        etEmail = binding.edLoginEmail
        etPassword = binding.edLoginPassword
        btnLogin = binding.loginButton

        setupAction()
    }

    private fun setupAction() {
        firebaseAuth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)

        btnLogin.setOnClickListener {
            val sEmail = etEmail.text.toString().trim()
            val sPassword = etPassword.text.toString().trim()

            if(sEmail.isNotEmpty() && sPassword.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(sEmail, sPassword)
                    .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(this, R.string.success_login, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, UserMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, R.string.wrong_email_pass, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, R.string.please_entry_email_pass, Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvToRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null) {
            Intent(this@LoginActivity, UserMainActivity::class.java).also {intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}