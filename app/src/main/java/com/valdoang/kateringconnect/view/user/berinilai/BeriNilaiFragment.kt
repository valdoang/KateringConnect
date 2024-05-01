package com.valdoang.kateringconnect.view.user.berinilai

import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.valdoang.kateringconnect.databinding.FragmentBeriNilaiBinding

class BeriNilaiFragment : DialogFragment() {

    private var _binding: FragmentBeriNilaiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId = ""
    private var userFoto = ""
    private var userNama = ""
    private var vendorId = ""
    private var menuId = ""
    private var menuNama = ""
    private var jumlahPesanan = ""


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

        firebaseAuth = Firebase.auth

        val mArgs = arguments
        val pesananId = mArgs!!.getString("id")

        setupData(pesananId!!)
        beriNilai(pesananId)
        closeDialog()
        return root
    }

    private fun setupData(pesananId: String) {
        val ref = db.collection("pesanan").document(pesananId)
        ref.get().addOnSuccessListener {  document ->
            if (document != null) {
                userId = document.data?.get("userId").toString()
                userFoto = document.data?.get("userFoto").toString()
                userNama = document.data?.get("userNama").toString()
                vendorId = document.data?.get("vendorId").toString()
                menuId = document.data?.get("menuId").toString()
                menuNama = document.data?.get("menuNama").toString()
                jumlahPesanan = document.data?.get("jumlah").toString()
            }
        }
    }

    private fun beriNilai(pesananId: String) {
        binding.btnKirim.setOnClickListener {
            val sTanggal = System.currentTimeMillis().toString()
            val sNilai = binding.ratingBar.rating.toString()
            val sUlasan = binding.edComment.text.toString().trim()

            val ratingMap = mapOf(
                "pesananId" to pesananId,
                "userId" to userId,
                "userFoto" to userFoto,
                "userNama" to userNama,
                "vendorId" to vendorId,
                "menuId" to menuId,
                "menuNama" to menuNama,
                "jumlahPesanan" to jumlahPesanan,
                "tanggal" to sTanggal,
                "nilai" to sNilai,
                "ulasan" to sUlasan
            )
            db.collection("nilai").document().set(ratingMap)
                .addOnSuccessListener {
                    db.collection("pesanan").document(pesananId).update("nilai", true)
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), R.string.fail_set_rating, Toast.LENGTH_SHORT).show()
                }
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