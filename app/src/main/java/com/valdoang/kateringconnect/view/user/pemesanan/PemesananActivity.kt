@file:Suppress("DEPRECATION")

package com.valdoang.kateringconnect.view.user.pemesanan

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityPemesananBinding
import com.valdoang.kateringconnect.utils.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import kotlin.math.roundToLong

class PemesananActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {
    private lateinit var binding: ActivityPemesananBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private val calendar = Calendar.getInstance()
    private var namaUser = ""
    private var alamatUser = ""
    private var kotaUser = ""
    private var nomorUser = ""
    private var fotoUser = ""
    private var namaVendor = ""
    private var alamatVendor = ""
    private var fotoVendor = ""
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private lateinit var userAddress: List<Address>
    private lateinit var vendorAddress: List<Address>
    private var jarak = 0f
    private var ongkir = 0L
    private var totalHarga = 0L
    private var userId = ""
    private var vendorId: String? = null
    private var kategoriId: String? = null
    private var menuId: String? = null
    private var namaMenu: String? = null
    private var deskMenu: String? = null
    private var jumlahPesanan: String? = null
    private var namaOpsi: ArrayList<String>? = null
    private var sNamaOpsi = ""
    private var catatan: String? = null
    private var subtotal: String? = null
    private var hargaPerPorsi: String? = null
    private lateinit var tvTanggal: TextView
    private lateinit var tvJam: TextView
    private lateinit var btnPesan: Button
    private lateinit var ivTanggalError: ImageView
    private lateinit var tvTanggalError: TextView
    private lateinit var progressBar: ProgressBar

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid

        vendorId = intent.getStringExtra(Cons.EXTRA_ID)
        kategoriId = intent.getStringExtra(Cons.EXTRA_SEC_ID)
        menuId = intent.getStringExtra(Cons.EXTRA_THIRD_ID)
        namaMenu = intent.getStringExtra(Cons.EXTRA_NAMA)
        deskMenu = intent.getStringExtra(Cons.EXTRA_DESC)
        jumlahPesanan = intent.getStringExtra(Cons.EXTRA_JUMLAH_PESANAN)
        namaOpsi = intent.getStringArrayListExtra(Cons.EXTRA_NAMA_OPSI)
        catatan = intent.getStringExtra(Cons.EXTRA_CATATAN)
        hargaPerPorsi = intent.getStringExtra(Cons.EXTRA_SUBTOTAL)
        subtotal = intent.getStringExtra(Cons.EXTRA_TOTAL)

        tvTanggal = binding.tvTanggal
        tvJam = binding.tvJam
        btnPesan = binding.btnPesan
        ivTanggalError = binding.ivTanggalError
        tvTanggalError = binding.tvTanggalError
        progressBar = binding.progressBar

        setupPemesanan()
        setupRangkumanPesanan()
        datePicker()
        pemesanan()
        setupAction()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupRangkumanPesanan() {
        binding.tvJumlahPesanan.text = getString(R.string.tv_jumlah_pesanan, jumlahPesanan)
        binding.tvNamaMenu.text = namaMenu
        sNamaOpsi = namaOpsi?.stream()?.collect(
            Collectors.joining(", ")
        )!!

        if (namaOpsi.isNullOrEmpty()) {
            binding.tvNamaOpsi.visibility = View.GONE
        } else {
            binding.tvNamaOpsi.text = sNamaOpsi
        }

        if (catatan == "") {
            binding.tvCatatan.visibility = View.GONE
        } else {
            binding.tvCatatan.text = catatan
        }

        binding.tvSubtotal.text = subtotal?.withNumberingFormat()
    }

