package com.valdoang.kateringconnect.view.vendor.main.ui.akun

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.valdoang.kateringconnect.databinding.FragmentVendorAkunBinding
import com.valdoang.kateringconnect.view.vendor.galeri.AddGaleriActivity
import com.valdoang.kateringconnect.view.vendor.menu.AddMenuFragment
import com.valdoang.kateringconnect.view.both.akun.EditAkunActivity
import com.valdoang.kateringconnect.view.vendor.galeri.DetailGaleriFragment
import com.valdoang.kateringconnect.view.both.login.LoginActivity
import com.valdoang.kateringconnect.view.vendor.menu.VendorMenuActivity

class VendorAkunFragment : Fragment() {

    private var _binding: FragmentVendorAkunBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        
        _binding = FragmentVendorAkunBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupAction()
        hideUI()
        return root
    }

    private fun setupAction() {
        binding.btnVendorEditAkun.setOnClickListener{
            val intent = Intent(requireContext(), EditAkunActivity::class.java)
            startActivity(intent)
        }
        binding.btnVendorLogout.setOnClickListener{
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        binding.fabAddMenu.setOnClickListener {
            val dialog = AddMenuFragment()
            dialog.show(this.parentFragmentManager, "addDialog")
        }
        binding.fabAddGaleri.setOnClickListener {
            val intent = Intent(requireContext(), AddGaleriActivity::class.java)
            startActivity(intent)
        }
        binding.cvNasiKotak.setOnClickListener {
            val intent = Intent(requireContext(), VendorMenuActivity::class.java)
            startActivity(intent)
        }
        binding.tvVendorGaleri.setOnClickListener {
            val dialog = DetailGaleriFragment()
            dialog.show(this.parentFragmentManager, "detailDialog")
        }
    }

    private fun hideUI() {
        binding.ibBack.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}