package com.valdoang.kateringconnect.view.user.alamat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityAddAlamatBinding

class AddAlamatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAlamatBinding
    //TODO: Add Alamat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAlamatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction(){
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}