    private fun setupPemesanan() {
        val vendorRef = db.collection("user").document(vendorId!!)
        vendorRef.get().addOnSuccessListener {vendorSnapshot ->
            if (vendorSnapshot != null) {
                namaVendor = vendorSnapshot.data?.get("nama").toString()
                alamatVendor = vendorSnapshot.data?.get("alamat").toString()
                fotoVendor = vendorSnapshot.data?.get("foto").toString()
                binding.tvVendorName.text = namaVendor

                val userRef = db.collection("user").document(userId)
                userRef.get().addOnSuccessListener { userSnapshot ->
                    if (userSnapshot != null) {
                        namaUser = userSnapshot.data?.get("nama").toString()
                        kotaUser = userSnapshot.data?.get("kota").toString()
                        alamatUser = userSnapshot.data?.get("alamat").toString()
                        nomorUser = userSnapshot.data?.get("telepon").toString()
                        fotoUser = userSnapshot.data?.get("foto").toString()
                        
                        binding.tvUserName.text = namaUser
                        binding.tvAddress.text = getString(R.string.tv_address_city, alamatUser, kotaUser)
                        binding.tvNoPhone.text = nomorUser

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

                            if (jarak > 60) {
                                binding.tvJarakError.visibility = View.VISIBLE
                            } else {
                                binding.tvJarakError.visibility = View.GONE
                            }

                            ongkir = jarak.roundToLong() * 3000
                            binding.tvOngkirValue.text = ongkir.withNumberingFormat()

                            totalHarga = ongkir + subtotal!!.toLong()
                            binding.totalHarga.text = totalHarga.withNumberingFormat()

                        } catch (e: Exception) {
                            Log.d(TAG, e.localizedMessage as String)
                        }

                    }
                }
            }
        }
    }

    private fun pemesanan() {
        btnPesan.setOnClickListener {
            val sStatus = getString(R.string.status_proses)
            val sDate = calendar.timeInMillis.toString()
            val sMetodePembayaran = getString(R.string.tunai)

            val pemesananMap = hashMapOf(
                "kategoriId" to kategoriId,
                "menuId" to menuId,
                "menuNama" to namaMenu,
                "namaOpsi" to sNamaOpsi,
                "menuKeterangan" to deskMenu,
                "userId" to userId,
                "userNama" to namaUser,
                "userKota" to kotaUser,
                "userAlamat" to alamatUser,
                "userTelepon" to nomorUser,
                "userFoto" to fotoUser,
                "vendorFoto" to fotoVendor,
                "vendorId" to vendorId,
                "vendorNama" to namaVendor,
                "vendorAlamat" to alamatVendor,
                "status" to sStatus,
                "jumlah" to jumlahPesanan,
                "catatan" to catatan,
                "jadwal" to sDate,
                "ongkir" to ongkir.toString(),
                "subtotal" to subtotal,
                "hargaPerPorsi" to hargaPerPorsi,
                "metodePembayaran" to sMetodePembayaran,
                "totalHarga" to totalHarga.toString()
            )

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

    private fun datePicker() {
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val today = dateFormat.format(System.currentTimeMillis()).withDateFormat()
            val datePicked = dateFormat.format(calendar.time).withDateFormat()
            tvTanggal.text = datePicked
            tvTanggal.setTextColor(resources.getColor(R.color.black))
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                ivTanggalError.visibility = View.VISIBLE
                tvTanggalError.visibility = View.VISIBLE
                btnPesan.isEnabled = false
            } else if(today == datePicked) {
                ivTanggalError.visibility = View.VISIBLE
                tvTanggalError.visibility = View.VISIBLE
                btnPesan.isEnabled = false
            } else {
                ivTanggalError.visibility = View.GONE
                tvTanggalError.visibility = View.GONE
                if (tvJam.currentTextColor == resources.getColor(R.color.black)) {
                    btnPesan.isEnabled = true
                }
                timePicker()
            }
        }

        binding.clPilihTanggal.setOnClickListener {
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
        binding.clPilihJam.setOnClickListener{
            val dialogFragment = TimePickerFragment()
            dialogFragment.show(supportFragmentManager, "timePicker")
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        tvJam.text = timeFormat.format(calendar.time)
        tvJam.setTextColor(resources.getColor(R.color.black))
        val today = dateFormat.format(System.currentTimeMillis()).withDateFormat()
        val datePicked = dateFormat.format(calendar.time).withDateFormat()
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            ivTanggalError.visibility = View.VISIBLE
            tvTanggalError.visibility = View.VISIBLE
            btnPesan.isEnabled = false
        } else if(today == datePicked) {
            ivTanggalError.visibility = View.VISIBLE
            tvTanggalError.visibility = View.VISIBLE
            btnPesan.isEnabled = false
        } else {
            ivTanggalError.visibility = View.GONE
            tvTanggalError.visibility = View.GONE
            btnPesan.isEnabled = true
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}