package com.valdoang.kateringconnect.view.vendor.detailpesanan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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
import com.valdoang.kateringconnect.view.user.berinilai.BeriNilaiFragment

class DetailPesananActivity : AppCompatActivity() {
    //TODO: UNTUK KONFIRMASI PESANAN, BERIKAN OTOMATIS MENOLAK JIKA MELEBIHI JADWAL PESANAN
    private lateinit var binding: ActivityDetailPesananPemesananBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var pesananId: String? = null
    private var fromWhere: String? = null
    private var vendorId = ""
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
                    status = pesanan.data?.get("status").toString()
                    val jadwal = pesanan.data?.get("jadwal").toString()
                    val metodePembayaran = pesanan.data?.get("metodePembayaran").toString()
                    ongkir = pesanan.data?.get("ongkir").toString().toLong()
                    val userNama = pesanan.data?.get("userNama").toString()
                    val userKota = pesanan.data?.get("userKota").toString()
                    val userAlamat = pesanan.data?.get("userAlamat").toString()
                    val userTelepon = pesanan.data?.get("userTelepon").toString()

                    tvUserNama.text = userNama
                    tvUserAlamat.text = getString(R.string.tv_address_city, userAlamat, userKota)
                    tvUserTelepon.text = userTelepon

                    tvPesananId.text = pesananId
                    tvPesananStatus.text = status
                    tvPesananOngkir.text = ongkir.withNumberingFormat()
                    tvPesananMetodePembayaran.text = metodePembayaran
                    tvPesananTanggal.text = jadwal.withTimestamptoDateFormat()
                    tvPesananJam.text = jadwal.withTimestamptoTimeFormat()

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
                }

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
            it.visibility = View.GONE
            btnBatalkan.visibility = View.GONE
            finish()
        }
        btnBatalkan.setOnClickListener {
            //TODO: TAMBAHKAN BATALKAN ACTIVITY ATAU DIALOG
            /*val updateStatus = mapOf(
                "status" to getString(R.string.status_batal)
            )
            db.collection("pesanan").document(pesananId!!).update(updateStatus)

            btnSelesaikan.visibility = View.GONE
            it.visibility = View.GONE
            finish()*/
        }
        btnTerimaPesanan.setOnClickListener {
            val updateStatus = mapOf(
                "status" to getString(R.string.status_proses)
            )
            db.collection("pesanan").document(pesananId!!).update(updateStatus)

            it.visibility = View.GONE
            btnTolakPesanan.visibility = View.GONE
            finish()
        }
        btnTolakPesanan.setOnClickListener {
            val args = Bundle()
            args.putString("id", pesananId)
            val dialog: DialogFragment = BatalkanPesananFragment()
            dialog.arguments = args
            dialog.show(this.supportFragmentManager, "batalkanPesananDialog")
            /*val updateStatus = mapOf(
                "status" to getString(R.string.status_ditolak)
            )
            db.collection("pesanan").document(pesananId!!).update(updateStatus)

            btnTerimaPesanan.visibility = View.GONE
            it.visibility = View.GONE*/
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