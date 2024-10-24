package com.valdoang.kateringconnect.view.vendor.detailpesanan

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentTolakBatalkanPesananBinding

class TolakPesananFragment : DialogFragment() {
    private var _binding: FragmentTolakBatalkanPesananBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTolakBatalkanPesananBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        firebaseAuth = Firebase.auth

        val mArgs = arguments
        val pesananId = mArgs!!.getString("id")

        setUI()
        batalkanPesanan(pesananId!!)
        closeDialog()
        return root
    }

    private fun batalkanPesanan(pesananId: String) {
        binding.btnKirim.setOnClickListener {
            val sAlasan = binding.edAlasan.text.toString().trim()

            val alasanMap = mapOf(
                "status" to getString(R.string.status_ditolak),
                "alasan" to sAlasan
            )
            db.collection("pesanan").document(pesananId).update(alasanMap)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), R.string.success_tolak_pesanan, Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), R.string.fail_tolak_pesanan, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setUI() {
        binding.titleAlasan.text = getString(R.string.title_alasan_penolakan)
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