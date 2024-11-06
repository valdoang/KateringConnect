package com.valdoang.kateringconnect.view.vendor.detailpesanan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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
import com.valdoang.kateringconnect.view.all.imageview.ImageViewActivity
import com.valdoang.kateringconnect.view.user.berinilai.BeriNilaiFragment

class DetailPesananActivity : AppCompatActivity() {
    //TODO: UNTUK KONFIRMASI PESANAN, BERIKAN OTOMATIS MENOLAK JIKA MELEBIHI JADWAL PESANAN
    private lateinit var binding: ActivityDetailPesananPemesananBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var pesananId: String? = null
    private var fromWhere: String? = null
    private var vendorId = ""
    private var userId = ""
    private var status = ""
    private var ongkir = 0L
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
    private lateinit var btnTerimaPesanan: Button
    private lateinit var btnTolakPesanan: Button
    private var menuPesananList: ArrayList<Keranjang> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var detailPesananPemesananAdapter: DetailPesananPemesananAdapter
    private lateinit var tvPesananTotalPembayaran: TextView
    private lateinit var tvPesananSubtotal: TextView
    private lateinit var tvPesananSubtotalValue: TextView
    private lateinit var tvAlasan: TextView
    private lateinit var tvAlasanValue: TextView
    private var total = 0L
    private lateinit var tvLihatBuktiPengiriman: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth

        pesananId = intent.getStringExtra(Cons.EXTRA_ID)
        fromWhere = intent.getStringExtra(Cons.EXTRA_NAMA)

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
        btnTerimaPesanan = binding.btnTerimaPesanan
        btnTolakPesanan = binding.btnTolakPesanan
        tvPesananTotalPembayaran = binding.tvTotalValue
        tvPesananSubtotal = binding.tvSubtotal
        tvPesananSubtotalValue = binding.tvSubtotalValue
        tvAlasan = binding.tvAlasan
        tvAlasanValue = binding.tvAlasanValue
        tvLihatBuktiPengiriman = binding.tvLihatBuktiPengiriman

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
                    userId = pesanan.data?.get("userId").toString()
                    vendorId = pesanan.data?.get("vendorId").toString()
                    status = pesanan.data?.get("status").toString()
                    val jadwal = pesanan.data?.get("jadwal").toString()
                    val metodePembayaran = pesanan.data?.get("metodePembayaran").toString()
                    ongkir = pesanan.data?.get("ongkir").toString().toLong()
                    val userNama = pesanan.data?.get("userNama").toString()
                    val userKota = pesanan.data?.get("userKota").toString()
                    val userAlamat = pesanan.data?.get("userAlamat").toString()
                    val userTelepon = pesanan.data?.get("userTelepon").toString()
                    val alasan = pesanan.data?.get("alasan").toString()
                    val fotoBuktiPengiriman = pesanan.data?.get("fotoBuktiPengiriman").toString()

                    tvUserNama.text = userNama
                    tvUserAlamat.text = getString(R.string.tv_address_city, userAlamat, userKota)
                    tvUserTelepon.text = userTelepon

                    tvPesananId.text = pesananId
                    tvPesananStatus.text = status
                    tvPesananOngkir.text = ongkir.withNumberingFormat()
                    tvPesananMetodePembayaran.text = metodePembayaran
                    tvPesananTanggal.text = jadwal.withTimestamptoDateFormat()
                    tvPesananJam.text = jadwal.withTimestamptoTimeFormat()
                    tvAlasanValue.text = alasan
                    tvLihatBuktiPengiriman.setOnClickListener {
                        val intent = Intent(this, ImageViewActivity::class.java)
                        intent.putExtra(Cons.EXTRA_NAMA, fotoBuktiPengiriman)
                        startActivity(intent)
                    }

