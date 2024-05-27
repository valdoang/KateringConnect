package com.valdoang.kateringconnect.view.vendor.menu

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.ViewPagerAdapter
import com.valdoang.kateringconnect.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var tabs: TabLayout
    private lateinit var viewPager : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        tabs = binding.tabs
        viewPager = binding.viewPager

        setupNavigation()
        setupAction()
    }

    private fun setupNavigation() {
        // Define fragments to display in viewPager2
        val listOfFragments = listOf(MenuFragment(), GrupOpsiFragment())
        viewPager.adapter = ViewPagerAdapter(this, listOfFragments)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }


    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.menu_tab,
            R.string.grup_opsi_tab
        )
    }
}