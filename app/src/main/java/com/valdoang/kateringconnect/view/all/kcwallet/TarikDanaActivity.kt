package com.valdoang.kateringconnect.view.all.kcwallet

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityTarikDanaBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.allChangedListener
import com.valdoang.kateringconnect.utils.withNumberingFormat


class TarikDanaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTarikDanaBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var userId = firebaseAuth.currentUser!!.uid
    private var db = Firebase.firestore
    private var userRef = db.collection("user").document(userId)
    private var mutasiRef = userRef.collection("mutasi").document()
    private var tarikDanaRef = db.collection("tarikDana").document()
    private var saldoKCWallet = ""
    private lateinit var edJumlahPenarikan: EditText
    private lateinit var btnKonfirmasi: Button
    private lateinit var progressBar: ProgressBar
    private var namaBank = ""
    private var nomorRekening = ""
    private var namaPemilikRekening = ""
    private var totalPenarikan = 0L
    private var penarikanDana = ""
    private var minTarikDana = ""
    private var adminTarikDana = ""
    private var totalPemasukanAdmin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTarikDanaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        edJumlahPenarikan = binding.edNominal
        btnKonfirmasi = binding.btnKonfirmasi
        progressBar = binding.progressBar

        setupView()
        setupData()
        tarikDana()
        setupAction()
    }

    private fun setupView() {
        val adminRef = db.collection("user").document(Cons.ADMIN_ID)
        adminRef.addSnapshotListener { adminSnapshot, _ ->
            if (adminSnapshot != null) {
                minTarikDana = adminSnapshot.data?.get("minTarikDana").toString()
                adminTarikDana = adminSnapshot.data?.get("adminTarikDana").toString()
                totalPemasukanAdmin = adminSnapshot.data?.get("totalPemasukan").toString()

                binding.tvMinTarikDana.text = getString(R.string.min_tarik_dana, minTarikDana.withNumberingFormat())
                binding.tvAdminTarikDana.text = getString(R.string.tv_admin, adminTarikDana.withNumberingFormat())

                edJumlahPenarikan.allChangedListener { nominal ->
                    val totalMinPenarikan = minTarikDana.toLong() + adminTarikDana.toLong()


                    if (nominal == "") {
                        btnKonfirmasi.isEnabled = false
                    } else {
                        totalPenarikan = nominal.toLong() + adminTarikDana.toLong()

                        if (saldoKCWallet.toLong() < totalPenarikan) {
                            btnKonfirmasi.isEnabled = false
                        } else {
                            btnKonfirmasi.isEnabled = totalPenarikan >= totalMinPenarikan
                        }
                    }
                }
            }
        }
    }

    private fun setupData() {
        userRef.addSnapshotListener { userSnapshot, _ ->
            if (userSnapshot != null) {
                namaBank = userSnapshot.data?.get("namaBank").toString()
                nomorRekening = userSnapshot.data?.get("nomorRekening").toString()
                namaPemilikRekening = userSnapshot.data?.get("namaPemilikRekening").toString()
                saldoKCWallet = userSnapshot.data?.get("saldo").toString()
                penarikanDana = userSnapshot.data?.get("penarikanDana").toString()

                if (penarikanDana == "null") {
                    penarikanDana = "0"
                }

                if (namaBank == "null") {
                    binding.cvRekeningBank.visibility = View.GONE

                    val dialog = BottomSheetDialog(this)
                    val view = layoutInflater.inflate(R.layout.bottom_sheet_tarik_dana, null)

                    dialog.behavior.isDraggable = false
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.setOnKeyListener { _, keyCode, _ ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss()
                            finish()
                        }
                        true
                    }

                    val btnTambahRekening = view.findViewById<Button>(R.id.btn_tambah_rekening)

                    btnTambahRekening.setOnClickListener {
                        val intent = Intent(this, RekeningBankActivity::class.java)
                        startActivity(intent)
                        dialog.dismiss()
                        finish()
                    }

                    dialog.setContentView(view)
                    dialog.show()
                }

                binding.tvNamaBank.text = namaBank
                binding.tvNomorRekening.text = nomorRekening
                binding.tvNamaPemilikRekening.text = namaPemilikRekening
                binding.tvSaldoKcwalletValue.text = getString(R.string.rupiah_text, saldoKCWallet.withNumberingFormat())
            }
        }
    }

    private fun tarikDana() {
        binding.btnKonfirmasi.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            val sNominal = binding.edNominal.text.toString().trim()
            val sDate = System.currentTimeMillis().toString()
            val sStatus = getString(R.string.status_proses)

            val tarikDanaMap = mapOf(
                "userId" to userId,
                "namaBank" to namaBank,
                "nomorRekening" to nomorRekening,
                "namaPemilikRekening" to namaPemilikRekening,
                "nominal" to sNominal,
                "tanggalPengajuan" to sDate,
                "status" to sStatus
            )

            val sJenis = getString(R.string.kredit)
            val sKeterangan = getString(R.string.penarikan_dana)

            val mutasiMap = hashMapOf(
                "tanggal" to sDate,
                "jenis" to sJenis,
                "keterangan" to sKeterangan,
                "nominal" to sNominal,
            )

            val sSaldo = saldoKCWallet.toLong() - totalPenarikan
            val sPenarikanDana = penarikanDana.toLong() + totalPenarikan

            val kcwalletMap = mapOf(
                "saldo" to sSaldo.toString(),
                "penarikanDana" to sPenarikanDana.toString()
            )

            tarikDanaRef.set(tarikDanaMap).addOnSuccessListener {
                userRef.update(kcwalletMap)
                mutasiRef.set(mutasiMap)
                adminAddPemasukan()

                val intent = Intent(this, TarikDanaBerhasilActivity::class.java)
                startActivity(intent)
                finish()
            } .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, getString(R.string.fail_tarik_dana), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun adminAddPemasukan() {
        val sDate = System.currentTimeMillis().toString()
        val sKeterangan = getString(R.string.biaya_admin_tarik_dana)
        val sNominal = adminTarikDana

        val mutasiMap = hashMapOf(
            "tanggal" to sDate,
            "keterangan" to sKeterangan,
            "nominal" to sNominal,
        )

        val adminRef = db.collection("user").document(Cons.ADMIN_ID)
        val newPemasukanRef = adminRef.collection("pemasukan").document()
        newPemasukanRef.set(mutasiMap).addOnSuccessListener {
            val newTotalPemasukan = totalPemasukanAdmin.toLong() + sNominal.toLong()

            val totalPemasukanMap = mapOf(
                "totalPemasukan" to newTotalPemasukan.toString()
            )
            adminRef.update(totalPemasukanMap)
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
        binding.ibHistory.setOnClickListener {
            val intent = Intent(this, RiwayatTarikDanaActivity::class.java)
            startActivity(intent)
        }
    }
}