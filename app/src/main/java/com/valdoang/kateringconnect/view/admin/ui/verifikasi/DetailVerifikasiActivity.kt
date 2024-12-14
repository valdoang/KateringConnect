package com.valdoang.kateringconnect.view.admin.ui.verifikasi

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityDetailVerifikasiBinding
import com.valdoang.kateringconnect.utils.Cons

class DetailVerifikasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailVerifikasiBinding
    private var userId: String? = null
    private var db = Firebase.firestore
    private lateinit var progressBar: ProgressBar
    private var fotoKtp = ""
    private var fotoSelfieKtp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailVerifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        userId = intent.getStringExtra(Cons.EXTRA_ID)
        progressBar = binding.progressBar
        
        setupData()
        setupOnClick()
        setupButton()
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val userRef = db.collection("user").document(userId!!)
        userRef.get().addOnSuccessListener { document->
            progressBar.visibility = View.GONE
            if (document != null) {
                val nama = document.data?.get("nama").toString()
                val email = document.data?.get("email").toString()
                val kota = document.data?.get("kota").toString()
                val alamat = document.data?.get("alamat").toString()
                val telepon = document.data?.get("telepon").toString()
                fotoKtp = document.data?.get("fotoKtp").toString()
                fotoSelfieKtp = document.data?.get("fotoSelfieKtp").toString()

                binding.tvNama.text = nama
                binding.tvEmail.text = email
                binding.tvKota.text = kota
                binding.tvAlamat.text = alamat
                binding.tvTelepon.text = telepon
                Glide.with(this).load(fotoKtp).into(binding.ivKtp)
                Glide.with(this).load(fotoSelfieKtp).into(binding.ivSelfieKtp)
            }
        }
    }

    private fun setupOnClick() {
        binding.cvKtp.setOnClickListener {
            val args = Bundle()
            args.putString("foto", fotoKtp)
            val newFragment = DetailPhotoFragment()
            newFragment.arguments = args
            newFragment.show(supportFragmentManager, "detailPhotoFragment")
        }
        binding.cvSelfieKtp.setOnClickListener {
            val args = Bundle()
            args.putString("foto", fotoSelfieKtp)
            val newFragment = DetailPhotoFragment()
            newFragment.arguments = args
            newFragment.show(supportFragmentManager, "detailPhotoFragment")
        }
    }

    private fun setupButton() {
        binding.btnTerimaPendaftaran.setOnClickListener {
            val updateStatus = mapOf(
                "statusPendaftaran" to getString(R.string.diterima)
            )
            db.collection("user").document(userId!!).update(updateStatus)
            finish()
        }
        binding.btnTolakPendaftaran.setOnClickListener {
            val args = Bundle()
            args.putString("userId", userId)
            val dialog: DialogFragment = TolakPendaftaranFragment()
            dialog.arguments = args
            dialog.show(this.supportFragmentManager, "tolakPendaftaranDialog")
        }
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}