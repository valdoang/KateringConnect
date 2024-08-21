package com.valdoang.kateringconnect.view.user.tambahpesanan

import android.content.Intent
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
import com.valdoang.kateringconnect.databinding.FragmentTambahPorsiBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.allChangedListener
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.view.user.pemesanan.PemesananBerhasilActivity

class TambahPorsiFragment : DialogFragment() {
    private var _binding: FragmentTambahPorsiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var jumlah = ""
    private var subtotal = ""
    private lateinit var etJumlah: EditText
    private lateinit var tvTotalPembayaran: TextView
    private var total = 0L
    private var sJumlah = 0L
    private var sSubtotal = 0L

    //TODO: TAMBAHKAN PEMBAYARAN DIGITAL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTambahPorsiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        firebaseAuth = Firebase.auth

        val mArgs = arguments
        val pesananId = mArgs!!.getString("pesananId")
        val menuPesananId = mArgs.getString("menuPesananId")

        etJumlah = binding.edJumlah
        tvTotalPembayaran = binding.tvTotalPembayaran

        closeDialog()
        setupData(pesananId!!, menuPesananId!!)
        tambahPorsi(pesananId, menuPesananId)
        return root
    }

    private fun setupData(pesananId : String, menuPesananId: String) {
        val ref = db.collection("pesanan").document(pesananId).collection("menuPesanan").document(menuPesananId)
        ref.get().addOnSuccessListener { document ->
                if (document != null) {
                    jumlah = document.data?.get("jumlah").toString()
                    subtotal = document.data?.get("subtotal").toString()
                    val hargaPerPorsi = document.data?.get("hargaPerPorsi").toString()

                    etJumlah.allChangedListener { etjumlah ->
                        if (etjumlah == "") {
                            tvTotalPembayaran.text = "0"
                        } else {
                            total = etjumlah.toLong() * hargaPerPorsi.toLong()
                            tvTotalPembayaran.text = total.withNumberingFormat()

                            //untuk updateMap
                            sJumlah = etjumlah.toLong() + jumlah.toLong()
                            sSubtotal = total + subtotal.toLong()
                        }
                    }
                }
            }
    }

    private fun tambahPorsi(pesananId: String, menuPesananId: String) {
        binding.btnPesan.setOnClickListener {
            val updateMap = mapOf(
                "jumlah" to sJumlah.toString(),
                "subtotal" to sSubtotal.toString(),
                "tambahPorsi" to true
            )

            when {
                etJumlah.text.toString().isEmpty() -> {
                    etJumlah.error = getString(R.string.entry_jumlah)
                }
                else -> {
                    db.collection("pesanan").document(pesananId).collection("menuPesanan").document(menuPesananId)
                        .update(updateMap).addOnSuccessListener {
                            dismiss()
                            val intent = Intent(requireContext(), PemesananBerhasilActivity::class.java)
                            intent.putExtra(Cons.EXTRA_NAMA, getString(R.string.from_tambah_porsi))
                            startActivity(intent)
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