package com.valdoang.kateringconnect.view.both.akun

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.valdoang.kateringconnect.utils.getImageUri
import java.util.*


class EditAkunActivity : AppCompatActivity() {
    //TODO: 7. Mengubah Desain UI dan cara Update Data

    private lateinit var binding: ActivityEditAkunBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private lateinit var progressBar: ProgressBar
    private lateinit var etName: EditText
    private lateinit var acCity: AutoCompleteTextView
    private lateinit var etAddress: EditText
    private lateinit var etNoPhone: EditText
    private lateinit var ivEditPhoto: ImageView
    private var currentImageUri: Uri? = null
    private var nama = ""
    private var kota = ""
    private var alamat = ""
    private var telepon = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        storageRef = FirebaseStorage.getInstance()

        progressBar = binding.progressBar
        etName = binding.edEditName
        acCity = binding.acEditCity
        etAddress = binding.edEditAddress
        etNoPhone = binding.edEditNoPhone
        ivEditPhoto = binding.ivEditPhoto

        setupAcCity()
        setupAccount()
        setupAction()
        updateData()
    }

    private fun setupAccount() {
        val userId = firebaseAuth.currentUser!!.uid
        val ref = db.collection("user").document(userId)
        ref.addSnapshotListener { document,_ ->
            if (document != null) {
                val foto = document.data?.get("foto").toString()
                Glide.with(applicationContext).load(foto).error(R.drawable.default_profile).into(ivEditPhoto)

            }
        }

        ref.get().addOnSuccessListener { document ->
            if (document != null) {
                nama = document.data?.get("nama").toString()
                kota = document.data?.get("kota").toString()
                alamat = document.data?.get("alamat").toString()
                telepon = document.data?.get("telepon").toString()

                etName.setText(nama)
                acCity.setText(kota,false)
                etAddress.setText(alamat)
                etNoPhone.setText(telepon)
            }
        }
    }

    private fun setupAcCity() {
        val cities = resources.getStringArray(R.array.Cities)
        val dropdownAdapter = ArrayAdapter(this, R.layout.dropdown_item, cities)
        acCity.setAdapter(dropdownAdapter)
    }

    private fun updateData() {
        binding.ibSave.setOnClickListener {
            val sName = etName.text.toString().trim()
            val sNoPhone = etNoPhone.text.toString().trim()
            val sAddress = etAddress.text.toString().trim()
            val sKota = acCity.text.toString().trim()

            val userId = firebaseAuth.currentUser!!.uid

            val updateMap = mapOf(
                "nama" to sName,
                "kota" to sKota,
                "alamat" to sAddress,
                "telepon" to sNoPhone
            )
            if (sName == nama && sNoPhone == telepon && sAddress == alamat && sKota == kota) {
                finish()
            }
            else {
                db.collection("user").document(userId).update(updateMap)
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, R.string.fail_update_data, Toast.LENGTH_SHORT).show()
                    }
            }
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