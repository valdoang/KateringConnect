package com.valdoang.kateringconnect.view.admin.ui.pengaturan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.valdoang.kateringconnect.databinding.FragmentAdminPengaturanBinding
import com.valdoang.kateringconnect.view.all.alertdialog.LogoutFragment

class AdminPengaturanFragment : Fragment() {

    private var _binding: FragmentAdminPengaturanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPengaturanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvLogout.setOnClickListener{
            val dialog = LogoutFragment()
            dialog.show(this.parentFragmentManager, "logoutDialog")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}