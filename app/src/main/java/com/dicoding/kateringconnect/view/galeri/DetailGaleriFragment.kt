package com.dicoding.kateringconnect.view.galeri

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.dicoding.kateringconnect.R
import com.dicoding.kateringconnect.databinding.FragmentDetailGaleriBinding

class DetailGaleriFragment : DialogFragment() {
    private var _binding: FragmentDetailGaleriBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailGaleriBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        binding.cvDeletePhoto.setOnClickListener {
            dismiss()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}