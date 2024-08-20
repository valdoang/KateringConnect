package com.valdoang.kateringconnect.view.vendor.detailpesanan

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.DetailPesananAdapter
import com.valdoang.kateringconnect.databinding.ActivityDetailPesananRiwayatBinding
import com.valdoang.kateringconnect.model.Keranjang
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.utils.withTimestamptoDateFormat
import com.valdoang.kateringconnect.utils.withTimestamptoTimeFormat

class DetailPesananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPesananRiwayatBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var pesananId: String? = null
    private var vendorId = ""
    private lateinit var tvVendorNama: TextView
    private lateinit var tvUserNama: TextView
    private lateinit var tvUserAlamat: TextView
    private lateinit var tvUserTelepon: TextView
    private lateinit var tvPesananId: TextView
    private lateinit var tvPesananStatus: TextView
    private lateinit var tvPesananOngkir: TextView
    private lateinit var tvPesananMetodePembayaran: TextView
    private lateinit var tvPesananTanggal: TextView
    private lateinit var tvPesananJam: TextView
    private lateinit var btnSelesaikan: Button
    private lateinit var btnBatalkan: Button
    private var menuPesananList: ArrayList<Keranjang> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var detailPesananAdapter: DetailPesananAdapter
    private lateinit var tvPesananTotalPembayaran: TextView
    private lateinit var tvPesananSubtotal: TextView
    private lateinit var tvPesananSubtotalValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth

        pesananId = intent.getStringExtra(Cons.EXTRA_ID)

        tvVendorNama = binding.tvNamaVendor
        tvUserNama = binding.tvUserName
        tvUserAlamat = binding.tvAddress
        tvUserTelepon = binding.tvNoPhone
        tvPesananId = binding.tvIdValue
        tvPesananStatus = binding.tvStatusValue
        tvPesananOngkir = binding.tvOngkirValue
        tvPesananMetodePembayaran = binding.tvPembayaranValue
        tvPesananTanggal = binding.tvTanggalValue
        tvPesananJam = binding.tvJamValue
        btnSelesaikan = binding.btnSelesaikan
        btnBatalkan = binding.btnBatalkan

        //TODO: KIRIM KE ADAPTER
        tvPesananTotalPembayaran = binding.tvTotalValue
        tvPesananSubtotal = binding.tvSubtotal
        tvPesananSubtotalValue = binding.tvSubtotalValue

        setupAction()
        setUI()
        setupView()
        setupData()
        setupDataMenu()
    }

    private fun setupData() {
        db.collection("pesanan").document(pesananId!!)
            .addSnapshotListener { pesanan,_ ->
                if (pesanan != null) {
                    val pesananId = pesanan.id
                    vendorId = pesanan.data?.get("vendorId").toString()
                    val vendorNama = pesanan.data?.get("vendorNama").toString()
                    val status = pesanan.data?.get("status").toString()
                    val jadwal = pesanan.data?.get("jadwal").toString()
                    val metodePembayaran = pesanan.data?.get("metodePembayaran").toString()
                    val ongkir = pesanan.data?.get("ongkir").toString()
                    val userNama = pesanan.data?.get("userNama").toString()
                    val userKota = pesanan.data?.get("userKota").toString()
                    val userAlamat = pesanan.data?.get("userAlamat").toString()
                    val userTelepon = pesanan.data?.get("userTelepon").toString()

                    tvUserNama.text = userNama
                    tvUserAlamat.text = getString(R.string.tv_address_city, userAlamat, userKota)
                    tvUserTelepon.text = userTelepon
                    tvVendorNama.text = vendorNama

                    tvPesananId.text = pesananId
                    tvPesananStatus.text = status
                    tvPesananOngkir.text = ongkir.withNumberingFormat()
                    tvPesananMetodePembayaran.text = metodePembayaran
                    tvPesananTanggal.text = jadwal.withTimestamptoDateFormat()
                    tvPesananJam.text = jadwal.withTimestamptoTimeFormat()

                }
            }
    }

    private fun setupDataMenu() {
        val menuPesananRef = db.collection("pesanan").document(pesananId!!).collection("menuPesanan")
        menuPesananRef.addSnapshotListener { menuPesananSnapshot, _ ->
            if (menuPesananSnapshot != null) {
                menuPesananList.clear()
                for (data in menuPesananSnapshot) {
                    val menuPesanan: Keranjang = data.toObject(Keranjang::class.java)
                    menuPesanan.id = data.id
                    menuPesananList.add(menuPesanan)
                }

                detailPesananAdapter.setItems(menuPesananList)
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvPesanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        detailPesananAdapter = DetailPesananAdapter(this, getString(R.string.vendor), "tidak perlu")
        recyclerView.adapter = detailPesananAdapter
        detailPesananAdapter.setItems(menuPesananList)
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

    private fun setUI() {
        tvVendorNama.text = getString(R.string.rangkuman_pesanan)
    }
}