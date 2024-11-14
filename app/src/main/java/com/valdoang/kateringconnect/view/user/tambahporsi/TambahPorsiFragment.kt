package com.valdoang.kateringconnect.view.user.tambahporsi

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
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
import com.valdoang.kateringconnect.view.user.pemesanan.KcWalletNotEnoughFragment
import com.valdoang.kateringconnect.view.user.pemesanan.PemesananBerhasilActivity

class TambahPorsiFragment : DialogFragment() {
    private var _binding: FragmentTambahPorsiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var userId = firebaseAuth.currentUser!!.uid
    private var db = Firebase.firestore
    private val userRef = db.collection("user").document(userId)
    private val newMutasi = userRef.collection("mutasi").document()
    private var jumlah = ""
    private var subtotal = ""
    private lateinit var etJumlah: EditText
    private lateinit var tvTotalPembayaran: TextView
    private var total = 0L
    private var sJumlah = 0L
    private var sSubtotal = 0L
    private lateinit var rgMetodePembayaran: RadioGroup
    private lateinit var btnPesan: Button
    private var pesananId: String? = null
    private var menuPesananId: String? = null
    private var namaMenu: String? = null
    private var namaUser = ""
    private var nomorUser = ""
    private var alamatUser = ""
    private var kotaUser = ""
    private var emailUser = ""
    private var metodePembayaran = ""
    private lateinit var tunai: RadioButton
    private lateinit var kcWallet: RadioButton
    private var saldoKcWallet = ""

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

        val mArgs = arguments
        pesananId = mArgs!!.getString("pesananId")
        menuPesananId = mArgs.getString("menuPesananId")
        namaMenu = mArgs.getString("namaMenu")

        etJumlah = binding.edJumlah
        tvTotalPembayaran = binding.tvTotalPembayaran
        rgMetodePembayaran = binding.rgMetodePembayaran
        btnPesan = binding.btnPesan
        tunai = binding.tunai
        kcWallet = binding.kcWallet

        val checkListener =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked && etJumlah.text.toString() != "") {
                    btnPesan.isEnabled = true
                }
            }

        tunai.setOnCheckedChangeListener(checkListener)
        kcWallet.setOnCheckedChangeListener(checkListener)

        setupUserData()
        closeDialog()
        setupData()
        tambahPorsi()
        return root
    }

    private fun setupUserData() {
        val pesananRef = db.collection("pesanan").document(pesananId!!)
        pesananRef.get().addOnSuccessListener { pesananSnapshot ->
            if (pesananSnapshot != null) {
                namaUser = pesananSnapshot.data?.get("userNama").toString()
                nomorUser = pesananSnapshot.data?.get("userTelepon").toString()
                alamatUser = pesananSnapshot.data?.get("userAlamat").toString()
                kotaUser = pesananSnapshot.data?.get("userKota").toString()
                metodePembayaran = pesananSnapshot.data?.get("metodePembayaran").toString()

                when (metodePembayaran) {
                    getString(R.string.tunai) -> {
                        tunai.isChecked = true
                        kcWallet.isEnabled = false
                    }
                    getString(R.string.kc_wallet) -> {
                        kcWallet.isChecked = true
                        tunai.isEnabled = false
                    }
                }
            }
        }

        val userRef = db.collection("user").document(userId)
        userRef.get().addOnSuccessListener { userSnapshot ->
            if (userSnapshot != null) {
                emailUser = userSnapshot.data?.get("email").toString()
                saldoKcWallet = userSnapshot.data?.get("saldo").toString()
                if (saldoKcWallet == "null") {
                    saldoKcWallet = "0"
                }

                kcWallet.text = getString(R.string.rupiah_text, saldoKcWallet.withNumberingFormat())
            }
        }
    }

    private fun setupData() {
        val ref = db.collection("pesanan").document(pesananId!!).collection("menuPesanan").document(menuPesananId!!)
        ref.get().addOnSuccessListener { document ->
                if (document != null) {
                    jumlah = document.data?.get("jumlah").toString()
                    subtotal = document.data?.get("subtotal").toString()
                    val hargaPerPorsi = document.data?.get("hargaPerPorsi").toString()

                    etJumlah.allChangedListener { etjumlah ->
                        if (etjumlah == "") {
                            tvTotalPembayaran.text = "0"
                            btnPesan.isEnabled = false
                        } else {
                            total = etjumlah.toLong() * hargaPerPorsi.toLong()
                            tvTotalPembayaran.text = total.withNumberingFormat()

                            if (rgMetodePembayaran.checkedRadioButtonId != -1) {
                                btnPesan.isEnabled = true
                            }

                            //untuk updateMap
                            sJumlah = etjumlah.toLong() + jumlah.toLong()
                            sSubtotal = total + subtotal.toLong()
                        }
                    }
                }
            }
    }

    private fun tambahPorsi() {
        binding.btnPesan.setOnClickListener {
            when (metodePembayaran) {
                getString(R.string.tunai) -> {
                    addTambahPorsiIntoDatabase()
                }
                getString(R.string.kc_wallet) -> {
                    kcWalletPayment()
                }
            }
        }
    }

    private fun addTambahPorsiIntoDatabase() {
        val updateMap = mapOf(
            "jumlah" to sJumlah.toString(),
            "subtotal" to sSubtotal.toString(),
            "tambahPorsi" to true
        )

        db.collection("pesanan").document(pesananId!!).collection("menuPesanan").document(menuPesananId!!)
            .update(updateMap).addOnSuccessListener {
                dismiss()
                val intent = Intent(requireContext(), PemesananBerhasilActivity::class.java)
                intent.putExtra(Cons.EXTRA_NAMA, getString(R.string.from_tambah_porsi))
                startActivity(intent)

                if (metodePembayaran == getString(R.string.kc_wallet)) {
                    val sTanggal = System.currentTimeMillis().toString()
                    val sJenis = getString(R.string.debit)
                    val sKeterangan = getString(R.string.penambahan_porsi_katering)

                    val mutasiMap = hashMapOf(
                        "tanggal" to sTanggal,
                        "jenis" to sJenis,
                        "keterangan" to sKeterangan,
                        "nominal" to total.toString(),
                    )

                    val sSaldo = saldoKcWallet.toLong() - total

                    newMutasi.set(mutasiMap).addOnSuccessListener {
                        val saldoMap = mapOf(
                            "saldo" to sSaldo.toString()
                        )

                        userRef.update(saldoMap)
                    } .addOnFailureListener {
                        Toast.makeText(requireContext(), getString(R.string.fail_penambahan), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), R.string.fail_penambahan, Toast.LENGTH_SHORT).show()
            }
    }

    private fun kcWalletPayment() {
        if (saldoKcWallet.toLong() < total) {
            val args = Bundle()
            args.putString("totalHarga", total.toString())
            val dialog: DialogFragment = KcWalletNotEnoughFragment()
            dialog.arguments = args
            dialog.show(this.parentFragmentManager, "KcWalletNotEnoughDialog")
        } else {
            addTambahPorsiIntoDatabase()
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