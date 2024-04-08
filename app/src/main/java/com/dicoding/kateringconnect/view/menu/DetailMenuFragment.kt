package com.dicoding.kateringconnect.view.menu

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.dicoding.kateringconnect.R
import com.dicoding.kateringconnect.databinding.FragmentDetailGaleriBinding
import com.dicoding.kateringconnect.databinding.FragmentDetailMenuBinding
import com.dicoding.kateringconnect.view.pemesanan.PemesananActivity

class DetailMenuFragment : DialogFragment() {
    private var _binding: FragmentDetailMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        binding.ibClose.setOnClickListener {
            dismiss()
        }

        binding.btnPesan.setOnClickListener {
            val intent = Intent(requireContext(), PemesananActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}