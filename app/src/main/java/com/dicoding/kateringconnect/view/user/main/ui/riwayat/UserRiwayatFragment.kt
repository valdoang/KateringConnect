package com.dicoding.kateringconnect.view.user.main.ui.riwayat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.kateringconnect.databinding.FragmentUserRiwayatBinding
import com.dicoding.kateringconnect.view.user.DetailRiwayatPemesananActivity

class UserRiwayatFragment : Fragment() {

    private var _binding: FragmentUserRiwayatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserRiwayatBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupAction()

        return root
    }

    private fun setupAction() {
        binding.titleUserRiwayat.setOnClickListener {
            val intent = Intent(requireContext(), DetailRiwayatPemesananActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}