package com.valdoang.kateringconnect.view.user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityDetailPesananRiwayatBinding

class DetailRiwayatPemesananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPesananRiwayatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupAction()
        hideUI()
        editUI()
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnBeriNilai.setOnClickListener {
            val dialog = BeriNilaiFragment()
            dialog.show(this.supportFragmentManager, "beriNilaiDialog")
        }
        binding.btnPesanLagi.setOnClickListener {
            onBackPressed()
        }
    }

    private fun hideUI() {
        binding.btnSelesaikan.visibility = View.GONE
        binding.tvId.visibility = View.GONE
        binding.tvIdValue.visibility = View.GONE
    }

    private fun editUI() {
        binding.titlePesanan.text = resources.getString(R.string.rincian_pesananmu)
        binding.tvRincianPesanan.text = resources.getString(R.string.rincian_pesananmu)
    }
}