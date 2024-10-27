package com.valdoang.kateringconnect.view.all.kcwallet

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.adapter.RiwayatTarikDanaAdapter
import com.valdoang.kateringconnect.databinding.ActivityRiwayatTarikDanaBinding
import com.valdoang.kateringconnect.model.TarikDana

class RiwayatTarikDanaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRiwayatTarikDanaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId = ""
    private var tarikDanaList: ArrayList<TarikDana> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var riwayatTarikDanaAdapter: RiwayatTarikDanaAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatTarikDanaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid
        progressBar = binding.progressBar

        setupView()
        setupData()
        setupAction()
    }
    
    private fun setupView() {
        recyclerView = binding.rvRiwayatTarikDana
        recyclerView.layoutManager = LinearLayoutManager(this)
        riwayatTarikDanaAdapter = RiwayatTarikDanaAdapter(this)
        recyclerView.adapter = riwayatTarikDanaAdapter
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val tarikDanaRef = db.collection("tarikDana").whereEqualTo("userId", userId)
        tarikDanaRef.addSnapshotListener { tarikDanaSnapshot, _ ->
            progressBar.visibility = View.GONE
            if (tarikDanaSnapshot != null) {
                tarikDanaList.clear()
                for (data in tarikDanaSnapshot.documents) {
                    val tarikDana: TarikDana? = data.toObject(TarikDana::class.java)
                    if (tarikDana != null) {
                        tarikDana.id = data.id
                        tarikDanaList.add(tarikDana)
                    }
                }

                tarikDanaList.sortByDescending {
                    it.tanggalPengajuan
                }

                riwayatTarikDanaAdapter.setItems(tarikDanaList)

                if (tarikDanaList.isEmpty()) {
                    binding.noDataAnimation.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.VISIBLE

                }
                else {
                    binding.noDataAnimation.visibility = View.GONE
                    binding.tvNoData.visibility = View.GONE
                }
            }
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}