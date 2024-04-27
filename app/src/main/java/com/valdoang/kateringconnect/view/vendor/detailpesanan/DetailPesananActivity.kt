package com.valdoang.kateringconnect.view.vendor.detailpesanan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.valdoang.kateringconnect.databinding.ActivityDetailPesananRiwayatBinding

class DetailPesananActivity : AppCompatActivity() {
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
        binding.btnSelesaikan.setOnClickListener {
            onBackPressed()
        }
    }

    private fun hideUI() {
        binding.btnBeriNilai.visibility = View.GONE
        binding.btnPesanLagi.visibility = View.GONE
        binding.tvVendorName.visibility = View.GONE
    }
}