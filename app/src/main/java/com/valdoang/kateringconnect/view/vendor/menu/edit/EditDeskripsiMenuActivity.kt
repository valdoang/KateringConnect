package com.valdoang.kateringconnect.view.vendor.menu.edit

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

class EditDeskripsiMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTextBinding
    private var deskripsiMenu: String? = null
    private var kategoriMenuId: String? = null
    private var menuId: String? = null
    private lateinit var etKeterangan: EditText
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

        deskripsiMenu = intent.getStringExtra(Cons.EXTRA_NAMA)
        kategoriMenuId = intent.getStringExtra(Cons.EXTRA_ID)
        menuId = intent.getStringExtra(Cons.EXTRA_SEC_ID)

        ibSave = binding.ibSave
        progressBar = binding.progressBar
        etKeterangan = binding.edAddName
        tvTitle = binding.titleEditText
        tvName = binding.tvName


        setupView()
        updateDeskripsi()
        closeActivity()
    }

    private fun setupView() {
        tvTitle.text = getString(R.string.tv_desc)
        tvName.text = getString(R.string.tv_desc)
        etKeterangan.setText(deskripsiMenu)
        etKeterangan.inputType = InputType.TYPE_CLASS_TEXT
    }

    private fun updateDeskripsi() {
        ibSave.setOnClickListener {
            ibSave.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            val userId = firebaseAuth.currentUser!!.uid
            val sKeterangan = etKeterangan.text.toString().trim()
            val updateMap = mapOf(
                "keterangan" to sKeterangan
            )
            val ref = db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenuId!!).collection("menu").document(menuId!!)
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