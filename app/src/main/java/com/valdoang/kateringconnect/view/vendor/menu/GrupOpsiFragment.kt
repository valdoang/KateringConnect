package com.valdoang.kateringconnect.view.vendor.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.valdoang.kateringconnect.databinding.FragmentGrupOpsiBinding

class GrupOpsiFragment : Fragment() {

    private var _binding: FragmentGrupOpsiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGrupOpsiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupAction()

        return root
    }

    private fun setupAction() {
        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddGrupOpsiActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}