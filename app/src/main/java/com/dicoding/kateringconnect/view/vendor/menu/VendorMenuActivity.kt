package com.dicoding.kateringconnect.view.vendor.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.kateringconnect.databinding.ActivityMenuBinding

class VendorMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvKategori.setOnClickListener {
            val dialog = EditMenuFragment()
            dialog.show(this.supportFragmentManager, "detailDialog")
        }
    }
}