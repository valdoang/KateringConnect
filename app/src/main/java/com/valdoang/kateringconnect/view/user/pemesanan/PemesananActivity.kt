package com.valdoang.kateringconnect.view.user.pemesanan

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityPemesananBinding
import com.valdoang.kateringconnect.utils.TimePickerFragment
import com.valdoang.kateringconnect.utils.textChangedListener
import com.valdoang.kateringconnect.utils.withDateFormat
import com.valdoang.kateringconnect.utils.withNumberingFormat
import java.text.SimpleDateFormat
import java.util.*

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
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth

        menuId = intent.getStringExtra(EXTRA_ID)

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

        setupPemesanan(menuId!!)
        setupUser()
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

                            tvVendorName.text = namaVendor
                        }
                    }

                etJumlah.textChangedListener {
                    if (it == ""){
                        tvTotalPembayaran.text = ""
                    } else if (it.toLong() < 10) {
                        etJumlah.error = getString(R.string.minimum_jumlah)
                    } else {
                        tvTotalPembayaran.text = (it.toLong() * menuHarga.toLong()).withNumberingFormat()
                    }
                }
            }
        }
    }

    private fun setupUser() {
        val userId = firebaseAuth.currentUser!!.uid
        val ref = db.collection("user").document(userId)
        ref.get().addOnSuccessListener { document ->
            if (document != null) {
                val nama = document.data?.get("nama").toString()
                val kota = document.data?.get("kota").toString()
                val alamat = document.data?.get("alamat").toString()
                val telepon = document.data?.get("telepon").toString()
                fotoUser = document.data?.get("foto").toString()
                tvUserName.text = nama
                tvUserCity.text = kota
                tvUserAddress.text = alamat
                tvUserNoPhone.text = telepon
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
            val sTotalPembayaran = tvTotalPembayaran.text.toString().trim()

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
                "status" to sStatus,
                "jumlah" to sJumlah,
                "catatan" to sCatatan,
                "jadwal" to sDate,
                "metodePembayaran" to sMetodePembayaran,
                "totalPembayaran" to sTotalPembayaran
            )

            when {
                sJumlah.isEmpty() -> {
                    etJumlah.error = getString(R.string.entry_jumlah)
                }
                sJumlah.toLong() < 10 -> {
                    etJumlah.error = getString(R.string.minimum_jumlah)
                }
                sCatatan.isEmpty() -> {
                    etCatatan.error = getString(R.string.entry_catatan)
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
            onBackPressed()
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}