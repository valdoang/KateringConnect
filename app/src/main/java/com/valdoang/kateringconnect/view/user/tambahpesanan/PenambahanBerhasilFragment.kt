package com.valdoang.kateringconnect.view.user.tambahpesanan

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentPemesananBerhasilBinding

class PenambahanBerhasilFragment : DialogFragment(), DialogInterface.OnCancelListener {
    private var _binding: FragmentPemesananBerhasilBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPemesananBerhasilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        binding.tvPesananDiproses.text = getString(R.string.penambahan_berhasil)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}