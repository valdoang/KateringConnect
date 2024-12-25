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
import com.valdoang.kateringconnect.view.all.imageview.ImageViewActivity

class DetailPesananActivity : AppCompatActivity() {
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
    private lateinit var tvPesananJadwalPengantaran: TextView
    private lateinit var tvPesananPesananDibuat: TextView
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
    private lateinit var tvConfirmAlert: TextView
    private var metodePembayaran: String? = null
    private var pesananDibuat = ""
    private var confirmDate = 0L
    private var potongan = ""
    private var totalPemasukanAdmin = ""
    private var saldoVendor = ""
    private var jadwal = ""

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
        tvPesananJadwalPengantaran = binding.tvJadwalPengantaranValue
        tvPesananPesananDibuat = binding.tvPesananDibuatValue
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
        tvConfirmAlert = binding.confirmAlert

        setupAction()
        setUI()
        setupView()
        setupData()
        setupDataMenu()
        setupPotongan()
    }

    private fun setupData() {
        db.collection("pesanan").document(pesananId!!)
            .addSnapshotListener { pesanan, _ ->
                if (pesanan != null) {
                    val pesananId = pesanan.id
                    userId = pesanan.data?.get("userId").toString()
                    vendorId = pesanan.data?.get("vendorId").toString()
                    status = pesanan.data?.get("status").toString()
                    jadwal = pesanan.data?.get("jadwal").toString()
                    pesananDibuat = pesanan.data?.get("pesananDibuat").toString()
                    metodePembayaran = pesanan.data?.get("metodePembayaran").toString()
                    ongkir = pesanan.data?.get("ongkir").toString().toLong()
                    val userNama = pesanan.data?.get("userNama").toString()
                    val userKota = pesanan.data?.get("userKota").toString()
                    val userAlamat = pesanan.data?.get("userAlamat").toString()
                    val userTelepon = pesanan.data?.get("userTelepon").toString()
                    val alasan = pesanan.data?.get("alasan").toString()
                    val fotoBuktiPengiriman = pesanan.data?.get("fotoBuktiPengiriman").toString()

                    confirmDate = pesananDibuat.toLong() + 86400000L

                    tvUserNama.text = userNama
                    tvUserAlamat.text = getString(R.string.tv_address_city, userAlamat, userKota)
                    tvUserTelepon.text = userTelepon

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

                    when (status) {
                        getString(R.string.status_ditolak) -> {
                            tvPesananStatus.text = getString(R.string.menolak)
                            tvAlasan.text = getString(R.string.alasan_penolakan)
                            tvAlasan.visibility = View.VISIBLE
                            tvAlasanValue.visibility = View.VISIBLE
                            btnTerimaPesanan.visibility = View.GONE
                            btnTolakPesanan.visibility = View.GONE
                            tvConfirmAlert.visibility = View.GONE
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
                            tvConfirmAlert.text = getString(R.string.terima_sebelum_tanggal, confirmDate.toString().withTimestamptoDateFormat(), confirmDate.toString().withTimestamptoTimeFormat())
                            tvAlasan.visibility = View.GONE
                            tvAlasanValue.visibility = View.GONE
                            tvLihatBuktiPengiriman.visibility = View.GONE
                            lateConfirm()
                        }
                        getString(R.string.status_butuh_konfirmasi_pengguna) -> {
                            tvAlasan.visibility = View.GONE
                            tvAlasanValue.visibility = View.GONE
                            tvLihatBuktiPengiriman.visibility = View.VISIBLE
                            userLateConfirm()
                        }
                    }

                    setupView()
                    setUI()

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
                    menuPesananList.add(menuPesanan)
                    total += menuPesanan.subtotal!!.toLong()
                }

                total += ongkir

                detailPesananPemesananAdapter.setItems(menuPesananList)
            }
        }
    }

    private fun lateConfirm() {
        val currentDate = System.currentTimeMillis()

        if (currentDate >= confirmDate) {
            val sAlasan = getString(R.string.ditolak_karena_batas_waktu)

            val alasanMap = mapOf(
                "status" to getString(R.string.status_ditolak),
                "alasan" to sAlasan
            )
            db.collection("pesanan").document(pesananId!!).update(alasanMap)
                .addOnSuccessListener {
                    if (metodePembayaran == getString(R.string.kc_wallet)) {
                        val sDate = currentDate.toString()
                        val sJenis = getString(R.string.kredit)
                        val sKeterangan = getString(R.string.pengembalian_dana)

                        val mutasiMap = hashMapOf(
                            "tanggal" to sDate,
                            "jenis" to sJenis,
                            "keterangan" to sKeterangan,
                            "nominal" to total.toString(),
                        )

                        val userRef = db.collection("user").document(userId)
                        userRef.get().addOnSuccessListener { userSnapshot ->
                            if (userSnapshot != null) {
                                var saldoUser = userSnapshot.data?.get("saldo").toString()
                                if (saldoUser == "null") {
                                    saldoUser = "0"
                                }

                                val newMutasi = userRef.collection("mutasi").document()
                                newMutasi.set(mutasiMap).addOnSuccessListener {
                                    val newSaldo = saldoUser.toLong() + total

                                    val saldoMap = mapOf(
                                        "saldo" to newSaldo.toString()
                                    )
                                    userRef.update(saldoMap)
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun userLateConfirm() {
        val currentDate = System.currentTimeMillis()
        val userConfirmDate = jadwal.toLong() + 86400000L
        if (currentDate >= userConfirmDate) {
            val updateStatus = mapOf(
                "status" to getString(R.string.status_selesai)
            )
            db.collection("pesanan").document(pesananId!!).update(updateStatus)
                .addOnSuccessListener {
                    when(metodePembayaran) {
                        getString(R.string.kc_wallet) -> {
                            kcWalletPaymentAddMutasi()
                        }
                        getString(R.string.tunai) -> {
                            tunaiPaymentAddMutasi()
                        }
                    }
                }
        }
    }

    private fun setupPotongan() {
        val adminRef = db.collection("user").document(Cons.ADMIN_ID)
        adminRef.addSnapshotListener { adminSnapshot, _ ->
            if (adminSnapshot != null) {
                potongan = adminSnapshot.data?.get("potongan").toString()
                totalPemasukanAdmin = adminSnapshot.data?.get("totalPemasukan").toString()
            }
        }
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

        val vendorRef = db.collection("user").document(vendorId)
        val newMutasi = vendorRef.collection("mutasi").document()
        newMutasi.set(mutasiMap).addOnSuccessListener {
            val newSaldo = saldoVendor.toLong() + sNominal

            val saldoMap = mapOf(
                "saldo" to newSaldo.toString()
            )
            vendorRef.update(saldoMap)
            adminAddPemasukan()
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

        val vendorRef = db.collection("user").document(vendorId)
        val newMutasi = vendorRef.collection("mutasi").document()
        newMutasi.set(mutasiMap).addOnSuccessListener {
            val newSaldo = saldoVendor.toLong() - sNominal

            val saldoMap = mapOf(
                "saldo" to newSaldo.toString()
            )
            vendorRef.update(saldoMap)
            adminAddPemasukan()
        }

    }

    private fun adminAddPemasukan() {
        val sDate = System.currentTimeMillis().toString()
        val sKeterangan = getString(R.string.potongan_hasil_vendor)
        val sNominal = total * potongan.toLong() / 100

        val mutasiMap = hashMapOf(
            "tanggal" to sDate,
            "keterangan" to sKeterangan,
            "nominal" to sNominal.toString(),
        )

        val adminRef = db.collection("user").document(Cons.ADMIN_ID)
        val newPemasukanRef = adminRef.collection("pemasukan").document()
        newPemasukanRef.set(mutasiMap).addOnSuccessListener {
            val newTotalPemasukan = totalPemasukanAdmin.toLong() + sNominal

            val totalPemasukanMap = mapOf(
                "totalPemasukan" to newTotalPemasukan.toString()
            )
            adminRef.update(totalPemasukanMap)
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
            when(metodePembayaran) {
                getString(R.string.kc_wallet) -> {
                    val intent = Intent(this, SelesaikanPesananActivity::class.java)
                    intent.putExtra(Cons.EXTRA_ID, pesananId)
                    startActivity(intent)
                }
                getString(R.string.tunai) -> {
                    val intent = Intent(this, TagihTunaiActivity::class.java)
                    intent.putExtra(Cons.EXTRA_ID, pesananId)
                    intent.putExtra(Cons.EXTRA_SEC_ID, total.toString())
                    startActivity(intent)
                }
            }
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
                        tvConfirmAlert.visibility = View.VISIBLE
                        btnTerimaPesanan.visibility = View.VISIBLE
                        btnTolakPesanan.visibility = View.VISIBLE
                    }
                    getString(R.string.status_proses) -> {
                        btnSelesaikan.visibility = View.VISIBLE
                        btnBatalkan.visibility = View.VISIBLE
                        tvConfirmAlert.visibility = View.GONE
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