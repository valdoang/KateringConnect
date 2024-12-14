package com.valdoang.kateringconnect.view.all.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityDitolakBinding
import com.valdoang.kateringconnect.utils.getImageUri
import com.valdoang.kateringconnect.view.all.logout.LogoutFragment
import java.util.*

class DitolakActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDitolakBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var userId = firebaseAuth.currentUser!!.uid
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private lateinit var ivKtp: ImageView
    private lateinit var ivSelfieKtp: ImageView
    private var ktpImageUri: Uri? = null
    private var selfieKtpImageUri: Uri? = null
    private var fotoKtpKeys = ""
    private var fotoSelfieKtpKeys = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDitolakBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        ivKtp = binding.ivKtp
        ivSelfieKtp = binding.ivSelfieKtp

        setupData()
        logout()
        setOnClick()
        setButtonClick()
    }

    private fun setupData() {
        val userRef = db.collection("user").document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val fotoKtp = document.data?.get("fotoKtp").toString()
                val fotoSelfieKtp = document.data?.get("fotoSelfieKtp").toString()
                val alasanPenolakanPendaftaran = document.data?.get("alasanPenolakanPendaftaran").toString()
                fotoKtpKeys = document.data?.get("fotoKtpKeys").toString()
                fotoSelfieKtpKeys = document.data?.get("fotoSelfieKtpKeys").toString()

                binding.tvAlasanPenolakan.text = alasanPenolakanPendaftaran
                Glide.with(this).load(fotoKtp).into(ivKtp)
                Glide.with(this).load(fotoSelfieKtp).into(ivSelfieKtp)
            }
        }
    }

    private fun setOnClick() {
        binding.cvKtp.setOnClickListener {
            ktpImageUri = getImageUri(this)
            launcherIntentCamera.launch(ktpImageUri)
        }
        binding.cvSelfieKtp.setOnClickListener {
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

    private fun setButtonClick() {
        binding.btnKirim.setOnClickListener {
            when {
                ktpImageUri == null && selfieKtpImageUri == null -> {
                    Toast.makeText(this, R.string.need_edit_alert, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    if (ktpImageUri != null) {
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
                                            storageRef.getReference("userDoc").child(userId).child(fotoKtpKeys).delete()
                                        }
                                }
                        }
                    }
                    if (selfieKtpImageUri != null) {
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
                                            storageRef.getReference("userDoc").child(userId).child(fotoSelfieKtpKeys).delete()
                                        }
                                }
                        }
                    }
                    val updateStatus = mapOf(
                        "statusPendaftaran" to getString(R.string.verifikasi)
                    )
                    db.collection("user").document(userId).update(updateStatus)

                    val intent = Intent(this, SedangDiverifikasiActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }

    private fun logout() {
        binding.ibLogout.setOnClickListener{
            val dialog = LogoutFragment()
            dialog.show(this.supportFragmentManager, "logoutDialog")
        }
    }
}