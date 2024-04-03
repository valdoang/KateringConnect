package com.dicoding.kateringconnect.view.register

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.dicoding.kateringconnect.R
import com.dicoding.kateringconnect.databinding.ActivityRegisterBinding
import com.dicoding.kateringconnect.view.login.LoginActivity
import com.dicoding.kateringconnect.view.main.MainActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        rgJenisAkun()
        acCity()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
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
        binding.tvToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.registerButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}