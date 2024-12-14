package com.valdoang.kateringconnect.view.admin.ui.verifikasi

import android.content.Intent
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
import com.valdoang.kateringconnect.view.admin.AdminMainActivity

class TolakPendaftaranFragment : DialogFragment() {
    private var _binding: FragmentTolakBatalkanPesananBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId: String? = null

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
        userId = mArgs!!.getString("userId")

        setUI()
        batalkanPesanan()
        closeDialog()
        return root
    }

    private fun batalkanPesanan() {
        binding.btnKirim.setOnClickListener {
            val sAlasan = binding.edAlasan.text.toString().trim()

            val alasanMap = mapOf(
                "statusPendaftaran" to getString(R.string.ditolak),
                "alasanPenolakanPendaftaran" to sAlasan
            )
            db.collection("user").document(userId!!).update(alasanMap)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), R.string.success_tolak_pendaftaran, Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), AdminMainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), R.string.fail_tolak_pendaftaran, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setUI() {
        binding.titleAlasan.text = getString(R.string.title_alasan_penolakan_pendaftaran)
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