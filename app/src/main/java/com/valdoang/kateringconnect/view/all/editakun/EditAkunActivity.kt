package com.valdoang.kateringconnect.view.all.editakun

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityEditAkunBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.getImageUri
import java.util.*


class EditAkunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAkunBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private lateinit var progressBar: ProgressBar
    private lateinit var ivEditPhoto: ImageView
    private var currentImageUri: Uri? = null
    private var nama = ""
    private var kota = ""
    private var alamat = ""
    private var telepon = ""
    private var jenis = ""
    private lateinit var tvNama: TextView
    private lateinit var tvKota: TextView
    private lateinit var tvAlamat: TextView
    private lateinit var tvTelepon: TextView
    private lateinit var cvNama: CardView
    private lateinit var cvKota: CardView
    private lateinit var cvAlamat: CardView
    private lateinit var cvTelepon: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        storageRef = FirebaseStorage.getInstance()

        progressBar = binding.progressBar
        ivEditPhoto = binding.ivEditPhoto
        tvNama = binding.tvNama
        tvKota = binding.tvKota
        tvAlamat = binding.tvAlamat
        tvTelepon = binding.tvTelepon
        cvNama = binding.cvNama
        cvKota = binding.cvKota
        cvAlamat = binding.cvAlamat
        cvTelepon = binding.cvTelepon

        setupAccount()
        editEach()
        setupAction()
    }

    private fun setupAccount() {
        val userId = firebaseAuth.currentUser!!.uid
        val ref = db.collection("user").document(userId)
        ref.addSnapshotListener { document,_ ->
            if (document != null) {
                val foto = document.data?.get("foto").toString()
                nama = document.data?.get("nama").toString()
                kota = document.data?.get("kota").toString()
                alamat = document.data?.get("alamat").toString()
                telepon = document.data?.get("telepon").toString()
                jenis = document.data?.get("jenis").toString()

                tvNama.text = nama
                tvKota.text = kota
                tvAlamat.text = alamat
                tvTelepon.text = telepon
                when(jenis) {
                    getString(R.string.pembeli) -> {
                        Glide.with(applicationContext).load(foto).error(R.drawable.default_profile).into(ivEditPhoto)
                    }
                    getString(R.string.vendor) -> {
                        Glide.with(applicationContext).load(foto).error(R.drawable.default_vendor_profile).into(ivEditPhoto)
                    }
                }
            }
        }
    }

    private fun editEach() {
        cvNama.setOnClickListener {
            val intent = Intent(this, EditNamaAkunActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, nama)
            startActivity(intent)
        }
        cvKota.setOnClickListener {
            val intent = Intent(this, EditKotaAkunActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, kota)
            startActivity(intent)
        }
        cvAlamat.setOnClickListener {
            val intent = Intent(this, EditAlamatAkunActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, alamat)
            startActivity(intent)
        }
        cvTelepon.setOnClickListener {
            val intent = Intent(this, EditTeleponAkunActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, telepon)
            startActivity(intent)
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.tvAddPhoto.setOnClickListener {
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

    private fun uploadPhoto() {
        progressBar.visibility = View.VISIBLE
        val userId = firebaseAuth.currentUser!!.uid
        val filename = UUID.randomUUID().toString()
        currentImageUri?.let {
            storageRef.getReference("userImages").child(filename)
                .putFile(it)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {uri ->
                            val mapImage = mapOf(
                                "foto" to uri.toString()
                            )
                            db.collection("user").document(userId).update(mapImage)
                                .addOnSuccessListener {
                                    progressBar.visibility = View.GONE
                                }
                                .addOnFailureListener{
                                    Toast.makeText(this, R.string.fail_upload_photo, Toast.LENGTH_SHORT).show()
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