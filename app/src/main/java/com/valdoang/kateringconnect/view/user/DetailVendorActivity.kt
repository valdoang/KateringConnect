package com.valdoang.kateringconnect.view.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.valdoang.kateringconnect.databinding.FragmentVendorAkunBinding
import com.valdoang.kateringconnect.view.vendor.galeri.DetailGaleriFragment
import com.valdoang.kateringconnect.view.user.menu.UserMenuActivity

class DetailVendorActivity : AppCompatActivity() {
    private lateinit var binding: FragmentVendorAkunBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentVendorAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupAction()
        hideUI()
    }

    private fun hideUI() {
        binding.titleVendorAkun.visibility = View.GONE
        binding.btnVendorLogout.visibility = View.GONE
        binding.btnVendorEditAkun.visibility = View.GONE
        binding.btnAddGaleri.visibility = View.GONE
        binding.btnAddMenu.visibility = View.GONE
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }

        binding.cvNasiKotak.setOnClickListener {
            val intent = Intent(this, UserMenuActivity::class.java)
            startActivity(intent)
        }

        binding.tvVendorGaleri.setOnClickListener {
            val dialog = DetailGaleriFragment()
            dialog.show(this.supportFragmentManager, "detailDialog")
        }
    }
}