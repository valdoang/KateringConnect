package com.valdoang.kateringconnect.view.all.kcwallet

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityAddEditRekeningBankBinding
import com.valdoang.kateringconnect.utils.Cons

class AddEditRekeningBankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditRekeningBankBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var db = Firebase.firestore
    private var userId = firebaseAuth.currentUser!!.uid
    private var userRef = db.collection("user").document(userId)
    private lateinit var title: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var ibSave: ImageButton
    private var addOrEdit: String? = null
    private lateinit var edNamaBank: EditText
    private lateinit var edNomorRekening: EditText
    private lateinit var edNamaPemilikRekening: EditText
    private var namaBank =""
    private var nomorRekening =""
    private var namaPemilikRekening =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditRekeningBankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        title = binding.titleAddEditRekeningBank
        progressBar = binding.progressBar
        ibSave = binding.ibSave
        edNamaBank = binding.edNamaBank
        edNomorRekening = binding.edNomorRekening
        edNamaPemilikRekening = binding.edNamaPemilikRekening

        addOrEdit = intent.getStringExtra(Cons.EXTRA_NAMA)

        setupData()
        setupUI()
        saveData()
        setupAction()
    }

    private fun setupData() {
        userRef.addSnapshotListener { userSnapshot, _ ->
            if (userSnapshot != null) {
                namaBank = userSnapshot.data?.get("namaBank").toString()
                nomorRekening = userSnapshot.data?.get("nomorRekening").toString()
                namaPemilikRekening = userSnapshot.data?.get("namaPemilikRekening").toString()

                if (namaBank == "null") {
                    namaBank = ""
                }
                if (nomorRekening == "null") {
                    nomorRekening = ""
                }
                if (namaPemilikRekening == "null") {
                    namaPemilikRekening = ""
                }

                edNamaBank.setText(namaBank)
                edNomorRekening.setText(nomorRekening)
                edNamaPemilikRekening.setText(namaPemilikRekening)
            }
        }
    }

    private fun setupUI() {
        when(addOrEdit) {
            getString(R.string.tambah_rekening_bank) -> {
                title.text = getString(R.string.tambah_rekening_bank)
            }
            getString(R.string.ubah_rekening_bank) -> {
                title.text = getString(R.string.ubah_rekening_bank)
            }
        }
    }

    private fun saveData() {
        ibSave.setOnClickListener {
            val sNamaBank = edNamaBank.text.toString().trim()
            val sNomorRekening = edNomorRekening.text.toString().trim()
            val sNamaPemilikRekening = edNamaPemilikRekening.text.toString().trim()

            val rekeningMap = mapOf(
                "namaBank" to sNamaBank,
                "nomorRekening" to sNomorRekening,
                "namaPemilikRekening" to sNamaPemilikRekening
            )

            when {
                sNamaBank.isEmpty() -> {
                    edNamaBank.error = getString(R.string.wajib_diisi)
                }
                sNomorRekening.isEmpty() -> {
                    edNomorRekening.error = getString(R.string.wajib_diisi)
                }
                sNamaPemilikRekening.isEmpty() -> {
                    edNamaPemilikRekening.error = getString(R.string.wajib_diisi)
                }
                else -> {
                    ibSave.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE

                    userRef.update(rekeningMap).addOnSuccessListener {
                        finish()
                    } .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        when(addOrEdit) {
                            getString(R.string.tambah_rekening_bank) -> {
                                Toast.makeText(this, getString(R.string.fail_tambah_rekening_bank), Toast.LENGTH_SHORT).show()
                            }
                            getString(R.string.ubah_rekening_bank) -> {
                                Toast.makeText(this, getString(R.string.fail_ubah_rekening_bank), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.ibClose.setOnClickListener {
            finish()
        }
    }
}