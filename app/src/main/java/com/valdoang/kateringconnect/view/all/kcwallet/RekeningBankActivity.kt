package com.valdoang.kateringconnect.view.all.kcwallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityRekeningBankBinding
import com.valdoang.kateringconnect.utils.Cons

class RekeningBankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRekeningBankBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var userId = firebaseAuth.currentUser!!.uid
    private var db = Firebase.firestore
    private var userRef = db.collection("user").document(userId)
    private var addOrEdit = ""
    private lateinit var tvAddOrEdit: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRekeningBankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        tvAddOrEdit = binding.tvTambahUbahRekening

        setupData()
        setupAction()
    }

    private fun setupData() {
        userRef.addSnapshotListener { userSnapshot, _ ->
            if (userSnapshot != null) {
                var namaBank = userSnapshot.data?.get("namaBank").toString()
                var nomorRekening = userSnapshot.data?.get("nomorRekening").toString()
                var namaPemilikRekening = userSnapshot.data?.get("namaPemilikRekening").toString()

                if (namaBank == "null") {
                    namaBank = "-"
                    addOrEdit = getString(R.string.tambah_rekening_bank)
                } else {
                    addOrEdit = getString(R.string.ubah_rekening_bank)
                }
                if (nomorRekening == "null") {
                    nomorRekening = "-"
                }
                if (namaPemilikRekening == "null") {
                    namaPemilikRekening = "-"
                }

                binding.tvNamaBank.text = namaBank
                binding.tvNomorRekening.text = nomorRekening
                binding.tvNamaPemilikRekening.text = namaPemilikRekening

                setupUI()
            }
        }
    }

    private fun setupUI() {
        when(addOrEdit) {
            getString(R.string.tambah_rekening_bank) -> {
                tvAddOrEdit.text = getString(R.string.tambah_rekening)
            }
            getString(R.string.ubah_rekening_bank) -> {
                tvAddOrEdit.text = getString(R.string.ubah_rekening)
            }
        }
    }

    private fun setupAction() {
        tvAddOrEdit.setOnClickListener {
            val intent = Intent(this, AddEditRekeningBankActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, addOrEdit)
            startActivity(intent)
        }
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}