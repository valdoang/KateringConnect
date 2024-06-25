package com.valdoang.kateringconnect.view.vendor.menu.kategori

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.databinding.ActivityAddKategoriBinding
import com.valdoang.kateringconnect.utils.beforeChangedListener

class AddKategoriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddKategoriBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var etKategori: EditText
    private lateinit var btnSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddKategoriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        etKategori = binding.edAddNamaKategori
        btnSimpan = binding.btnSimpan

        setupAction()
        saveData()
    }

    private fun saveData() {
        etKategori.beforeChangedListener(btnSimpan)

        btnSimpan.setOnClickListener {
            val userId = firebaseAuth.currentUser!!.uid
            val sNama = etKategori.text.toString().trim()

            val kategoriMap = mapOf(
                "nama" to sNama
            )

            val ref = db.collection("user").document(userId).collection("kategoriMenu").document()
            ref.set(kategoriMap).addOnSuccessListener {
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal Menambahkan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}