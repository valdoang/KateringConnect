package com.valdoang.kateringconnect.view.vendor.menu

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
import com.valdoang.kateringconnect.databinding.ActivityAddEditMenuBinding
import com.valdoang.kateringconnect.utils.getImageUri


class EditMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditMenuBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private lateinit var ivFoto: ImageView
    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var etPrice: EditText
    private lateinit var acKategori: AutoCompleteTextView
    private lateinit var tvHapus: TextView
    private var storageKeys = ""
    private var nama = ""
    private var desc = ""
    private var price = ""
    private var jenis = ""
    private var menuId: String? = null
    private var currentImageUri: Uri? = null
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        storageRef = FirebaseStorage.getInstance()

        progressBar = binding.progressBar
        ivFoto = binding.ivEditPhoto
        etName = binding.edAddName
        etDesc = binding.edAddDesc
        etPrice = binding.edAddPrice
        acKategori = binding.acAddKategori
        tvHapus = binding.tvHapus

        menuId = intent.getStringExtra(EXTRA_ID)

        setupView()
        setData(menuId!!)
        setupAction()
        updateData()
        deleteData()
        closeDialog()
    }

    private fun setupView() {
        tvHapus.visibility = View.VISIBLE
    }

    private fun setData(menuId: String) {
        val ref = db.collection("menu").document(menuId)
        ref.addSnapshotListener{document,_ ->
                if (document != null) {
                    val foto = document.data?.get("foto").toString()
                    Glide.with(applicationContext).load(foto).error(R.drawable.addphoto).into(ivFoto)
                }
            }
        ref.get().addOnSuccessListener { document ->
            if (document != null) {
                nama = document.data?.get("nama").toString()
                desc = document.data?.get("keterangan").toString()
                price = document.data?.get("harga").toString()
                jenis = document.data?.get("jenis").toString()
                storageKeys = document.data?.get("storageKeys").toString()
                etName.setText(nama)
                etDesc.setText(desc)
                etPrice.setText(price)
                acKategori.setText(jenis, false)
            }
        }
    }

    private fun updateData() {
        binding.ibSave.setOnClickListener{
            val sName = etName.text.toString().trim()
            val sDesc = etDesc.text.toString().trim()
            val sPrice = etPrice.text.toString().trim()
            val sKategori = acKategori.text.toString().trim()

            val updateMap = mapOf(
                "nama" to sName,
                "keterangan" to sDesc,
                "harga" to sPrice,
                "kategori" to sKategori
            )
            if (sName == nama && sDesc == desc && sPrice == price && sKategori == jenis) {
                Toast.makeText(this, R.string.no_one_change, Toast.LENGTH_SHORT).show()
            }
            else {
                db.collection("menu").document(menuId!!).update(updateMap)
                    .addOnSuccessListener {
                        onBackPressed()
                        Toast.makeText(this, R.string.success_update_data, Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, R.string.fail_update_data, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun deleteData() {
        tvHapus.setOnClickListener{
            onBackPressed()
            db.collection("menu").document(menuId!!).delete()
            storageRef.getReference("menuImages").child(storageKeys).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, R.string.success_delete_data, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, R.string.fail_delete_data, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun closeDialog() {
        binding.ibBack.setOnClickListener{
            onBackPressed()
        }
    }

    private fun setupAction() {
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
        currentImageUri?.let {
            storageRef.getReference("menuImages").child(storageKeys)
                .putFile(it)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {uri ->
                            val mapImage = mapOf(
                                "foto" to uri.toString()
                            )
                            db.collection("menu").document(menuId!!).update(mapImage)
                                .addOnSuccessListener {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(this, R.string.success_upload_photo, Toast.LENGTH_SHORT).show()
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

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}