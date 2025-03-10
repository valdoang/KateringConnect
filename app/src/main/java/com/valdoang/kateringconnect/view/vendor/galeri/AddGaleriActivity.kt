package com.valdoang.kateringconnect.view.vendor.galeri

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityAddGaleriBinding
import com.valdoang.kateringconnect.utils.getImageUri
import java.util.*

class AddGaleriActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddGaleriBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private var currentImageUri: Uri? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var ibSave: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGaleriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        storageRef = FirebaseStorage.getInstance()
        progressBar = binding.progressBar
        ibSave = binding.ibSave

        setupAction()
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }

        ibSave.setOnClickListener {
            if(currentImageUri == null) {
                Toast.makeText(this, R.string.add_photo_alert, Toast.LENGTH_SHORT).show()
            }
            else {
                uploadPhoto()
            }
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
        ibSave.visibility = View.GONE
        val userId = firebaseAuth.currentUser!!.uid
        val filename = UUID.randomUUID().toString()
        currentImageUri?.let {
            storageRef.getReference("vendorGallery").child(userId).child(filename)
                .putFile(it)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {uri ->
                            val mapGallery = mapOf(
                                "foto" to uri.toString(),
                                "storageKeys" to filename
                            )
                            db.collection("user").document(userId).collection("galeri").document().set(mapGallery)
                                .addOnSuccessListener {
                                    finish()
                                    Toast.makeText(this, R.string.success_upload_gallery, Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{
                                    Toast.makeText(this, R.string.fail_upload_gallery, Toast.LENGTH_SHORT).show()
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
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
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
        currentImageUri?.let {
            binding.ivFoto.setImageURI(it)
        }
        binding.tvAddPhoto.text = getString(R.string.edit_foto)
    }
}