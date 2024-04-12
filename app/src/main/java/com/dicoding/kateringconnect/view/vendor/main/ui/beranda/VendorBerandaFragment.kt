package com.dicoding.kateringconnect.view.vendor.main.ui.beranda

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.kateringconnect.databinding.FragmentVendorBerandaBinding
import com.dicoding.kateringconnect.view.vendor.DetailPesananActivity

class VendorBerandaFragment : Fragment() {

    private var _binding: FragmentVendorBerandaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        
        _binding = FragmentVendorBerandaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupAction()
        
        return root
    }

    private fun setupAction() {
        binding.titleBeranda.setOnClickListener {
            val intent = Intent(requireContext(), DetailPesananActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}