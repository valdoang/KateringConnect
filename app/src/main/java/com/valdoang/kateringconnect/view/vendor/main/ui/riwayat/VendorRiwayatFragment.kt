package com.valdoang.kateringconnect.view.vendor.main.ui.riwayat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.valdoang.kateringconnect.databinding.FragmentVendorRiwayatBinding
import com.valdoang.kateringconnect.view.vendor.detailriwayat.DetailRiwayatPesananActivity

class VendorRiwayatFragment : Fragment() {

    private var _binding: FragmentVendorRiwayatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       
        _binding = FragmentVendorRiwayatBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupAction()
        
        return root
    }

    private fun setupAction() {
        binding.titleVendorRiwayat.setOnClickListener {
            val intent = Intent(requireContext(), DetailRiwayatPesananActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}