@file:Suppress("DEPRECATION")

package com.valdoang.kateringconnect.view.user.pemesanan

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityPemesananBinding
import com.valdoang.kateringconnect.utils.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class PemesananActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {
    private lateinit var binding: ActivityPemesananBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var menuId: String? = null
    private lateinit var tvVendorName: TextView
    private lateinit var tvMenuName: TextView
    private lateinit var tvMenuDesc: TextView
    private lateinit var tvMenuPrice: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserCity: TextView
    private lateinit var tvUserAddress: TextView
    private lateinit var tvUserNoPhone: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var rgMetodePembayaran: RadioGroup
    private lateinit var tvTotalPembayaran: TextView
    private lateinit var etJumlah: EditText
    private lateinit var etCatatan: EditText
    private lateinit var progressBar: ProgressBar
    private val calendar = Calendar.getInstance()
    private var vendorId = ""
    private var menuNama = ""
    private var menuHarga = ""
    private var menuDesc = ""
    private var fotoUser = ""
    private var fotoVendor = ""
    private var alamatUser = ""
    private var alamatVendor = ""
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private lateinit var userAddress: List<Address>
    private lateinit var vendorAddress: List<Address>
    private lateinit var tvSubtotal: TextView
    private lateinit var tvOngkir: TextView
    private var jarak = 0f
    private var ongkir = 0L
    private var subtotal = 0L
    private var totalPembayaran = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth

        menuId = intent.getStringExtra(Cons.EXTRA_ID)

        tvVendorName = binding.tvVendorName
        tvMenuName = binding.tvMenuName
        tvMenuDesc = binding.tvMenuDesc
        tvMenuPrice = binding.tvMenuPrice
        tvUserName = binding.tvUserName
        tvUserCity = binding.tvCity
        tvUserAddress = binding.tvAddress
        tvUserNoPhone = binding.tvNoPhone
        tvDate = binding.tvDate
        tvTime = binding.tvTime
        rgMetodePembayaran = binding.rgMetodePembayaran
        tvTotalPembayaran = binding.tvTotalPembayaran
        etJumlah = binding.edJumlah
        etCatatan = binding.edCatatan
        progressBar = binding.progressBar
        tvSubtotal = binding.tvSubtotalValue
        tvOngkir = binding.tvOngkirValue

        setupPemesanan(menuId!!)
        datePicker()
        timePicker()
        pemesanan()
        setupAction()
    }

    private fun setupPemesanan(menuId: String) {
        val ref = db.collection("menu").document(menuId)
        ref.get().addOnSuccessListener { document ->
            if (document != null) {
                menuNama = document.data?.get("nama").toString()
                menuDesc = document.data?.get("keterangan").toString()
                menuHarga = document.data?.get("harga").toString()
                vendorId = document.data?.get("userId").toString()
                tvMenuName.text = menuNama
                tvMenuDesc.text = menuDesc
                tvMenuPrice.text = menuHarga.withNumberingFormat()

                db.collection("user").document(vendorId)
                    .get().addOnSuccessListener {
                        if (it != null) {
                            val namaVendor = it.data?.get("nama").toString()
                            fotoVendor = it.data?.get("foto").toString()
                            alamatVendor = it.data?.get("alamat").toString()
                            tvVendorName.text = namaVendor

                            val userId = firebaseAuth.currentUser!!.uid
                            val userRef = db.collection("user").document(userId)
                            userRef.get().addOnSuccessListener { document1 ->
                                if (document1 != null) {
                                    val nama = document1.data?.get("nama").toString()
                                    val kota = document1.data?.get("kota").toString()
                                    alamatUser = document1.data?.get("alamat").toString()
                                    val telepon = document1.data?.get("telepon").toString()
                                    fotoUser = document1.data?.get("foto").toString()
                                    tvUserName.text = nama
                                    tvUserCity.text = kota
                                    tvUserAddress.text = alamatUser
                                    tvUserNoPhone.text = telepon

                                    //Hitung Ongkos Kirim
                                    val coder = Geocoder(this)
                                    try {
                                        userAddress = coder.getFromLocationName(alamatUser,5)!!
                                        val userLocation = userAddress[0]
                                        val userLat = userLocation.latitude
                                        val userLon = userLocation.longitude

                                        vendorAddress = coder.getFromLocationName(alamatVendor,5)!!
                                        val vendorLocation = vendorAddress[0]
                                        val vendorLat = vendorLocation.latitude
                                        val vendorLon = vendorLocation.longitude

                                        val userPoint = Location("locationA")
                                        userPoint.latitude = userLat
                                        userPoint.longitude = userLon

                                        val vendorPoint = Location("locationB")
                                        vendorPoint.latitude = vendorLat
                                        vendorPoint.longitude = vendorLon

                                        jarak = userPoint.distanceTo(vendorPoint) / 1000

                                        ongkir = jarak.roundToLong() * 3000
                                        tvOngkir.text = ongkir.withNumberingFormat()

                                        etJumlah.textChangedListener { jumlah ->
                                            if (jumlah == ""){
                                                tvTotalPembayaran.text = ongkir.withNumberingFormat()
                                                tvSubtotal.text = ""
                                            } else if (jumlah.toLong() < 10) {
                                                etJumlah.error = getString(R.string.minimum_jumlah)
                                            } else {
                                                subtotal = jumlah.toLong() * menuHarga.toLong()
                                                tvSubtotal.text = subtotal.withNumberingFormat()

                                                totalPembayaran = ongkir + subtotal
                                                tvTotalPembayaran.text = totalPembayaran.withNumberingFormat()
                                            }
                                        }

                                    } catch (e: Exception) {
                                        Log.d(TAG, e.localizedMessage as String)
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun pemesanan() {
        var sMetodePembayaran = ""

        rgMetodePembayaran.setOnCheckedChangeListener { _, checkedId ->
            val rbMetodePembayaran: RadioButton = findViewById(checkedId)
            when (rbMetodePembayaran.text) {
                getString(R.string.tunai) -> sMetodePembayaran = getString(R.string.tunai)
                getString(R.string.bank_bca) -> sMetodePembayaran = getString(R.string.bank_bca)
                getString(R.string.bank_bri) -> sMetodePembayaran = getString(R.string.bank_bri)
            }
        }

        binding.btnPesan.setOnClickListener {
            val userId = firebaseAuth.currentUser!!.uid
            val userNama = tvUserName.text
            val userKota = tvUserCity.text
            val userAlamat = tvUserAddress.text
            val userTelepon = tvUserNoPhone.text

            val sStatus = getString(R.string.status_proses)
            val sVendorNama = tvVendorName.text
            val sJumlah = etJumlah.text.toString().trim()
            val sCatatan = etCatatan.text.toString().trim()
            val sDate = calendar.timeInMillis.toString()

            val pemesananMap = hashMapOf(
                "menuId" to menuId,
                "menuNama" to menuNama,
                "menuHarga" to menuHarga,
                "menuKeterangan" to menuDesc,
                "userId" to userId,
                "userNama" to userNama,
                "userKota" to userKota,
                "userAlamat" to userAlamat,
                "userTelepon" to userTelepon,
                "userFoto" to fotoUser,
                "vendorFoto" to fotoVendor,
                "vendorId" to vendorId,
                "vendorNama" to sVendorNama,
                "vendorAlamat" to alamatVendor,
                "status" to sStatus,
                "jumlah" to sJumlah,
                "catatan" to sCatatan,
                "jadwal" to sDate,
                "ongkir" to ongkir.toString(),
                "subtotal" to subtotal.toString(),
                "metodePembayaran" to sMetodePembayaran,
                "totalPembayaran" to totalPembayaran.toString()
            )

            when {
                sJumlah.isEmpty() -> {
                    etJumlah.error = getString(R.string.entry_jumlah)
                }
                sJumlah.toLong() < 10 -> {
                    etJumlah.error = getString(R.string.minimum_jumlah)
                }
                tvDate.text == getString(R.string.pilih_tanggal) -> {
                    Toast.makeText(this, R.string.entry_tanggal, Toast.LENGTH_SHORT).show()
                }
                tvTime.text == getString(R.string.pilih_waktu) -> {
                    Toast.makeText(this, R.string.entry_waktu, Toast.LENGTH_SHORT).show()
                }
                sMetodePembayaran.isEmpty() -> {
                    Toast.makeText(this, R.string.entry_metode_pembayaran, Toast.LENGTH_SHORT).show()
                }
                calendar.timeInMillis <= System.currentTimeMillis() -> {
                    Toast.makeText(this, R.string.minimum_date, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    progressBar.visibility = View.VISIBLE
                    db.collection("pesanan").document()
                        .set(pemesananMap).addOnSuccessListener {
                            val newFragment: DialogFragment = PemesananBerhasilFragment()
                            newFragment.show(supportFragmentManager, "TAG")
                        }
                        .addOnFailureListener {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, R.string.fail_pemesanan, Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun datePicker() {
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            tvDate.text = dateFormat.format(calendar.time).withDateFormat()
        }

        binding.ibDate.setOnClickListener {
            DatePickerDialog(
                this,
                datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun timePicker() {
        binding.ibTime.setOnClickListener{
            val dialogFragment = TimePickerFragment()
            dialogFragment.show(supportFragmentManager, "timePicker")
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        tvTime.text = timeFormat.format(calendar.time)
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}