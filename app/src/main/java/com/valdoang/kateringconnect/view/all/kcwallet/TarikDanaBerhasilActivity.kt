package com.valdoang.kateringconnect.view.all.kcwallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.valdoang.kateringconnect.databinding.ActivityTarikDanaBerhasilBinding

class TarikDanaBerhasilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTarikDanaBerhasilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTarikDanaBerhasilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}