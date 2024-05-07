package com.valdoang.kateringconnect.view.user.tambahpesanan

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentTambahPesananBinding
import com.valdoang.kateringconnect.utils.textChangedListener
import com.valdoang.kateringconnect.utils.withNumberingFormat

class TambahPesananFragment : DialogFragment() {
    private var _binding: FragmentTambahPesananBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var jumlah = ""
    private var subtotal = ""
    private var totalPembayaran = ""
    private lateinit var etJumlah: EditText
    private lateinit var tvTotalPembayaran: TextView
    private var total = 0L
    private var sJumlah = 0L
    private var sSubtotal = 0L
    private var sTotalPembayaran = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTambahPesananBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        firebaseAuth = Firebase.auth

        val mArgs = arguments
        val pesananId = mArgs!!.getString("id")

        etJumlah = binding.edJumlah
        tvTotalPembayaran = binding.tvTotalPembayaran

        closeDialog()
        setupData(pesananId!!)
        tambahPorsi(pesananId)
        return root
    }

    private fun setupData(pesananId : String) {
        val ref = db.collection("pesanan").document(pesananId)
        ref.get().addOnSuccessListener { document ->
                if (document != null) {
                    val menuHarga = document.data?.get("menuHarga").toString()
                    jumlah = document.data?.get("jumlah").toString()
                    subtotal = document.data?.get("subtotal").toString()
                    totalPembayaran = document.data?.get("totalPembayaran").toString()

                    etJumlah.textChangedListener { etjumlah ->
                        if (etjumlah == "") {
                            tvTotalPembayaran.text = ""
                        } else {
                            total = etjumlah.toLong() * menuHarga.toLong()
                            tvTotalPembayaran.text = total.withNumberingFormat()

                            //untuk updateMap
                            sJumlah = etjumlah.toLong() + jumlah.toLong()
                            sSubtotal = total + subtotal.toLong()
                            sTotalPembayaran = total + totalPembayaran.toLong()
                        }
                    }
                }
            }
    }

    private fun tambahPorsi(pesananId: String) {
        binding.btnPesan.setOnClickListener {
            val updateMap = mapOf(
                "jumlah" to sJumlah.toString(),
                "subtotal" to sSubtotal.toString(),
                "totalPembayaran" to sTotalPembayaran.toString()
            )

            when {
                etJumlah.text.toString().isEmpty() -> {
                    etJumlah.error = getString(R.string.entry_jumlah)
                }
                else -> {
                    db.collection("pesanan").document(pesananId)
                        .update(updateMap).addOnSuccessListener {
                            dismiss()
                            val newFragment: DialogFragment = PenambahanBerhasilFragment()
                            newFragment.show(parentFragmentManager, "TAG")
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), R.string.fail_penambahan, Toast.LENGTH_SHORT).show()
                        }
                }
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