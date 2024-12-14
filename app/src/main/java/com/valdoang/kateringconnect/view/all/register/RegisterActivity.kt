package com.valdoang.kateringconnect.view.all.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityRegisterBinding
import com.valdoang.kateringconnect.utils.getImageUri
import com.valdoang.kateringconnect.view.all.login.LoginActivity
import java.util.*

class RegisterActivity : AppCompatActivity(), PotonganAlertFragment.GetAnswer {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var rgJenisAkun: RadioGroup
    private lateinit var etName: EditText
    private lateinit var acCity: AutoCompleteTextView
    private lateinit var etNoPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var progressBar: ProgressBar
    private var db = Firebase.firestore
    private var ktpImageUri: Uri? = null
    private var selfieKtpImageUri: Uri? = null
    private lateinit var ivKtp: ImageView
    private lateinit var ivSelfieKtp: ImageView
    private lateinit var cvKtp: CardView
    private lateinit var cvSelfieKtp: CardView
    private var storageRef = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth

        etEmail = binding.edRegisterEmail
        etPassword = binding.edRegisterPassword
        btnRegister = binding.registerButton
        rgJenisAkun = binding.rgJenisAkun
        etName = binding.edRegisterName
        acCity = binding.acRegisterCity
        etNoPhone = binding.edRegisterNoPhone
        etAddress = binding.edRegisterAddress
        progressBar = binding.progressBar
        ivKtp = binding.ivKtp
        ivSelfieKtp = binding.ivSelfieKtp
        cvKtp = binding.cvKtp
        cvSelfieKtp = binding.cvSelfieKtp

