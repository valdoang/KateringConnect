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

class EditMinOrderMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTextBinding
    private var minOrderMenu: String? = null
    private var kategoriMenuId: String? = null
    private var menuId: String? = null
    private lateinit var etMinOrder: EditText
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

        minOrderMenu = intent.getStringExtra(Cons.EXTRA_NAMA)
        kategoriMenuId = intent.getStringExtra(Cons.EXTRA_ID)
        menuId = intent.getStringExtra(Cons.EXTRA_SEC_ID)

        ibSave = binding.ibSave
        progressBar = binding.progressBar
        etMinOrder = binding.edAddName
        tvTitle = binding.titleEditText
        tvName = binding.tvName


        setupView()
        updateMinOrder()
        closeActivity()
    }

    private fun setupView() {
        tvTitle.text = getString(R.string.minimal_pemesanan)
        tvName.text = getString(R.string.minimal_pemesanan)
        etMinOrder.setText(minOrderMenu)
        etMinOrder.inputType = InputType.TYPE_CLASS_NUMBER
    }

    private fun updateMinOrder() {
        ibSave.setOnClickListener {
            ibSave.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            val userId = firebaseAuth.currentUser!!.uid
            val sMinOrder = etMinOrder.text.toString().trim()
            val updateMap = mapOf(
                "minOrder" to sMinOrder
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