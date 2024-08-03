package com.valdoang.kateringconnect.view.user.alamat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.AlamatAdapter
import com.valdoang.kateringconnect.databinding.ActivityAlamatBinding
import com.valdoang.kateringconnect.model.Alamat
import com.valdoang.kateringconnect.utils.Cons


class AlamatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlamatBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var alamatAdapter: AlamatAdapter
    private var alamatList: ArrayList<Alamat> = ArrayList()
    private lateinit var progressBar: ProgressBar
    private var alamatRumah = Alamat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlamatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid
        progressBar = binding.progressBar

        setupAction()
        setupView()
        setupAlamat()
    }

    private fun setupAlamat() {
        progressBar.visibility = View.VISIBLE
        val userRef = db.collection("user").document(userId)
        userRef.addSnapshotListener { userDoc, _ ->
            if (userDoc != null) {
                val kota = userDoc.data?.get("kota").toString()
                val alamat = userDoc.data?.get("alamat").toString()
                val namaKontak = userDoc.data?.get("nama").toString()
                val nomorKontak = userDoc.data?.get("telepon").toString()

                alamatRumah = Alamat(
                    id = userId,
                    nama = getString(R.string.rumah),
                    kota = kota,
                    alamat = alamat,
                    namaKontak = namaKontak,
                    nomorKontak = nomorKontak
                    )

                alamatList.add(alamatRumah)
            }

            val alamatTersimpanRef = userRef.collection("alamatTersimpan")
            alamatTersimpanRef.addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    alamatList.clear()
                    alamatList.add(alamatRumah)
                    for (data in snapshot.documents) {
                        val alamatTersimpan: Alamat? = data.toObject(Alamat::class.java)
                        if (alamatTersimpan != null) {
                            alamatTersimpan.id = data.id
                            alamatList.add(alamatTersimpan)
                        }
                    }

                    progressBar.visibility = View.GONE
                    alamatAdapter.setItems(alamatList)

                    alamatAdapter.setOnItemClickCallback(object : AlamatAdapter.OnItemClickCallback{
                        override fun onItemClicked(data: Alamat) {
                            setResult(
                                RESULT_OK,
                                Intent().putExtra(Cons.EXTRA_ID, data.id).putExtra(Cons.EXTRA_NAMA, data.nama)
                                    .putExtra(Cons.EXTRA_KOTA, data.kota).putExtra(Cons.EXTRA_ALAMAT, data.alamat)
                            )
                            finish()
                        }
                    })
                }
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvAlamat
        recyclerView.layoutManager = LinearLayoutManager(this)
        alamatAdapter = AlamatAdapter(this)
        recyclerView.adapter = alamatAdapter
        alamatAdapter.setItems(alamatList)
    }

    private fun setupAction() {
        binding.addAlamat.setOnClickListener {
            val intent = Intent(this, AddAlamatActivity::class.java)
            startActivity(intent)
        }
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}