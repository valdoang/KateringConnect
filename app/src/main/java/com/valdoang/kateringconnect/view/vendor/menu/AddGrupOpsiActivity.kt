package com.valdoang.kateringconnect.view.vendor.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityAddGrupOpsiBinding

class AddGrupOpsiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGrupOpsiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGrupOpsiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }
}