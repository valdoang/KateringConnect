package com.dicoding.kateringconnect.view.vendor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.kateringconnect.databinding.ActivityDetailVendorBinding
import com.dicoding.kateringconnect.view.galeri.DetailGaleriFragment
import com.dicoding.kateringconnect.view.menu.MenuActivity

class DetailVendorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailVendorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailVendorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }

        binding.cvNasiKotak.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        binding.tvVendorGaleri.setOnClickListener {
            val dialog = DetailGaleriFragment()
            dialog.show(this.supportFragmentManager, "detailDialog")
        }
    }
}