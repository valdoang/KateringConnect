package com.valdoang.kateringconnect.view.all.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.valdoang.kateringconnect.databinding.ActivitySedangDiverifikasiBinding
import com.valdoang.kateringconnect.view.all.logout.LogoutFragment

class SedangDiverifikasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySedangDiverifikasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySedangDiverifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        logout()
    }

    private fun logout() {
        binding.ibLogout.setOnClickListener{
            val dialog = LogoutFragment()
            dialog.show(this.supportFragmentManager, "logoutDialog")
        }
    }
}