package com.valdoang.kateringconnect.view.admin.ui.pengaturan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityAturPotonganBinding
import com.valdoang.kateringconnect.utils.Cons

class AturPotonganActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAturPotonganBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var adminId = firebaseAuth.currentUser!!.uid
    private var db = Firebase.firestore
    private var potongan: String? = null
    private lateinit var ibSave: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var etPotongan: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAturPotonganBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        ibSave = binding.ibSave
        progressBar = binding.progressBar
        etPotongan = binding.edPotongan

        potongan = intent.getStringExtra(Cons.EXTRA_NAMA)
        etPotongan.setText(potongan)

        updatePotongan()
        closeActivity()
    }

    private fun updatePotongan() {
        ibSave.setOnClickListener {
            val sPotongan = etPotongan.text.toString().trim()
            val updateMap = mapOf(
                "potongan" to sPotongan
            )

            when {
                sPotongan.isEmpty() -> {
                    etPotongan.error = getString(R.string.tidak_boleh_kosong)
                }
                else -> {
                    ibSave.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE

                    val adminRef = db.collection("user").document(adminId)
                    adminRef.update(updateMap).addOnSuccessListener {
                        finish()
                    } .addOnFailureListener {
                        Toast.makeText(this, R.string.gagal_mengubah, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun closeActivity() {
        binding.ibClose.setOnClickListener {
            finish()
        }
    }
}