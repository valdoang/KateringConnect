package com.dicoding.kateringconnect.view.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.dicoding.kateringconnect.R
import com.dicoding.kateringconnect.databinding.ActivityRegisterBinding
import com.dicoding.kateringconnect.view.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
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