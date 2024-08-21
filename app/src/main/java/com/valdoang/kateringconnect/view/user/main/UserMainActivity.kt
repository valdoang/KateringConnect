package com.valdoang.kateringconnect.view.user.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityUserMainBinding
import com.valdoang.kateringconnect.adapter.ViewPagerAdapter
import com.valdoang.kateringconnect.view.user.main.ui.akun.UserAkunFragment
import com.valdoang.kateringconnect.view.user.main.ui.beranda.UserBerandaFragment
import com.valdoang.kateringconnect.view.user.main.ui.riwayat.UserRiwayatFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.valdoang.kateringconnect.view.user.main.ui.pesanan.UserPesananFragment

class UserMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserMainBinding
    private lateinit var navView : BottomNavigationView
    private lateinit var viewPager : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView= binding.navView
        viewPager = binding.viewPager

        setupNavigation()
    }

    private fun setupNavigation() {
        // Define fragments to display in viewPager2
        val listOfFragments = listOf(UserBerandaFragment(), UserPesananFragment(), UserRiwayatFragment(), UserAkunFragment())
        viewPager.adapter = ViewPagerAdapter(this, listOfFragments)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> navView.menu.findItem(R.id.navigation_user_beranda).isChecked = true
                    1 -> navView.menu.findItem(R.id.navigation_user_pesanan).isChecked = true
                    2 -> navView.menu.findItem(R.id.navigation_user_riwayat).isChecked = true
                    3 -> navView.menu.findItem(R.id.navigation_user_akun).isChecked = true

                }
            }
        })

        // Listen bottom navigation tabs change
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_user_beranda -> {
                    viewPager.setCurrentItem(0, true)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_user_pesanan -> {
                    viewPager.setCurrentItem(1, true)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_user_riwayat -> {
                    viewPager.setCurrentItem(2, true)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_user_akun -> {
                    viewPager.setCurrentItem(3, true)
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