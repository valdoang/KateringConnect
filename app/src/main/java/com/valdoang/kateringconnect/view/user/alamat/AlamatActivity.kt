package com.valdoang.kateringconnect.view.user.alamat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityAlamatBinding

class AlamatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlamatBinding
    //TODO: Setup RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlamatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction() {
        binding.addAlamat.setOnClickListener {
            val intent = Intent(this, AddAlamatActivity::class.java)
            startActivity(intent)
        }
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}