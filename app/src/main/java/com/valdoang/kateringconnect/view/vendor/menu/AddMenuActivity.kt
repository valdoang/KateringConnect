package com.valdoang.kateringconnect.view.vendor.menu

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityAddMenuBinding
import com.valdoang.kateringconnect.utils.getImageUri
import java.util.*

class AddMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMenuBinding
    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var etPrice: EditText
    private lateinit var acJenis: AutoCompleteTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        storageRef = FirebaseStorage.getInstance()

        etName = binding.edAddName
        etDesc = binding.edAddDesc
        etPrice = binding.edAddPrice
        acJenis = binding.acAddJenis
        progressBar = binding.progressBar

        setupAction()
        setupAcJenis()
        saveData()
    }

    private fun setupAcJenis() {
        val jenis = resources.getStringArray(R.array.Jenis)
        val dropdownAdapter = ArrayAdapter(this, R.layout.dropdown_item, jenis)
        acJenis.setAdapter(dropdownAdapter)
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener{
            onBackPressed()
        }

        binding.tvAddPhoto.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_add_photo, null)

            val cvGaleri = view.findViewById<CardView>(R.id.cv_gallery)
            val cvCamera = view.findViewById<CardView>(R.id.cv_camera)

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

    private fun saveData() {
        var sJenis = ""

        acJenis.onItemClickListener = AdapterView.OnItemClickListener{
                adapterView, _, i, _ ->

            sJenis = adapterView.getItemAtPosition(i).toString()
        }

        binding.btnTambah.setOnClickListener{
            val sName = etName.text.toString().trim()
            val sDesc = etDesc.text.toString().trim()
            val sPrice = etPrice.text.toString().trim()

            val userId = firebaseAuth.currentUser!!.uid
            val filename = UUID.randomUUID().toString()

            when {
                currentImageUri == null -> {
                    Toast.makeText(this, R.string.can_not_blank, Toast.LENGTH_SHORT).show()
                }
                sName.isEmpty() -> {
                    etName.error = getString(R.string.entry_name)
                }
                sDesc.isEmpty() -> {
                    etDesc.error = getString(R.string.entry_desc)
                }
                sPrice.isEmpty() -> {
                    etPrice.error = getString(R.string.entry_price)
                }
                sJenis.isEmpty() -> {
                    acJenis.error = getString(R.string.entry_jenis)
                }
                else -> {
                    progressBar.visibility = View.VISIBLE

                    currentImageUri?.let {
                        storageRef.getReference("menuImages").child(filename)
                            .putFile(it)
                            .addOnSuccessListener { task ->
                                task.metadata!!.reference!!.downloadUrl
                                    .addOnSuccessListener {uri ->
                                        val mapImage = mapOf(
                                            "foto" to uri.toString(),
                                            "nama" to sName,
                                            "keterangan" to sDesc,
                                            "harga" to sPrice,
                                            "jenis" to sJenis,
                                            "storageKeys" to filename,
                                            "userId" to userId
                                        )
                                        db.collection("menu").document().set(mapImage)
                                            .addOnSuccessListener {
                                                progressBar.visibility = View.GONE
                                                onBackPressed()
                                                Toast.makeText(this, R.string.success_upload_menu, Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener{
                                                progressBar.visibility = View.GONE
                                                Toast.makeText(this, R.string.fail_upload_menu, Toast.LENGTH_SHORT).show()
                                            }
                                    }
                            }
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
            Log.d("Image URI", "showImage: $it")
            binding.ivEditPhoto.setImageURI(it)
        }
    }
}