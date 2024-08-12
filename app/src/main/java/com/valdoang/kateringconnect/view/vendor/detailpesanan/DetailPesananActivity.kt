package com.valdoang.kateringconnect.view.vendor.detailpesanan

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityDetailPesananRiwayatBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.utils.withTimestamptoDateFormat
import com.valdoang.kateringconnect.utils.withTimestamptoTimeFormat

class DetailPesananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPesananRiwayatBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var pesananId: String? = null
    private var menuId: String? = null
    private var vendorId = ""
    private lateinit var tvVendorNama: TextView
    private lateinit var tvMenuNama: TextView
    private lateinit var tvMenuDesc: TextView
    private lateinit var tvUserNama: TextView
    private lateinit var tvUserAlamat: TextView
    private lateinit var tvUserTelepon: TextView
    private lateinit var tvPesananId: TextView
    private lateinit var tvPesananStatus: TextView
    private lateinit var tvPesananJumlah: TextView
    private lateinit var tvPesananTotalPembayaran: TextView
    private lateinit var tvPesananSubtotal: TextView
    private lateinit var tvPesananOngkir: TextView
    private lateinit var tvPesananMetodePembayaran: TextView
    private lateinit var tvPesananPilihanOpsi: TextView
    private lateinit var tvPesananCatatan: TextView
    private lateinit var tvPesananTanggal: TextView
    private lateinit var tvPesananJam: TextView
    private lateinit var btnSelesaikan: Button
    private lateinit var btnBatalkan: Button

    //TODO: SESUAIKAN FORMAT PEMESANAN YANG TERBARU

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth

        pesananId = intent.getStringExtra(Cons.EXTRA_ID)

        tvVendorNama = binding.tvVendorName
        tvMenuNama = binding.tvMenuName
        tvMenuDesc = binding.tvMenuDesc
        tvUserNama = binding.tvUserName
        tvUserAlamat = binding.tvAddress
        tvUserTelepon = binding.tvNoPhone
        tvPesananId = binding.tvIdValue
        tvPesananStatus = binding.tvStatusValue
        tvPesananJumlah = binding.tvJumlahValue
        tvPesananTotalPembayaran = binding.tvTotalValue
        tvPesananSubtotal = binding.tvSubtotalValue
        tvPesananOngkir = binding.tvOngkirValue
        tvPesananMetodePembayaran = binding.tvPembayaranValue
        tvPesananPilihanOpsi = binding.tvPilihanOpsiValue
        tvPesananCatatan = binding.tvCatatanValue
        tvPesananTanggal = binding.tvTanggalValue
        tvPesananJam = binding.tvJamValue
        btnSelesaikan = binding.btnSelesaikan
        btnBatalkan = binding.btnBatalkan

        setupAction()
        hideUI()
        setupData()
    }

    private fun setupData() {
        db.collection("pesanan").document(pesananId!!)
            .addSnapshotListener { pesanan,_ ->
                if (pesanan != null) {
                    val pesananId = pesanan.id
                    menuId = pesanan.data?.get("menuId").toString()
                    val menuNama = pesanan.data?.get("menuNama").toString()
                    vendorId = pesanan.data?.get("vendorId").toString()
                    val vendorNama = pesanan.data?.get("vendorNama").toString()
                    val status = pesanan.data?.get("status").toString()
                    val jumlah = pesanan.data?.get("jumlah").toString()
                    val catatan = pesanan.data?.get("catatan").toString()
                    val pilihanOpsi = pesanan.data?.get("namaOpsi").toString()
                    val jadwal = pesanan.data?.get("jadwal").toString()
                    val metodePembayaran = pesanan.data?.get("metodePembayaran").toString()
                    val totalPembayaran = pesanan.data?.get("totalHarga").toString()
                    val subtotal = pesanan.data?.get("subtotal").toString()
                    val ongkir = pesanan.data?.get("ongkir").toString()
                    val menuDesc = pesanan.data?.get("menuKeterangan").toString()
                    val userNama = pesanan.data?.get("userNama").toString()
                    val userKota = pesanan.data?.get("userKota").toString()
                    val userAlamat = pesanan.data?.get("userAlamat").toString()
                    val userTelepon = pesanan.data?.get("userTelepon").toString()

                    tvMenuDesc.text = menuDesc

                    tvUserNama.text = userNama
                    tvUserAlamat.text = getString(R.string.tv_address_city, userAlamat, userKota)
                    tvUserTelepon.text = userTelepon

                    tvVendorNama.text = vendorNama
                    tvMenuNama.text = menuNama
                    tvPesananId.text = pesananId
                    tvPesananStatus.text = status
                    tvPesananJumlah.text = jumlah
                    tvPesananTotalPembayaran.text = totalPembayaran.withNumberingFormat()
                    tvPesananSubtotal.text = subtotal.withNumberingFormat()
                    tvPesananOngkir.text = ongkir.withNumberingFormat()
                    tvPesananMetodePembayaran.text = metodePembayaran
                    if (catatan == "") {
                        tvPesananCatatan.text = getString(R.string.tidak_ada)
                    } else {
                        tvPesananCatatan.text = catatan
                    }

                    if (pilihanOpsi == "") {
                        tvPesananPilihanOpsi.text = getString(R.string.tidak_ada)
                    } else {
                        tvPesananPilihanOpsi.text = pilihanOpsi
                    }
                    tvPesananTanggal.text = jadwal.withTimestamptoDateFormat()
                    tvPesananJam.text = jadwal.withTimestamptoTimeFormat()


                }
            }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
        btnSelesaikan.setOnClickListener {
            val updateStatus = mapOf(
                "status" to getString(R.string.status_selesai)
            )
            db.collection("pesanan").document(pesananId!!).update(updateStatus)

            it.visibility = View.GONE
            btnBatalkan.visibility = View.GONE
            finish()
        }
        btnBatalkan.setOnClickListener {
            val updateStatus = mapOf(
                "status" to getString(R.string.status_batal)
            )
            db.collection("pesanan").document(pesananId!!).update(updateStatus)

            btnSelesaikan.visibility = View.GONE
            it.visibility = View.GONE
            finish()
        }
    }

    private fun hideUI() {
        binding.tvVendorName.visibility = View.GONE
    }
}