                    when (status) {
                        getString(R.string.status_ditolak) -> {
                            tvPesananStatus.text = getString(R.string.menolak)
                            tvAlasan.text = getString(R.string.alasan_penolakan)
                            tvAlasan.visibility = View.VISIBLE
                            tvAlasanValue.visibility = View.VISIBLE
                        }
                        getString(R.string.status_batal) -> {
                            tvAlasan.text = getString(R.string.alasan_pembatalan)
                            tvAlasan.visibility = View.VISIBLE
                            tvAlasanValue.visibility = View.VISIBLE
                        }
                        getString(R.string.selesai) -> {
                            tvAlasan.visibility = View.GONE
                            tvAlasanValue.visibility = View.GONE
                            tvLihatBuktiPengiriman.visibility = View.VISIBLE
                        }
                        getString(R.string.status_butuh_konfirmasi_vendor) -> {
                            tvAlasan.visibility = View.GONE
                            tvAlasanValue.visibility = View.GONE
                            tvLihatBuktiPengiriman.visibility = View.GONE
                        }
                        getString(R.string.status_butuh_konfirmasi_pengguna) -> {
                            tvAlasan.visibility = View.GONE
                            tvAlasanValue.visibility = View.GONE
                            tvLihatBuktiPengiriman.visibility = View.VISIBLE
                        }
                    }

                    setupView()
                    setUI()
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
                    total += menuPesanan.subtotal!!.toLong()
                }

                total += ongkir

                detailPesananPemesananAdapter.setItems(menuPesananList)
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvPesanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        detailPesananPemesananAdapter = DetailPesananPemesananAdapter(this, getString(R.string.vendor), status, tvPesananTotalPembayaran, tvPesananSubtotal, tvPesananSubtotalValue, ongkir, vendorId, "tidak perlu")
        recyclerView.adapter = detailPesananPemesananAdapter
        detailPesananPemesananAdapter.setItems(menuPesananList)
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
        btnSelesaikan.setOnClickListener {
            val intent = Intent(this, SelesaikanPesananActivity::class.java)
            intent.putExtra(Cons.EXTRA_ID, pesananId)
            startActivity(intent)
            finish()
        }
        btnBatalkan.setOnClickListener {
            val args = Bundle()
            args.putString("id", pesananId)
            args.putString("userId", userId)
            args.putString("total", total.toString())
            val dialog: DialogFragment = BatalkanPesananFragment()
            dialog.arguments = args
            dialog.show(this.supportFragmentManager, "batalkanPesananDialog")

            it.visibility = View.GONE
            btnSelesaikan.visibility = View.GONE
        }
        btnTerimaPesanan.setOnClickListener {
            val updateStatus = mapOf(
                "status" to getString(R.string.status_proses)
            )
            db.collection("pesanan").document(pesananId!!).update(updateStatus)
            finish()
        }
        btnTolakPesanan.setOnClickListener {
            val args = Bundle()
            args.putString("id", pesananId)
            args.putString("userId", userId)
            args.putString("total", total.toString())
            val dialog: DialogFragment = TolakPesananFragment()
            dialog.arguments = args
            dialog.show(this.supportFragmentManager, "tolakPesananDialog")

            it.visibility = View.GONE
            btnTerimaPesanan.visibility = View.GONE
        }
    }

    private fun setUI() {
        tvVendorNama.text = getString(R.string.rangkuman_pesanan)
        when (fromWhere) {
            getString(R.string.from_vendor_beranda) -> {
                when (status) {
                    getString(R.string.status_butuh_konfirmasi_vendor) -> {
                        btnSelesaikan.visibility = View.GONE
                        btnBatalkan.visibility = View.GONE
                        btnTerimaPesanan.visibility = View.VISIBLE
                        btnTolakPesanan.visibility = View.VISIBLE
                    }
                    getString(R.string.status_proses) -> {
                        btnSelesaikan.visibility = View.VISIBLE
                        btnBatalkan.visibility = View.VISIBLE
                        btnTerimaPesanan.visibility = View.GONE
                        btnTolakPesanan.visibility = View.GONE
                    }
                }
            }
            getString(R.string.from_vendor_riwayat) -> {
                btnSelesaikan.visibility = View.GONE
                btnBatalkan.visibility = View.GONE
                binding.viewButton.visibility = View.GONE
            }
        }
    }
}