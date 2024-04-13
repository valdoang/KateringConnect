package com.valdoang.kateringconnect.view.vendor.main

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.ViewPagerAdapter
import com.valdoang.kateringconnect.databinding.ActivityVendorMainBinding
import com.valdoang.kateringconnect.view.vendor.main.ui.akun.VendorAkunFragment
import com.valdoang.kateringconnect.view.vendor.main.ui.beranda.VendorBerandaFragment
import com.valdoang.kateringconnect.view.vendor.main.ui.riwayat.VendorRiwayatFragment

class VendorMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVendorMainBinding
    private lateinit var navView : BottomNavigationView
    private lateinit var viewPager : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityVendorMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView= binding.navView
        viewPager = binding.viewPager

        setupNavigation()
    }

    private fun setupNavigation() {
        // Define fragments to display in viewPager2
        val listOfFragments = listOf(VendorBerandaFragment(), VendorRiwayatFragment(), VendorAkunFragment())
        viewPager.adapter = ViewPagerAdapter(this, listOfFragments)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> navView.menu.findItem(R.id.navigation_vendor_beranda).isChecked = true
                    1 -> navView.menu.findItem(R.id.navigation_vendor_riwayat).isChecked = true
                    2 -> navView.menu.findItem(R.id.navigation_vendor_akun).isChecked = true

                }
            }
        })

        // Listen bottom navigation tabs change
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_vendor_beranda -> {
                    viewPager.setCurrentItem(0, true)
                    return@setOnItemSelectedListener true

                }
                R.id.navigation_vendor_riwayat -> {
                    viewPager.setCurrentItem(1, true)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_vendor_akun -> {
                    viewPager.setCurrentItem(2, true)
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
    }

    override fun onBackPressed() {
        if (viewPager.currentItem != 0) {
            viewPager.setCurrentItem(0, false)
        } else {
            super.onBackPressed()
        }
    }
}