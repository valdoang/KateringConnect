package com.valdoang.kateringconnect.view.all.editakun

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityEditAcBinding
import com.valdoang.kateringconnect.utils.Cons

class EditKotaAkunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAcBinding
    private var kotaAkun: String? = null
    private lateinit var acKota:AutoCompleteTextView
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var ibSave: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvTitle: TextView
    private lateinit var tvName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth

        kotaAkun = intent.getStringExtra(Cons.EXTRA_NAMA)

        ibSave = binding.ibSave
        progressBar = binding.progressBar
        acKota = binding.edAddName
        tvTitle = binding.titleEditAc
        tvName = binding.tvName

        setupAcCity()
        setupView()
        updateKota()
        closeActivity()
    }

    private fun setupAcCity() {
        val cities = resources.getStringArray(R.array.Cities)
        val dropdownAdapter = ArrayAdapter(this, R.layout.dropdown_item, cities)
        acKota.setAdapter(dropdownAdapter)
    }

    private fun setupView() {
        tvTitle.text = getString(R.string.tv_city)
        tvName.text = getString(R.string.tv_city)
        acKota.setText(kotaAkun)
    }

    private fun updateKota() {
        ibSave.setOnClickListener {
            ibSave.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            val userId = firebaseAuth.currentUser!!.uid
            val sKota = acKota.text.toString().trim()
            val updateMap = mapOf(
                "kota" to sKota
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