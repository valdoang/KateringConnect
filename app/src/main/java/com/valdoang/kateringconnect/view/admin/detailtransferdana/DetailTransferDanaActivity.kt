package com.valdoang.kateringconnect.view.admin.detailtransferdana

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityDetailTransferDanaBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.getImageUri
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat
import java.util.*

class DetailTransferDanaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTransferDanaBinding
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private var tarikDanaId: String? = null
    private lateinit var progressBar: ProgressBar
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransferDanaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        tarikDanaId = intent.getStringExtra(Cons.EXTRA_ID)
        storageRef = FirebaseStorage.getInstance()
        progressBar = binding.progressBar

        setupData()
        kirimBuktiTransfer()
        setupAction()
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val tarikDanaRef = db.collection("tarikDana").document(tarikDanaId!!)
        tarikDanaRef.get().addOnSuccessListener { document->
            if (document != null) {
                val userId = document.data?.get("userId").toString()
                val nominal = document.data?.get("nominal").toString()
                val namaBank = document.data?.get("namaBank").toString()
                val namaPemilikRekening = document.data?.get("namaPemilikRekening").toString()
                val nomorRekening = document.data?.get("nomorRekening").toString()
                val tanggalPengajuan = document.data?.get("tanggalPengajuan").toString()

                val userRef = db.collection("user").document(userId)
                userRef.addSnapshotListener { userSnapshot, _ ->
                    progressBar.visibility = View.GONE
                    if (userSnapshot != null) {
                        val foto = userSnapshot.data?.get("foto").toString()
                        val nama = userSnapshot.data?.get("nama").toString()
                        Glide.with(this).load(foto).error(R.drawable.default_profile).into(binding.ivUser)
                        binding.tvNamaUser.text = nama
                    }
                }

                binding.tvTanggal.text = tanggalPengajuan.withTimestampToDateTimeFormat()
                binding.tvNominal.text = getString(R.string.rupiah_text, nominal.withNumberingFormat())
                binding.tvNamaBank.text = namaBank
                binding.tvNamaPemilikRekening.text = namaPemilikRekening
                binding.tvNomorRekening.text = nomorRekening

            }
        }
    }

    private fun kirimBuktiTransfer() {
        binding.btnKirimBuktiTransfer.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_add_photo, null)

            val cvGaleri = view.findViewById<ConstraintLayout>(R.id.cv_gallery)
            val cvCamera = view.findViewById<ConstraintLayout>(R.id.cv_camera)

            cvGaleri.setOnClickListener {
                launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                dialog.dismiss()
            }

            cvCamera.setOnClickListener {
                currentImageUri = getImageUri(this)
                launcherIntentCamera.launch(currentImageUri)
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    private fun uploadPhoto() {
        progressBar.visibility = View.VISIBLE
        val filename = UUID.randomUUID().toString()
        currentImageUri?.let {
            storageRef.getReference("buktiTransferAdmin").child(filename)
                .putFile(it)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {uri ->
                            val sStatus = getString(R.string.status_selesai)
                            val sTanggalDibayarkan = System.currentTimeMillis().toString()
                            val mapImage = mapOf(
                                "fotoBuktiTransfer" to uri.toString(),
                                "status" to sStatus,
                                "tanggalDibayarkan" to sTanggalDibayarkan
                            )
                            db.collection("tarikDana").document(tarikDanaId!!).update(mapImage)
                                .addOnSuccessListener {
                                    progressBar.visibility = View.GONE
                                    finish()
                                }
                                .addOnFailureListener{
                                    Toast.makeText(this, R.string.fail_upload_bukti_transfer, Toast.LENGTH_SHORT).show()
                                }
                        }
                }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            uploadPhoto()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            uploadPhoto()
        }
    }
}