        setupAcCity()
        setupAction()
        register()
    }

    private fun setupAcCity() {
        val cities = resources.getStringArray(R.array.Cities)
        val dropdownAdapter = ArrayAdapter(this, R.layout.dropdown_item, cities)
        acCity.setAdapter(dropdownAdapter)
    }

    private fun setupAction() {
        binding.tvToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        cvKtp.setOnClickListener {
            ktpImageUri = getImageUri(this)
            launcherIntentCamera.launch(ktpImageUri)
        }
        cvSelfieKtp.setOnClickListener {
            selfieKtpImageUri = getImageUri(this)
            launcherIntentCamera.launch(selfieKtpImageUri)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        ktpImageUri?.let {
            ivKtp.setImageURI(it)
        }
        selfieKtpImageUri?.let {
            ivSelfieKtp.setImageURI(it)
        }
    }

    private fun register() {
        var sJenisAkun = ""
        rgJenisAkun.setOnCheckedChangeListener { _, checkedId ->
            val rbJenisAkun: RadioButton = findViewById(checkedId)
            when (rbJenisAkun.text) {
                getString(R.string.akun_pembeli) -> sJenisAkun = getString(R.string.pembeli)
                getString(R.string.akun_vendor) -> sJenisAkun = getString(R.string.vendor)
            }
        }

        btnRegister.setOnClickListener {
            val sName = etName.text.toString().trim()
            val sNoPhone = etNoPhone.text.toString().trim()
            val sCity = acCity.text.toString().trim()
            val sAddress = etAddress.text.toString().trim()
            val sEmail = etEmail.text.toString().trim()
            val sPassword = etPassword.text.toString().trim()
            val statusPendaftaran = getString(R.string.verifikasi)

            val userMap = hashMapOf(
                "jenis" to sJenisAkun,
                "nama" to sName,
                "kota" to sCity,
                "alamat" to sAddress,
                "telepon" to sNoPhone,
                "email" to sEmail,
                "statusPendaftaran" to statusPendaftaran
            )

            when {
                sJenisAkun.isEmpty() -> {
                    Toast.makeText(this, R.string.can_not_blank, Toast.LENGTH_SHORT).show()
                }
                sEmail.isEmpty() -> {
                    etEmail.error = getString(R.string.entry_email)
                }
                !Patterns.EMAIL_ADDRESS.matcher(sEmail).matches() -> {
                    etEmail.error = getString(R.string.not_valid_email)
                }
                sPassword.isEmpty() -> {
                    etPassword.error = getString(R.string.entry_password)
                }
                sPassword.length < 8 -> {
                    etPassword.error = getString(R.string.minimum_character)
                }
                ktpImageUri == null -> {
                    Toast.makeText(this, R.string.add_ktp_photo_alert, Toast.LENGTH_SHORT).show()
                }
                selfieKtpImageUri == null -> {
                    Toast.makeText(this, R.string.add_selfie_ktp_photo_alert, Toast.LENGTH_SHORT).show()
                }
                sName.isEmpty() -> {
                    etName.error = getString(R.string.entry_name)
                }
                sCity.isEmpty() -> {
                    acCity.error = getString(R.string.entry_city)
                }
                sAddress.isEmpty() -> {
                    etAddress.error = getString(R.string.entry_address)
                }
                sNoPhone.isEmpty() -> {
                    etNoPhone.error = getString(R.string.entry_no_phone)
                }
                else -> {
                    when(sJenisAkun) {
                        getString(R.string.pembeli) -> {
                            progressBar.visibility = View.VISIBLE
                            firebaseAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                                .addOnCompleteListener(this) { task ->
                                    if(task.isSuccessful) {
                                        val userId = firebaseAuth.currentUser!!.uid
                                        db.collection("user").document(userId).set(userMap)

                                        val ktpFilename = UUID.randomUUID().toString()
                                        ktpImageUri?.let {
                                            storageRef.getReference("userDoc").child(userId).child(ktpFilename)
                                                .putFile(it)
                                                .addOnSuccessListener { task ->
                                                    task.metadata!!.reference!!.downloadUrl
                                                        .addOnSuccessListener {uri ->
                                                            val mapKtp = mapOf(
                                                                "fotoKtp" to uri.toString(),
                                                                "fotoKtpKeys" to ktpFilename
                                                            )
                                                            db.collection("user").document(userId).update(mapKtp)
                                                        }
                                                }
                                        }

                                        val selfieKtpFilename = UUID.randomUUID().toString()
                                        selfieKtpImageUri?.let {
                                            storageRef.getReference("userDoc").child(userId).child(selfieKtpFilename)
                                                .putFile(it)
                                                .addOnSuccessListener { task ->
                                                    task.metadata!!.reference!!.downloadUrl
                                                        .addOnSuccessListener {uri ->
                                                            val mapSelfieKtp = mapOf(
                                                                "fotoSelfieKtp" to uri.toString(),
                                                                "fotoSelfieKtpKeys" to selfieKtpFilename
                                                            )
                                                            db.collection("user").document(userId).update(mapSelfieKtp)
                                                        }
                                                }
                                        }

                                        val intent = Intent(this, SedangDiverifikasiActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    } else {
                                        progressBar.visibility = View.GONE
                                        Toast.makeText(this, R.string.already_exists_email, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        getString(R.string.vendor) -> {
                            val dialog = PotonganAlertFragment()
                            dialog.show(this.supportFragmentManager, "potonganDialog")
                        }
                    }
                }
            }
        }
    }

    override fun getAnswer(answer: String) {
        if (answer == getString(R.string.ya)) {
            val sName = etName.text.toString().trim()
            val sNoPhone = etNoPhone.text.toString().trim()
            val sCity = acCity.text.toString().trim()
            val sAddress = etAddress.text.toString().trim()
            val sEmail = etEmail.text.toString().trim()
            val sPassword = etPassword.text.toString().trim()
            val sJenisAkun = getString(R.string.vendor)
            val statusPendaftaran = getString(R.string.verifikasi)

            val userMap = hashMapOf(
                "jenis" to sJenisAkun,
                "nama" to sName,
                "kota" to sCity,
                "alamat" to sAddress,
                "telepon" to sNoPhone,
                "email" to sEmail,
                "statusPendaftaran" to statusPendaftaran
            )

            progressBar.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        val userId = FirebaseAuth.getInstance().currentUser!!.uid
                        db.collection("user").document(userId).set(userMap)

                        val ktpFilename = UUID.randomUUID().toString()
                        ktpImageUri?.let {
                            storageRef.getReference("userDoc").child(userId).child(ktpFilename)
                                .putFile(it)
                                .addOnSuccessListener { task ->
                                    task.metadata!!.reference!!.downloadUrl
                                        .addOnSuccessListener {uri ->
                                            val mapKtp = mapOf(
                                                "fotoKtp" to uri.toString(),
                                                "fotoKtpKeys" to ktpFilename
                                            )
                                            db.collection("user").document(userId).update(mapKtp)
                                        }
                                }
                        }

                        val selfieKtpFilename = UUID.randomUUID().toString()
                        selfieKtpImageUri?.let {
                            storageRef.getReference("userDoc").child(userId).child(selfieKtpFilename)
                                .putFile(it)
                                .addOnSuccessListener { task ->
                                    task.metadata!!.reference!!.downloadUrl
                                        .addOnSuccessListener {uri ->
                                            val mapSelfieKtp = mapOf(
                                                "fotoSelfieKtp" to uri.toString(),
                                                "fotoSelfieKtpKeys" to selfieKtpFilename
                                            )
                                            db.collection("user").document(userId).update(mapSelfieKtp)
                                        }
                                }
                        }

                        val intent = Intent(this, SedangDiverifikasiActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, R.string.already_exists_email, Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
}