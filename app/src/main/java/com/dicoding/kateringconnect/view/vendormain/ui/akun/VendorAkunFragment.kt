package com.dicoding.kateringconnect.view.vendormain.ui.akun

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.kateringconnect.databinding.FragmentVendorAkunBinding
import com.dicoding.kateringconnect.view.galeri.AddGaleriActivity
import com.dicoding.kateringconnect.view.menu.AddMenuFragment
import com.dicoding.kateringconnect.view.akun.EditAkunActivity
import com.dicoding.kateringconnect.view.galeri.DetailGaleriFragment
import com.dicoding.kateringconnect.view.login.LoginActivity

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}