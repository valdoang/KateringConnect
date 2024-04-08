package com.dicoding.kateringconnect.view.pemesanan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.dicoding.kateringconnect.R
import com.dicoding.kateringconnect.databinding.ActivityPemesananBinding

class PemesananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPemesananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }

        rgMetodePembayaran()
    }

    private fun rgMetodePembayaran() {
        val radioGroup = binding.rgMetodePembayaran
        radioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener{ group, checkedId ->
                val radio : RadioButton = findViewById(checkedId)
                Toast.makeText(this, "Metode Pembayaran : ${radio.text}", Toast.LENGTH_SHORT).show()

            }
        )
    }
}