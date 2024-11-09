package com.valdoang.kateringconnect.view.user.detailpemesanan

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
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
import com.valdoang.kateringconnect.view.all.imageview.ImageViewActivity
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
    private lateinit var tvPesananPesananDibuat: TextView
    private lateinit var tvPesananStatus: TextView
    private lateinit var tvPesananTotalPembayaran: TextView
    private lateinit var tvPesananSubtotal: TextView
    private lateinit var tvPesananOngkir: TextView
    private lateinit var tvPesananMetodePembayaran: TextView
    private lateinit var tvPesananJadwalPengantaran: TextView
    private lateinit var btnBeriNilai: Button
    private lateinit var btnPesananTelahDiterima: Button
    private lateinit var tvTotalPembayaran: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvSubtotalValue: TextView
    private var menuPesananList: ArrayList<Keranjang> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var detailPesananPemesananAdapter: DetailPesananPemesananAdapter
    private lateinit var viewButton: View
    private lateinit var tvAlasan: TextView
    private lateinit var tvAlasanValue: TextView
    private var potongan = ""
    private lateinit var progressBar: ProgressBar
    private var saldoVendor = ""
    private var metodePembayaran = ""
    private var total = 0L
    private lateinit var tvLihatBuktiPengiriman: TextView
    private lateinit var tvConfirmAlert: TextView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        
        pesananId = intent.getStringExtra(Cons.EXTRA_ID)

        tvUserNama = binding.tvUserName
        tvUserAlamat = binding.tvAddress
        tvUserTelepon = binding.tvNoPhone
        tvVendorNama = binding.tvNamaVendor
        tvPesananId = binding.tvIdValue
        tvPesananPesananDibuat = binding.tvPesananDibuatValue
        tvPesananStatus = binding.tvStatusValue
        tvPesananTotalPembayaran = binding.tvTotalValue
        tvPesananSubtotal = binding.tvSubtotalValue
        tvPesananOngkir = binding.tvOngkirValue
        tvPesananMetodePembayaran = binding.tvPembayaranValue
        tvPesananJadwalPengantaran = binding.tvJadwalPengantaranValue
        btnBeriNilai = binding.btnBeriNilai
        btnPesananTelahDiterima = binding.btnPesananTelahDiterima
        viewButton = binding.viewButton
        tvTotalPembayaran = binding.tvTotalValue
        tvSubtotal = binding.tvSubtotal
        tvSubtotalValue = binding.tvSubtotalValue
        tvAlasan = binding.tvAlasan
        tvAlasanValue = binding.tvAlasanValue
        progressBar = binding.progressBar
        tvLihatBuktiPengiriman = binding.tvLihatBuktiPengiriman
        tvConfirmAlert = binding.confirmAlert

        setupView()
        setupData()
        setupDataMenu()
        setupAction()
        setupPotongan()
        hideUI()
        editUI()
    }

    private fun setupPotongan() {
        val adminRef = db.collection("user").document(Cons.ADMIN_ID)
        adminRef.addSnapshotListener { adminSnapshot, _ ->
            if (adminSnapshot != null) {
                potongan = adminSnapshot.data?.get("potongan").toString()
            }
        }
    }

    private fun setupData() {
        db.collection("pesanan").document(pesananId!!)
            .addSnapshotListener { pesanan,_ ->
                if (pesanan != null) {
                    vendorId = pesanan.data?.get("vendorId").toString()
                    val vendorNama = pesanan.data?.get("vendorNama").toString()
                    status = pesanan.data?.get("status").toString()
                    val jadwal = pesanan.data?.get("jadwal").toString()
                    val pesananDibuat = pesanan.data?.get("pesananDibuat").toString()
                    metodePembayaran = pesanan.data?.get("metodePembayaran").toString()
                    ongkir = pesanan.data?.get("ongkir").toString().toLong()
                    val userNama = pesanan.data?.get("userNama").toString()
                    val userKota = pesanan.data?.get("userKota").toString()
                    val userAlamat = pesanan.data?.get("userAlamat").toString()
                    val userTelepon = pesanan.data?.get("userTelepon").toString()
                    val nilai = pesanan.data?.get("nilai")
                    val alasan = pesanan.data?.get("alasan").toString()
                    val fotoBuktiPengiriman = pesanan.data?.get("fotoBuktiPengiriman").toString()

                    val confirmDate = jadwal.toLong() + 86400000L

                    if (status == getString(R.string.status_butuh_konfirmasi_pengguna)) {
                        tvConfirmAlert.text = getString(R.string.selesaikan_sebelum_tanggal, confirmDate.toString().withTimestamptoDateFormat(), confirmDate.toString().withTimestamptoTimeFormat())
                        viewButton.visibility = View.VISIBLE
                        btnPesananTelahDiterima.visibility = View.VISIBLE
                        tvLihatBuktiPengiriman.visibility = View.VISIBLE
                        tvConfirmAlert.visibility = View.VISIBLE

                    }
                    else if (status == getString(R.string.status_selesai) && nilai == null) {
                        viewButton.visibility = View.VISIBLE
                        btnBeriNilai.visibility = View.VISIBLE
                        btnPesananTelahDiterima.visibility = View.GONE
                        tvLihatBuktiPengiriman.visibility = View.VISIBLE
                    }
                    else if (status == getString(R.string.status_selesai) && nilai == true) {
                        viewButton.visibility = View.GONE
                        btnBeriNilai.visibility = View.GONE
                        tvLihatBuktiPengiriman.visibility = View.VISIBLE
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
                    tvPesananJadwalPengantaran.text = getString(R.string.tanggal_jam, jadwal.withTimestamptoDateFormat(), jadwal.withTimestamptoTimeFormat())
                    tvPesananPesananDibuat.text = getString(R.string.tanggal_jam, pesananDibuat.withTimestamptoDateFormat(), pesananDibuat.withTimestamptoTimeFormat())
                    tvAlasanValue.text = alasan
                    tvLihatBuktiPengiriman.setOnClickListener {
                        val intent = Intent(this, ImageViewActivity::class.java)
                        intent.putExtra(Cons.EXTRA_NAMA, fotoBuktiPengiriman)
                        startActivity(intent)
                    }

                    setupView()

                    val vendorRef = db.collection("user").document(vendorId)
                    vendorRef.addSnapshotListener { vendorSnapshot, _ ->
                        if (vendorSnapshot != null) {
                            saldoVendor = vendorSnapshot.data?.get("saldo").toString()
                            if (saldoVendor == "null") {
                                saldoVendor = "0"
                            }
                        }
                    }
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
                    total += menuPesanan.subtotal!!.toLong()
                    menuPesananList.add(menuPesanan)
                }

                total += ongkir

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

    private fun kcWalletPaymentAddMutasi() {
        val sDate = System.currentTimeMillis().toString()
        val sJenis = getString(R.string.kredit)
        val sKeterangan = getString(R.string.penjualan_katering)
        val sNominal = total*(100 - potongan.toLong())/100

        val mutasiMap = hashMapOf(
            "tanggal" to sDate,
            "jenis" to sJenis,
            "keterangan" to sKeterangan,
            "nominal" to sNominal.toString(),
        )

        progressBar.visibility = View.VISIBLE
        val vendorRef = db.collection("user").document(vendorId)
        val newMutasi = vendorRef.collection("mutasi").document()
        newMutasi.set(mutasiMap).addOnSuccessListener {
            progressBar.visibility = View.GONE
            val newSaldo = saldoVendor.toLong() + sNominal

            val saldoMap = mapOf(
                "saldo" to newSaldo.toString()
            )
            vendorRef.update(saldoMap)
        }

    }

    private fun tunaiPaymentAddMutasi() {
        val sDate = System.currentTimeMillis().toString()
        val sJenis = getString(R.string.debit)
        val sKeterangan = getString(R.string.penjualan_katering_tunai)
        val sNominal = total * potongan.toLong() / 100

        val mutasiMap = hashMapOf(
            "tanggal" to sDate,
            "jenis" to sJenis,
            "keterangan" to sKeterangan,
            "nominal" to sNominal.toString(),
        )

        progressBar.visibility = View.VISIBLE
        val vendorRef = db.collection("user").document(vendorId)
        val newMutasi = vendorRef.collection("mutasi").document()
        newMutasi.set(mutasiMap).addOnSuccessListener {
            progressBar.visibility = View.GONE
            val newSaldo = saldoVendor.toLong() - sNominal

            val saldoMap = mapOf(
                "saldo" to newSaldo.toString()
            )
            vendorRef.update(saldoMap)
        }

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
            when(metodePembayaran) {
                getString(R.string.kc_wallet) -> {
                    kcWalletPaymentAddMutasi()
                }
                getString(R.string.tunai) -> {
                    tunaiPaymentAddMutasi()
                }
            }

            it.visibility = View.GONE
            finish()
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