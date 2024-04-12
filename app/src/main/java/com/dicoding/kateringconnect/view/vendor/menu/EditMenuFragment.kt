package com.dicoding.kateringconnect.view.vendor.menu

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.dicoding.kateringconnect.databinding.FragmentEditMenuBinding


class EditMenuFragment : DialogFragment() {
    private var _binding: FragmentEditMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        saveData()
        deleteData()
        closeDialog()

        return root
    }

    private fun saveData() {
        binding.btnSimpan.setOnClickListener{
            dismiss()
        }
    }

    private fun deleteData() {
        binding.btnHapus.setOnClickListener{
            dismiss()
        }
    }

    private fun closeDialog() {
        binding.ibClose.setOnClickListener{
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}