package com.valdoang.kateringconnect.view.user.detailvendor

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.adapter.GalleryAdapter
import com.valdoang.kateringconnect.databinding.ActivityGaleriBinding
import com.valdoang.kateringconnect.model.Gallery
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.both.detailgaleri.DetailGaleriFragment

class GaleriVendorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGaleriBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var vendorId: String? = null
    private var galeriList: ArrayList<Gallery> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGaleriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        vendorId = intent.getStringExtra(Cons.EXTRA_ID)
        progressBar = binding.progressBar

        setupView()
        setupData()
        setupAction()
    }

    private fun setupView() {
        recyclerView = binding.rvGallery
        recyclerView.layoutManager = GridLayoutManager(this,2)
        galleryAdapter = GalleryAdapter(this)
        recyclerView.adapter = galleryAdapter
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val galeriRef = db.collection("user").document(vendorId!!).collection("galeri")
        galeriRef.addSnapshotListener{ snapshot, _ ->
            progressBar.visibility = View.GONE
            if (snapshot != null) {
                galeriList.clear()
                for (data in snapshot.documents) {
                    val galeri: Gallery? = data.toObject(Gallery::class.java)
                    if (galeri != null) {
                        galeri.id = data.id
                        galeriList.add(galeri)
                    }
                }

                galleryAdapter.setItems(galeriList)
                galleryAdapter.setOnItemClickCallback(object : GalleryAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Gallery) {
                        val args = Bundle()
                        args.putString("galleryId", data.id)
                        args.putString("vendorId", vendorId)
                        val newFragment = DetailGaleriFragment()
                        newFragment.arguments = args
                        newFragment.show(supportFragmentManager, "detailGaleriFragment")
                    }
                })

                if (galeriList.isEmpty()) {
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
        binding.ibAdd.visibility = View.GONE
    }
}