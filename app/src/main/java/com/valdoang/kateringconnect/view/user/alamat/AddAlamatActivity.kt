package com.valdoang.kateringconnect.view.user.alamat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityAddAlamatBinding

class AddAlamatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAlamatBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var userId = ""
    private lateinit var btnSimpan: Button
    private lateinit var etNama: EditText
    private lateinit var acKota: AutoCompleteTextView
    private lateinit var etAlamat: EditText
    private lateinit var etNamaKontak: EditText
    private lateinit var etNomorKontak: EditText
    private lateinit var progressBar: ProgressBar
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAlamatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid

        btnSimpan = binding.btnSimpan
        etNama = binding.edAlamatName
        acKota = binding.acKota
        etAlamat = binding.edAlamat
        etNamaKontak = binding.edNamaKontak
        etNomorKontak = binding.edNomorKontak
        progressBar = binding.progressBar

        setupAcCity()
        setupAction()
        saveAlamat()
    }

    private fun setupAcCity() {
        val cities = resources.getStringArray(R.array.Cities)
        val dropdownAdapter = ArrayAdapter(this, R.layout.dropdown_item, cities)
        acKota.setAdapter(dropdownAdapter)
    }

    private fun saveAlamat() {
        btnSimpan.setOnClickListener {
            val sNama = etNama.text.toString().trim()
            val sKota = acKota.text.toString().trim()
            val sAlamat = etAlamat.text.toString().trim()
            val sNamaKontak = etNamaKontak.text.toString().trim()
            val sNomorKontak = etNomorKontak.text.toString().trim()

            val alamatMap = mapOf(
                "nama" to sNama,
                "kota" to sKota,
                "alamat" to sAlamat,
                "namaKontak" to sNamaKontak,
                "nomorKontak" to sNomorKontak
            )

            when {
                sNama.isEmpty() -> {
                    etNama.error = getString(R.string.entry_name)
                }
                sKota.isEmpty() -> {
                    acKota.error = getString(R.string.entry_city)
                }
                sAlamat.isEmpty() -> {
                    etAlamat.error = getString(R.string.entry_address)
                }
                sNamaKontak.isEmpty() -> {
                    etNamaKontak.error = getString(R.string.entry_nama_kontak)
                }
                sNomorKontak.isEmpty() -> {
                    etNomorKontak.error = getString(R.string.entry_nomor_kontak)
                }
                else -> {
                    progressBar.visibility = View.VISIBLE
                    val alamatRef = db.collection("user").document(userId).collection("alamatTersimpan").document()
                    alamatRef.set(alamatMap).addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.succesfully_add_alamat), Toast.LENGTH_SHORT).show()
                        finish()
                    } .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, getString(R.string.failed_add_alamat), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupAction(){
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}