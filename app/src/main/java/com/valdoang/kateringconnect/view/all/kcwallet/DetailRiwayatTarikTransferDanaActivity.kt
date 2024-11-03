package com.valdoang.kateringconnect.view.all.kcwallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityDetailRiwayatTransferDanaBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat
import com.valdoang.kateringconnect.view.all.imageview.ImageViewActivity

class DetailRiwayatTarikTransferDanaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRiwayatTransferDanaBinding
    private var db = Firebase.firestore
    private lateinit var progressBar: ProgressBar
    private var tarikDanaId: String? = null
    private var who: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRiwayatTransferDanaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        progressBar = binding.progressBar
        tarikDanaId = intent.getStringExtra(Cons.EXTRA_ID)
        who = intent.getStringExtra(Cons.EXTRA_NAMA)

        setupUI()
        setupData()
        setupAction()
    }

    private fun setupUI() {
        when(who) {
            getString(R.string.user) -> {
                binding.titleDetailTransferDana.text = getString(R.string.rincian_riwayat_tarik_dana)
            }
            getString(R.string.admin) -> {
                binding.titleDetailTransferDana.text = getString(R.string.rincian_riwayat_transfer_dana)
            }
        }
    }

    private fun setupData() {
        val tarikDanaRef = db.collection("tarikDana").document(tarikDanaId!!)
        tarikDanaRef.get().addOnSuccessListener {  document->
            if (document != null) {
                val nominal = document.data?.get("nominal").toString()
                val tanggalPengajuan = document.data?.get("tanggalPengajuan").toString()
                val fotoBuktiTransfer = document.data?.get("fotoBuktiTransfer").toString()
                val namaBank = document.data?.get("namaBank").toString()
                val nomorRekening = document.data?.get("nomorRekening").toString()
                val namaPemilikRekening = document.data?.get("namaPemilikRekening").toString()
                val userId = document.data?.get("userId").toString()

                when(document.data?.get("status").toString()) {
                    getString(R.string.status_proses) -> {
                        binding.tvDibayarkanKe.visibility = View.GONE
                        binding.tvLihatBuktiTransfer.visibility = View.GONE
                        binding.tvTanggalDibayarkan.text = getString(R.string.kirim_ke)
                    }
                    getString(R.string.status_selesai) -> {
                        binding.tvDibayarkanKe.text = getString(R.string.telah_dibayarkan_ke)
                        binding.tvLihatBuktiTransfer.visibility = View.VISIBLE
                        binding.tvTanggalDibayarkan.visibility = View.VISIBLE
                        val tanggalDibayarkan = document.data?.get("tanggalDibayarkan").toString()
                        binding.tvTanggalDibayarkan.text = tanggalDibayarkan.withTimestampToDateTimeFormat()
                    }
                }

                binding.tvNominal.text = getString(R.string.rupiah_text, nominal.withNumberingFormat())
                binding.tvTanggalPengajuan.text = tanggalPengajuan.withTimestampToDateTimeFormat()
                binding.tvNamaBank.text = namaBank
                binding.tvNomorRekening.text = nomorRekening
                binding.tvNamaPemilikRekening.text = namaPemilikRekening

                binding.tvLihatBuktiTransfer.setOnClickListener {
                    val intent = Intent(this, ImageViewActivity::class.java)
                    intent.putExtra(Cons.EXTRA_NAMA, fotoBuktiTransfer)
                    startActivity(intent)
                }

                val userRef = db.collection("user").document(userId)
                userRef.addSnapshotListener { userSnapshot, _ ->
                    if (userSnapshot != null) {
                        val nama = userSnapshot.data?.get("nama").toString()
                        val foto = userSnapshot.data?.get("foto").toString()

                        binding.tvNamaUser.text = nama
                        Glide.with(this).load(foto).error(R.drawable.default_profile).into(binding.ivUser)
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}