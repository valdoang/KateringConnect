package com.valdoang.kateringconnect.view.vendor.detailpesanan

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivitySelesaikanPesananBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.getImageUri
import java.util.*

class SelesaikanPesananActivity : AppCompatActivity() {
    //TODO: BELUM DITES RUN
    private lateinit var binding: ActivitySelesaikanPesananBinding
    private var pesananId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private var currentImageUri: Uri? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var ibSave: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelesaikanPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        pesananId = intent.getStringExtra(Cons.EXTRA_ID)
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
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        }
    }

    private fun uploadPhoto() {
        progressBar.visibility = View.VISIBLE
        ibSave.visibility = View.GONE
        val userId = firebaseAuth.currentUser!!.uid
        val filename = UUID.randomUUID().toString()
        currentImageUri?.let {
            storageRef.getReference("buktiPengiriman").child(userId).child(filename)
                .putFile(it)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {uri ->
                            val mapBuktiPengiriman = mapOf(
                                "fotoBuktiPengiriman" to uri.toString(),
                                "storageKeys" to filename,
                                "status" to getString(R.string.status_selesai)
                            )
                            db.collection("pesanan").document(pesananId!!).update(mapBuktiPengiriman)
                                .addOnSuccessListener {
                                    finish()
                                    Toast.makeText(this, R.string.success_selesaikan_pesanan, Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{
                                    Toast.makeText(this, R.string.fail_selesaikan_pesanan, Toast.LENGTH_SHORT).show()
                                }
                        }
                }
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