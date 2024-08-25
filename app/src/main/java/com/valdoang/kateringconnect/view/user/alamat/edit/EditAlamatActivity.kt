package com.valdoang.kateringconnect.view.user.alamat.edit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.databinding.ActivityEditAlamatBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.user.alamat.DeleteAlamatFragment

class EditAlamatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAlamatBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var userId = ""
    private var nama = ""
    private var kota = ""
    private var alamat = ""
    private var namaKontak = ""
    private var nomorKontak = ""
    private var db = Firebase.firestore
    private var alamatId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAlamatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid

        alamatId = intent.getStringExtra(Cons.EXTRA_ID)

        setupAction()
        setupAlamat()
        editEach()
        deleteAlamat()
    }

    private fun setupAlamat() {
        val alamatRef = db.collection("user").document(userId).collection("alamatTersimpan").document(alamatId!!)
        alamatRef.addSnapshotListener { alamatDoc, _ ->
            if (alamatDoc != null) {
                nama = alamatDoc.data?.get("nama").toString()
                kota = alamatDoc.data?.get("kota").toString()
                alamat = alamatDoc.data?.get("alamat").toString()
                namaKontak = alamatDoc.data?.get("namaKontak").toString()
                nomorKontak = alamatDoc.data?.get("nomorKontak").toString()

                binding.tvNama.text = nama
                binding.tvKota.text = kota
                binding.tvAlamat.text = alamat
                binding.tvNamaKontak.text = namaKontak
                binding.tvNomorKontak.text = nomorKontak
            }
        }
    }

    private fun editEach() {
        binding.cvNama.setOnClickListener {
            val intent = Intent(this, EditNamaAlamatActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, nama)
            intent.putExtra(Cons.EXTRA_ID, alamatId)
            startActivity(intent)
        }
        binding.cvKota.setOnClickListener {
            val intent = Intent(this, EditKotaAlamatActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, kota)
            intent.putExtra(Cons.EXTRA_ID, alamatId)
            startActivity(intent)
        }
        binding.cvAlamat.setOnClickListener {
            val intent = Intent(this, EditAlamatAlamatActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, alamat)
            intent.putExtra(Cons.EXTRA_ID, alamatId)
            startActivity(intent)
        }
        binding.cvNamaKontak.setOnClickListener {
            val intent = Intent(this, EditNamaKontakAlamatActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, namaKontak)
            intent.putExtra(Cons.EXTRA_ID, alamatId)
            startActivity(intent)
        }
        binding.cvNomorKontak.setOnClickListener {
            val intent = Intent(this, EditNomorKontakAlamatActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, nomorKontak)
            intent.putExtra(Cons.EXTRA_ID, alamatId)
            startActivity(intent)
        }
    }

    private fun deleteAlamat() {
        binding.tvHapus.setOnClickListener {
            val args = Bundle()
            args.putString("alamatId", alamatId)
            val dialog = DeleteAlamatFragment()
            dialog.arguments = args
            dialog.show(supportFragmentManager, "deleteAlamatDialog")
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}