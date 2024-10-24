package com.valdoang.kateringconnect.view.user.detailpemesanan

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.DetailPesananPemesananAdapter
import com.valdoang.kateringconnect.databinding.ActivityDetailPesananPemesananBinding
import com.valdoang.kateringconnect.model.Keranjang
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.utils.withTimestamptoDateFormat
import com.valdoang.kateringconnect.utils.withTimestamptoTimeFormat
import com.valdoang.kateringconnect.view.user.berinilai.BeriNilaiFragment

class DetailPemesananActivity : AppCompatActivity() {
    //TODO: UNTUK KONFIRMASI TELAH MENERIMA PESANAN, BERIKAN OTOMATIS TELAH MENERIMA JIKA MELEBIHI H+1
    private lateinit var binding: ActivityDetailPesananPemesananBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var pesananId: String? = null
    private var vendorId = ""
    private var status = ""
    private var ongkir = 0L
    private lateinit var tvUserNama: TextView
    private lateinit var tvUserAlamat: TextView
    private lateinit var tvUserTelepon: TextView
    private lateinit var tvVendorNama: TextView
    private lateinit var tvPesananId: TextView
    private lateinit var tvPesananStatus: TextView
    private lateinit var tvPesananTotalPembayaran: TextView
    private lateinit var tvPesananSubtotal: TextView
    private lateinit var tvPesananOngkir: TextView
    private lateinit var tvPesananMetodePembayaran: TextView
    private lateinit var tvPesananTanggal: TextView
    private lateinit var tvPesananJam: TextView
    private lateinit var btnBeriNilai: Button
    private lateinit var btnPesananTelahDiterima: Button
    private lateinit var btnPesananBelumDiterima: Button
    private lateinit var tvTotalPembayaran: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvSubtotalValue: TextView
    private var menuPesananList: ArrayList<Keranjang> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var detailPesananPemesananAdapter: DetailPesananPemesananAdapter
    private lateinit var viewButton: View
    private lateinit var tvAlasan: TextView
    private lateinit var tvAlasanValue: TextView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth =Firebase.auth
        
        pesananId = intent.getStringExtra(Cons.EXTRA_ID)

        tvUserNama = binding.tvUserName
        tvUserAlamat = binding.tvAddress
        tvUserTelepon = binding.tvNoPhone
        tvVendorNama = binding.tvNamaVendor
        tvPesananId = binding.tvIdValue
        tvPesananStatus = binding.tvStatusValue
        tvPesananTotalPembayaran = binding.tvTotalValue
        tvPesananSubtotal = binding.tvSubtotalValue
        tvPesananOngkir = binding.tvOngkirValue
        tvPesananMetodePembayaran = binding.tvPembayaranValue
        tvPesananTanggal = binding.tvTanggalValue
        tvPesananJam = binding.tvJamValue
        btnBeriNilai = binding.btnBeriNilai
        btnPesananTelahDiterima = binding.btnPesananTelahDiterima
        btnPesananBelumDiterima = binding.btnPesananBelumDiterima
        viewButton = binding.viewButton
        tvTotalPembayaran = binding.tvTotalValue
        tvSubtotal = binding.tvSubtotal
        tvSubtotalValue = binding.tvSubtotalValue
        tvAlasan = binding.tvAlasan
        tvAlasanValue = binding.tvAlasanValue

        setupView()
        setupData()
        setupDataMenu()
        setupAction()
        hideUI()
        editUI()
    }

    private fun setupData() {
        db.collection("pesanan").document(pesananId!!)
            .addSnapshotListener { pesanan,_ ->
                if (pesanan != null) {
                    vendorId = pesanan.data?.get("vendorId").toString()
                    val vendorNama = pesanan.data?.get("vendorNama").toString()
                    status = pesanan.data?.get("status").toString()
                    val jadwal = pesanan.data?.get("jadwal").toString()
                    val metodePembayaran = pesanan.data?.get("metodePembayaran").toString()
                    ongkir = pesanan.data?.get("ongkir").toString().toLong()
                    val userNama = pesanan.data?.get("userNama").toString()
                    val userKota = pesanan.data?.get("userKota").toString()
                    val userAlamat = pesanan.data?.get("userAlamat").toString()
                    val userTelepon = pesanan.data?.get("userTelepon").toString()
                    val nilai = pesanan.data?.get("nilai")
                    val alasan = pesanan.data?.get("alasan").toString()

                    if (status == getString(R.string.status_butuh_konfirmasi_pengguna)) {
                        viewButton.visibility = View.VISIBLE
                        btnPesananTelahDiterima.visibility = View.VISIBLE
                        btnPesananBelumDiterima.visibility = View.VISIBLE
                    }
                    else if (status == getString(R.string.status_selesai) && nilai == null) {
                        viewButton.visibility = View.VISIBLE
                        btnBeriNilai.visibility = View.VISIBLE
                        btnPesananTelahDiterima.visibility = View.GONE
                        btnPesananBelumDiterima.visibility = View.GONE
                    }
                    else if (status == getString(R.string.status_selesai) && nilai == true) {
                        viewButton.visibility = View.GONE
                        btnBeriNilai.visibility = View.GONE
                    }
                    else if (status == getString(R.string.status_batal) ) {
                        viewButton.visibility = View.GONE
                        tvAlasan.text = getString(R.string.alasan_pembatalan)
                        tvAlasan.visibility = View.VISIBLE
                        tvAlasanValue.visibility = View.VISIBLE
                    }
                    else if (status == getString(R.string.status_ditolak) ) {
                        viewButton.visibility = View.GONE
                        tvAlasan.text = getString(R.string.alasan_penolakan)
                        tvAlasan.visibility = View.VISIBLE
                        tvAlasanValue.visibility = View.VISIBLE
                    }
                    else if (status == getString(R.string.status_proses) ) {
                        viewButton.visibility = View.GONE
                    }

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
                    tvAlasanValue.text = alasan

                    setupView()
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

                detailPesananPemesananAdapter.setItems(menuPesananList)
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvPesanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        detailPesananPemesananAdapter = DetailPesananPemesananAdapter(this, getString(R.string.pembeli), status, tvTotalPembayaran, tvSubtotal, tvSubtotalValue, ongkir, vendorId, pesananId!!)
        recyclerView.adapter = detailPesananPemesananAdapter
        detailPesananPemesananAdapter.setItems(menuPesananList)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
        btnBeriNilai.setOnClickListener {
            val args = Bundle()
            args.putString("id", pesananId)
            val dialog: DialogFragment = BeriNilaiFragment()
            dialog.arguments = args
            dialog.show(this.supportFragmentManager, "beriNilaiDialog")
        }
        btnPesananTelahDiterima.setOnClickListener {
            val updateStatus = mapOf(
                "status" to getString(R.string.status_selesai)
            )
            db.collection("pesanan").document(pesananId!!).update(updateStatus)

            it.visibility = View.GONE
            finish()
        }
        btnPesananBelumDiterima.setOnClickListener {
            //TODO: TAMBAHKAN COMPLAIN KE ADMIN
        }
    }

    private fun hideUI() {
        binding.btnSelesaikan.visibility = View.GONE
        binding.btnBatalkan.visibility = View.GONE
    }

    private fun editUI() {
        binding.titlePesanan.text = resources.getString(R.string.rincian_pesananmu)
        binding.tvRincianPesanan.text = resources.getString(R.string.rincian_pesananmu)
    }
}