package com.valdoang.kateringconnect.view.all.kcwallet

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityKcwalletBinding
import com.valdoang.kateringconnect.utils.withNumberingFormat

class KcwalletActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKcwalletBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId: String? = null
    private lateinit var tvSaldo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKcwalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid
        tvSaldo = binding.tvSaldoKcwallet

        setupAction()
        setupData()
    }

    private fun setupData() {
        val userRef = db.collection("user").document(userId!!)
        userRef.addSnapshotListener { userSnapshot, _ ->
            if (userSnapshot != null) {
                var saldo = userSnapshot.data?.get("saldo")
                if (saldo == null) {
                    saldo = "0"
                }
                tvSaldo.text = getString(R.string.rupiah_text, saldo.toString().withNumberingFormat())
            }
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
        binding.clTopup.setOnClickListener {
            val intent = Intent(this, TopupActivity::class.java)
            startActivity(intent)
        }
        binding.clTarikDana.setOnClickListener {
            val intent = Intent(this, TarikDanaActivity::class.java)
            startActivity(intent)
        }
        binding.clInfoRekening.setOnClickListener {
            val intent = Intent(this, RekeningBankActivity::class.java)
            startActivity(intent)
        }
        binding.clMutasi.setOnClickListener {
            val intent = Intent(this, MutasiActivity::class.java)
            startActivity(intent)
        }
    }
}