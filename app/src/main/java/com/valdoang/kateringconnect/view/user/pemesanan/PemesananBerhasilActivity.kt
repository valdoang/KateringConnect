package com.valdoang.kateringconnect.view.user.pemesanan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityPemesananBerhasilBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.user.main.UserMainActivity

class PemesananBerhasilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPemesananBerhasilBinding
    private var from: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemesananBerhasilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        from = intent.getStringExtra(Cons.EXTRA_NAMA)

        setupTextView()
        setupAction()
    }

    private fun setupTextView() {
        when (from) {
            getString(R.string.from_pemesanan) -> binding.tvPesananDiproses.text = getString(R.string.pemesanan_berhasil)
            getString(R.string.from_tambah_porsi) -> binding.tvPesananDiproses.text = getString(R.string.penambahan_porsi_berhasil)
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            val intent = Intent(this, UserMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.tvToBeranda.setOnClickListener {
            val intent = Intent(this, UserMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, UserMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        super.onBackPressed()
    }
}