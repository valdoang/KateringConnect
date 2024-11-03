package com.valdoang.kateringconnect.view.admin.transferdana

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.RiwayatTransferDanaAdapter
import com.valdoang.kateringconnect.databinding.ActivityRiwayatTransferDanaBinding
import com.valdoang.kateringconnect.model.TarikDana
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.all.kcwallet.DetailRiwayatTarikTransferDanaActivity

class RiwayatTransferDanaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRiwayatTransferDanaBinding
    private var db = Firebase.firestore
    private var riwayatTransferDanaList: ArrayList<TarikDana> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var riwayatTransferDanaAdapter: RiwayatTransferDanaAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatTransferDanaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        progressBar = binding.progressBar

        setupView()
        setupData()
        setupAction()
    }

    private fun setupView() {
        recyclerView = binding.rvRiwayatTransferDana
        recyclerView.layoutManager = LinearLayoutManager(this)
        riwayatTransferDanaAdapter = RiwayatTransferDanaAdapter(this)
        recyclerView.adapter = riwayatTransferDanaAdapter
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val tarikDanaRef = db.collection("tarikDana").whereEqualTo("status", getString(R.string.status_selesai))
        tarikDanaRef.addSnapshotListener { transferDanaSnapshot, _ ->
            progressBar.visibility = View.GONE
            if (transferDanaSnapshot != null) {
                riwayatTransferDanaList.clear()
                for (data in transferDanaSnapshot.documents) {
                    val riwayatTransferDana: TarikDana? = data.toObject(TarikDana::class.java)
                    if (riwayatTransferDana != null) {
                        riwayatTransferDana.id = data.id
                        riwayatTransferDanaList.add(riwayatTransferDana)
                    }
                }

                riwayatTransferDanaList.sortByDescending {
                    it.tanggalPengajuan
                }

                riwayatTransferDanaAdapter.setItems(riwayatTransferDanaList)
                riwayatTransferDanaAdapter.setOnItemClickCallback(object :
                    RiwayatTransferDanaAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: TarikDana) {
                        val intent = Intent(this@RiwayatTransferDanaActivity, DetailRiwayatTarikTransferDanaActivity::class.java)
                        intent.putExtra(Cons.EXTRA_ID, data.id)
                        intent.putExtra(Cons.EXTRA_NAMA, getString(R.string.admin))
                        startActivity(intent)
                    }
                })

                if (riwayatTransferDanaList.isEmpty()) {
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