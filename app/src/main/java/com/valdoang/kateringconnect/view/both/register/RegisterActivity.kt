package com.valdoang.kateringconnect.view.both.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityRegisterBinding
import com.valdoang.kateringconnect.view.both.login.LoginActivity
import com.valdoang.kateringconnect.view.user.main.UserMainActivity
import com.valdoang.kateringconnect.view.vendor.main.VendorMainActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        etEmail = binding.edRegisterEmail
        etPassword = binding.edRegisterPassword
        btnRegister = binding.registerButton

        setupAction()
        rgJenisAkun()
        acCity()
    }

    private fun rgJenisAkun() {
        val radioGroup = binding.rgJenisAkun
        radioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener{ group, checkedId ->
                val radio : RadioButton = findViewById(checkedId)
                Toast.makeText(this, "Jenis Akun : ${radio.text}", Toast.LENGTH_SHORT).show()

            }
        )
    }

    private fun acCity() {
        val cities = resources.getStringArray(R.array.Cities)
        val autoComplete = binding.acRegisterCity
        val dropdownAdapter = ArrayAdapter(this, R.layout.dropdown_item, cities)
        autoComplete.setAdapter(dropdownAdapter)
        autoComplete.onItemClickListener = AdapterView.OnItemClickListener{
                adapterView, view, i, l ->

            val itemSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(this, "Kota: $itemSelected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAction() {
        firebaseAuth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)

        btnRegister.setOnClickListener {
            val sEmail = etEmail.text.toString().trim()
            val sPassword = etPassword.text.toString().trim()

            if(sEmail.isNotEmpty() && sPassword.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                    .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(this, R.string.success_register, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, UserMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, R.string.already_exists_email, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, R.string.please_entry_email_pass, Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvToLogin.setOnClickListener{
            val intent = Intent(this, VendorMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}