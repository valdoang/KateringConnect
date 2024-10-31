package com.valdoang.kateringconnect.view.user.pemesanan

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentBeriNilaiBinding
import com.valdoang.kateringconnect.databinding.FragmentKcwalletNotEnoughBinding
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.view.all.kcwallet.TopupActivity
import java.util.stream.Collectors

class KcWalletNotEnoughFragment : DialogFragment() {

    private var _binding: FragmentKcwalletNotEnoughBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var totalHarga: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentKcwalletNotEnoughBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        val mArgs = arguments
        totalHarga = mArgs!!.getString("totalHarga")

        binding.tvKcwallet.text = getString(R.string.rupiah_text, totalHarga?.withNumberingFormat())
        binding.btnTopup.setOnClickListener {
            val intent = Intent(requireContext(), TopupActivity::class.java)
            startActivity(intent)
            dismiss()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}