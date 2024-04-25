package com.valdoang.kateringconnect.view.user

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
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
import com.valdoang.kateringconnect.view.user.main.UserMainActivity
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
                val nama = document.data?.get("nama").toString()
                val desc = document.data?.get("keterangan").toString()
                val price = document.data?.get("harga").toString()
                vendorId = document.data?.get("userId").toString()
                tvMenuName.text = nama
                tvMenuDesc.text = desc
                tvMenuPrice.text = price.withNumberingFormat()

                db.collection("user").document(vendorId)
                    .get().addOnSuccessListener {
                        if (it != null) {
                            val namaVendor = it.data?.get("nama").toString()
                            tvVendorName.text = namaVendor
                        }
                    }

                etJumlah.textChangedListener {
                    if (it == ""){
                        tvTotalPembayaran.text = ""
                    } else if (it.toLong() < 10) {
                        etJumlah.error = getString(R.string.minimum_jumlah)
                    } else {
                        tvTotalPembayaran.text = (it.toLong() * price.toLong()).withNumberingFormat()
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
            val sJumlah = etJumlah.text.toString().trim()
            val sCatatan = etCatatan.text.toString().trim()
            val sDate = calendar.timeInMillis
            val sTotalPembayaran = tvTotalPembayaran.text.toString().trim()

            val pemesananMap = hashMapOf(
                "menuId" to menuId,
                "userId" to userId,
                "vendorId" to vendorId,
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
                else -> {
                    progressBar.visibility = View.VISIBLE
                    db.collection("pemesanan").document()
                        .set(pemesananMap).addOnSuccessListener {
                            //PIKIRIN LAGI NANTI GIMANA ENAKNYA!!
                            Toast.makeText(this, "Pesananan Diproses!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, UserMainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
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