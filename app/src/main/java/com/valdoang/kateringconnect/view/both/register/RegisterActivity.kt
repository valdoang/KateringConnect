package com.valdoang.kateringconnect.view.both.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    private lateinit var rgJenisAkun: RadioGroup
    private lateinit var etName: EditText
    private lateinit var acCity: AutoCompleteTextView
    private lateinit var etNoPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var progressBar: ProgressBar
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        etEmail = binding.edRegisterEmail
        etPassword = binding.edRegisterPassword
        btnRegister = binding.registerButton
        rgJenisAkun = binding.rgJenisAkun
        etName = binding.edRegisterName
        acCity = binding.acRegisterCity
        etNoPhone = binding.edRegisterNoPhone
        etAddress = binding.edRegisterAddress
        progressBar = binding.progressBar

        setupAcCity()
        setupAction()
    }

    private fun setupAcCity() {
        val cities = resources.getStringArray(R.array.Cities)
        val dropdownAdapter = ArrayAdapter(this, R.layout.dropdown_item, cities)
        acCity.setAdapter(dropdownAdapter)
    }

    private fun setupAction() {
        firebaseAuth = Firebase.auth

        var sJenisAkun = ""
        var sCity = ""
        rgJenisAkun.setOnCheckedChangeListener { _, checkedId ->
            val rbJenisAkun: RadioButton = findViewById(checkedId)
            when (rbJenisAkun.text) {
                getString(R.string.akun_pembeli) -> sJenisAkun = getString(R.string.pembeli)
                getString(R.string.akun_vendor) -> sJenisAkun = getString(R.string.vendor)
            }
        }

        acCity.onItemClickListener = AdapterView.OnItemClickListener{
                adapterView, _, i, _ ->

            sCity = adapterView.getItemAtPosition(i).toString()
        }

        btnRegister.setOnClickListener {
            val sName = etName.text.toString().trim()
            val sNoPhone = etNoPhone.text.toString().trim()
            val sAddress = etAddress.text.toString().trim()
            val sEmail = etEmail.text.toString().trim()
            val sPassword = etPassword.text.toString().trim()

            val userMap = hashMapOf(
                "jenis" to sJenisAkun,
                "nama" to sName,
                "kota" to sCity,
                "alamat" to sAddress,
                "telepon" to sNoPhone,
                "email" to sEmail
            )

            when {
                sJenisAkun.isEmpty() -> {
                    Toast.makeText(this, R.string.can_not_blank, Toast.LENGTH_SHORT).show()
                }
                sName.isEmpty() -> {
                    etName.error = getString(R.string.entry_name)
                }
                sCity.isEmpty() -> {
                    acCity.error = getString(R.string.entry_city)
                }
                sAddress.isEmpty() -> {
                    etAddress.error = getString(R.string.entry_address)
                }
                sNoPhone.isEmpty() -> {
                    etNoPhone.error = getString(R.string.entry_no_phone)
                }
                sEmail.isEmpty() -> {
                    etEmail.error = getString(R.string.entry_email)
                }
                sPassword.isEmpty() -> {
                    etPassword.error = getString(R.string.entry_password)
                }
                sPassword.length < 8 -> {
                    etPassword.error = getString(R.string.minimum_character)
                }
                else -> {
                    progressBar.visibility = View.VISIBLE
                    firebaseAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                        .addOnCompleteListener(this) { task ->
                            if(task.isSuccessful) {
                                val userId = FirebaseAuth.getInstance().currentUser!!.uid

                                db.collection("user").document(userId).set(userMap)
                                when(sJenisAkun) {
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
                                Toast.makeText(this, R.string.success_register, Toast.LENGTH_SHORT).show()
                            } else {
                                progressBar.visibility = View.GONE
                                Toast.makeText(this, R.string.already_exists_email, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        binding.tvToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}