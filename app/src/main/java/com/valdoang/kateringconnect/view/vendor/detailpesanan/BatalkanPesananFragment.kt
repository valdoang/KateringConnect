package com.valdoang.kateringconnect.view.vendor.detailpesanan

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
import com.valdoang.kateringconnect.databinding.FragmentBatalkanPesananBinding
import com.valdoang.kateringconnect.databinding.FragmentBeriNilaiBinding
import java.util.stream.Collectors

class BatalkanPesananFragment : DialogFragment() {
    //TODO: BENAHI KEMBALI

    private var _binding: FragmentBatalkanPesananBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId = ""
    private var userFoto = ""
    private var userNama = ""
    private var vendorId = ""
    private var menuNama = ""
    private var menuPesananList: ArrayList<String> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBatalkanPesananBinding.inflate(inflater, container, false)
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupData(pesananId: String) {
        val pesananRef = db.collection("pesanan").document(pesananId)
        pesananRef.get().addOnSuccessListener {  pesananSnapshot ->
            if (pesananSnapshot != null) {
                userId = pesananSnapshot.data?.get("userId").toString()
                userFoto = pesananSnapshot.data?.get("userFoto").toString()
                userNama = pesananSnapshot.data?.get("userNama").toString()
                vendorId = pesananSnapshot.data?.get("vendorId").toString()
            }
        }

        val menuPesananRef = db.collection("pesanan").document(pesananId).collection("menuPesanan")
        menuPesananRef.get().addOnSuccessListener { menuPesananSnapshot ->
            if (menuPesananSnapshot != null) {
                menuPesananList.clear()
                for (i in menuPesananSnapshot) {
                    val namaMenu = i.data["namaMenu"].toString()
                    val jumlah = i.data["jumlah"].toString()
                    val sNama = getString(R.string.menu_jumlah, namaMenu, jumlah)
                    menuPesananList.add(sNama)
                }

                menuNama = menuPesananList.stream().collect(
                    Collectors.joining(", ")
                )
            }
        }
    }

    private fun beriNilai(pesananId: String) {
        binding.btnKirim.setOnClickListener {
            val sTanggal = System.currentTimeMillis().toString()
            //val sNilai = binding.ratingBar.rating.toString()
            val sUlasan = binding.edComment.text.toString().trim()

            val ratingMap = mapOf(
                "pesananId" to pesananId,
                "userId" to userId,
                "userFoto" to userFoto,
                "userNama" to userNama,
                "vendorId" to vendorId,
                "menuNama" to menuNama,
                "tanggal" to sTanggal,
                //"nilai" to sNilai,
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