package com.valdoang.kateringconnect.view.user.berinilai

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.valdoang.kateringconnect.databinding.FragmentBeriNilaiBinding

class BeriNilaiFragment : DialogFragment() {

    private var _binding: FragmentBeriNilaiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBeriNilaiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        ratingBar()
        closeDialog()
        return root
    }

    private fun closeDialog() {
        binding.ibClose.setOnClickListener{
            dismiss()
        }
    }

    private fun ratingBar() {
        binding.btnKirim.setOnClickListener {
            Toast.makeText(requireContext(), "Rating is: ${binding.ratingBar.rating}", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}