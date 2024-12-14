package com.valdoang.kateringconnect.view.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.ViewPagerAdapter
import com.valdoang.kateringconnect.databinding.ActivityAdminMainBinding
import com.valdoang.kateringconnect.view.admin.ui.chat.AdminChatFragment
import com.valdoang.kateringconnect.view.admin.ui.pengaturan.AdminPengaturanFragment
import com.valdoang.kateringconnect.view.admin.ui.transferdana.AdminTransferDanaFragment
import com.valdoang.kateringconnect.view.admin.ui.verifikasi.AdminVerifikasiFragment

class AdminMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMainBinding
    private lateinit var navView : BottomNavigationView
    private lateinit var viewPager : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        navView= binding.navView
        viewPager = binding.viewPager

        setupNavigation()
    }

    private fun setupNavigation() {
        // Define fragments to display in viewPager2
        val listOfFragments = listOf(AdminVerifikasiFragment(), AdminTransferDanaFragment(), AdminChatFragment(), AdminPengaturanFragment())
        viewPager.adapter = ViewPagerAdapter(this, listOfFragments)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> navView.menu.findItem(R.id.navigation_admin_verifikasi).isChecked = true
                    1 -> navView.menu.findItem(R.id.navigation_admin_transfer_dana).isChecked = true
                    2 -> navView.menu.findItem(R.id.navigation_admin_chat).isChecked = true
                    3 -> navView.menu.findItem(R.id.navigation_admin_pengaturan).isChecked = true
                }
            }
        })

        // Listen bottom navigation tabs change
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_admin_verifikasi -> {
                    viewPager.setCurrentItem(0, true)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_admin_transfer_dana -> {
                    viewPager.setCurrentItem(1, true)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_admin_chat -> {
                    viewPager.setCurrentItem(2, true)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_admin_pengaturan -> {
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