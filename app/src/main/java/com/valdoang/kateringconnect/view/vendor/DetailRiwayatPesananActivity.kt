package com.valdoang.kateringconnect.view.vendor

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.valdoang.kateringconnect.databinding.ActivityDetailPesananRiwayatBinding

class DetailRiwayatPesananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPesananRiwayatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupAction()
        hideUI()
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun hideUI() {
        binding.btnBeriNilai.visibility = View.GONE
        binding.btnPesanLagi.visibility = View.GONE
        binding.btnSelesaikan.visibility = View.GONE
        binding.tvVendorName.visibility = View.GONE
    }
}