package com.valdoang.kateringconnect.view.all.editakun

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityEditTextBinding
import com.valdoang.kateringconnect.utils.Cons

class EditNamaAkunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTextBinding
    private var namaAkun: String? = null
    private lateinit var etNama: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var ibSave: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvTitle: TextView
    private lateinit var tvName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth

        namaAkun = intent.getStringExtra(Cons.EXTRA_NAMA)

        ibSave = binding.ibSave
        progressBar = binding.progressBar
        etNama = binding.edAddName
        tvTitle = binding.titleEditText
        tvName = binding.tvName


        setupView()
        updateName()
        closeActivity()
    }

    private fun setupView() {
        tvTitle.text = getString(R.string.tv_nama)
        tvName.text = getString(R.string.tv_nama)
        etNama.setText(namaAkun)
        etNama.inputType = InputType.TYPE_CLASS_TEXT
    }

    private fun updateName() {
        ibSave.setOnClickListener {
            val userId = firebaseAuth.currentUser!!.uid
            val sName = etNama.text.toString().trim()
            val updateMap = mapOf(
                "nama" to sName
            )
            when {
                sName.isEmpty() -> {
                    etNama.error = getString(R.string.tidak_boleh_kosong)
                }
                else -> {
                    ibSave.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE

                    val ref = db.collection("user").document(userId)
                    ref.update(updateMap).addOnSuccessListener {
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