package com.valdoang.kateringconnect.view.both.editakun

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

class EditAlamatAkunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTextBinding
    private var alamatAkun: String? = null
    private lateinit var etAlamat: EditText
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

        alamatAkun = intent.getStringExtra(Cons.EXTRA_NAMA)

        ibSave = binding.ibSave
        progressBar = binding.progressBar
        etAlamat = binding.edAddName
        tvTitle = binding.titleEditText
        tvName = binding.tvName


        setupView()
        updateName()
        closeActivity()
    }

    private fun setupView() {
        tvTitle.text = getString(R.string.tv_address)
        tvName.text = getString(R.string.tv_address)
        etAlamat.setText(alamatAkun)
        etAlamat.inputType = InputType.TYPE_CLASS_TEXT
    }

    private fun updateName() {
        ibSave.setOnClickListener {
            ibSave.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            val userId = firebaseAuth.currentUser!!.uid
            val sAlamat = etAlamat.text.toString().trim()
            val updateMap = mapOf(
                "alamat" to sAlamat
            )
            val ref = db.collection("user").document(userId)
            ref.update(updateMap).addOnSuccessListener {
                finish()
            } .addOnFailureListener {
                Toast.makeText(this, R.string.gagal_mengubah, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun closeActivity() {
        binding.ibClose.setOnClickListener {
            finish()
        }
    }
}