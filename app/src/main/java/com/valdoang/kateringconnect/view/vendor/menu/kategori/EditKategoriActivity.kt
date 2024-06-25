package com.valdoang.kateringconnect.view.vendor.menu.kategori

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.databinding.ActivityEditKategoriBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.beforeChangedListener

class EditKategoriActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditKategoriBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var kategoriMenuId: String? = null
    private lateinit var etNama: EditText
    private lateinit var tvHapus: TextView
    private lateinit var btnSimpan: Button
    private lateinit var ibBack: ImageButton
    private var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditKategoriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid

        kategoriMenuId = intent.getStringExtra(Cons.EXTRA_ID)

        etNama = binding.edEditNamaKategori
        tvHapus = binding.tvHapus
        btnSimpan = binding.btnSimpan
        ibBack = binding.ibBack

        setupAction()
        setupKategori()
        updateKategori()
        deleteKategori()
    }

    private fun setupKategori() {
        val kategoriRef = db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenuId!!)
        kategoriRef.get().addOnSuccessListener {  document ->
            if (document != null) {
                val namaKategori = document.data?.get("nama").toString()
                etNama.setText(namaKategori)
            }
        }
    }

    private fun updateKategori() {
        etNama.beforeChangedListener(btnSimpan)

        btnSimpan.setOnClickListener {
            val sNama = etNama.text.toString().trim()
            val updateMap = mapOf(
                "nama" to sNama
            )

            val kategoriRef = db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenuId!!)
            kategoriRef.update(updateMap)
            finish()
        }
    }

    private fun deleteKategori() {
        tvHapus.setOnClickListener {
            val args = Bundle()
            args.putString("kategoriMenuId", kategoriMenuId)
            val dialog = DeleteKategoriFragment()
            dialog.arguments = args
            dialog.show(supportFragmentManager, "deleteKategoriDialog")
        }
    }

    private fun setupAction() {
        ibBack.setOnClickListener {
            finish()
        }
    }
}