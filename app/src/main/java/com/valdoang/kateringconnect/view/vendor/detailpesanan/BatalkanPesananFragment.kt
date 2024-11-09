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

class BatalkanPesananFragment : DialogFragment() {
    private var _binding: FragmentTolakBatalkanPesananBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId: String? = null
    private var total: String? = null
    private var pesananId: String? = null
    private var saldoUser = ""
    private var metodePembayaran = ""

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
        pesananId = mArgs!!.getString("id")
        userId = mArgs.getString("userId")
        total = mArgs.getString("total")

        setUI()
        getSaldo()
        getMetodePembayaran()
        batalkanPesanan()
        closeDialog()
        return root
    }

    private fun getSaldo() {
        val userRef = db.collection("user").document(userId!!)
        userRef.addSnapshotListener { userSnapshot, _ ->
            if (userSnapshot != null) {
                saldoUser = userSnapshot.data?.get("saldo").toString()
                if (saldoUser == "null") {
                    saldoUser = "0"
                }
            }
        }
    }

    private fun getMetodePembayaran() {
        val pesananRef = db.collection("pesanan").document(pesananId!!)
        pesananRef.get().addOnSuccessListener {  pesananSnapshot->
            if (pesananSnapshot != null) {
                metodePembayaran = pesananSnapshot.data?.get("metodePembayaran").toString()
            }
        }
    }

    private fun addMutasiIntoUserDatabase() {
        val sDate = System.currentTimeMillis().toString()
        val sJenis = getString(R.string.kredit)
        val sKeterangan = getString(R.string.pengembalian_dana)

        val mutasiMap = hashMapOf(
            "tanggal" to sDate,
            "jenis" to sJenis,
            "keterangan" to sKeterangan,
            "nominal" to total,
        )

        val userRef = db.collection("user").document(userId!!)
        val newMutasi = userRef.collection("mutasi").document()
        newMutasi.set(mutasiMap).addOnSuccessListener {
            val newSaldo = saldoUser.toLong() + total!!.toLong()

            val saldoMap = mapOf(
                "saldo" to newSaldo.toString()
            )
            userRef.update(saldoMap)
        }

    }

    private fun batalkanPesanan() {
        binding.btnKirim.setOnClickListener {
            val sAlasan = binding.edAlasan.text.toString().trim()

            val alasanMap = mapOf(
                "status" to getString(R.string.status_batal),
                "alasan" to sAlasan
            )
            db.collection("pesanan").document(pesananId!!).update(alasanMap)
                .addOnSuccessListener {
                    if (metodePembayaran == getString(R.string.kc_wallet)) {
                        addMutasiIntoUserDatabase()
                    }

                    Toast.makeText(requireContext(), R.string.success_batalkan_pesanan, Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), R.string.fail_batalkan_pesanan, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setUI() {
        binding.titleAlasan.text = getString(R.string.title_alasan_pembatalan)
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