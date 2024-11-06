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

class EditHargaMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTextBinding
    private var hargaMenu: String? = null
    private var kategoriMenuId: String? = null
    private var menuId: String? = null
    private lateinit var etHarga: EditText
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

        hargaMenu = intent.getStringExtra(Cons.EXTRA_NAMA)
        kategoriMenuId = intent.getStringExtra(Cons.EXTRA_ID)
        menuId = intent.getStringExtra(Cons.EXTRA_SEC_ID)

        ibSave = binding.ibSave
        progressBar = binding.progressBar
        etHarga = binding.edAddName
        tvTitle = binding.titleEditText
        tvName = binding.tvName


        setupView()
        updateHarga()
        closeActivity()
    }

    private fun setupView() {
        tvTitle.text = getString(R.string.tv_harga)
        tvName.text = getString(R.string.tv_harga)
        etHarga.setText(hargaMenu)
        etHarga.inputType = InputType.TYPE_CLASS_NUMBER
    }

    private fun updateHarga() {
        ibSave.setOnClickListener {
            val userId = firebaseAuth.currentUser!!.uid
            val sHarga = etHarga.text.toString().trim()
            val updateMap = mapOf(
                "harga" to sHarga
            )

            when {
                sHarga.isEmpty() -> {
                    etHarga.error = getString(R.string.tidak_boleh_kosong)
                }
                else -> {
                    ibSave.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE

                    val ref = db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenuId!!).collection("menu").document(menuId!!)
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