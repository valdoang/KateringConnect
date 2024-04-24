package com.valdoang.kateringconnect.view.user

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.GalleryAdapter
import com.valdoang.kateringconnect.databinding.FragmentVendorAkunBinding
import com.valdoang.kateringconnect.model.Gallery
import com.valdoang.kateringconnect.view.vendor.galeri.DetailGaleriFragment
import com.valdoang.kateringconnect.view.vendor.menu.VendorMenuActivity

class DetailVendorActivity : AppCompatActivity() {
    private lateinit var binding: FragmentVendorAkunBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var tvName: TextView
    private lateinit var tvCity: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvNoPhone: TextView
    private lateinit var ivVendorAkun: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var galleryList: ArrayList<Gallery>
    private lateinit var galleryAdapter: GalleryAdapter
    private var foto: String? = null
    private var nama: String? = null
    private var vendorId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentVendorAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        vendorId = intent.getStringExtra(EXTRA_ID)

        firebaseAuth = Firebase.auth
        galleryList = arrayListOf()

        tvName = binding.tvVendorAkunName
        tvCity = binding.tvCity
        tvAddress = binding.tvAddress
        tvNoPhone = binding.tvNoPhone
        ivVendorAkun = binding.ivVendorAkun

        setupAccount()
        setupView()
        setupAction()
        hideUI()
    }

    private fun hideUI() {
        binding.titleVendorAkun.visibility = View.GONE
        binding.btnVendorLogout.visibility = View.GONE
        binding.btnVendorEditAkun.visibility = View.GONE
        binding.btnAddGaleri.visibility = View.GONE
        binding.btnAddMenu.visibility = View.GONE
    }

    private fun setupAccount() {
        val userRef = db.collection("user").document(vendorId!!)
        userRef.addSnapshotListener { document,_ ->
            if (document != null) {
                foto = document.data?.get("foto").toString()
                nama = document.data?.get("nama").toString()
                val kota = document.data?.get("kota").toString()
                val alamat = document.data?.get("alamat").toString()
                val telepon = document.data?.get("telepon").toString()

                Glide.with(applicationContext).load(foto).error(R.drawable.default_vendor_profile).into(ivVendorAkun)
                tvName.text = nama
                tvCity.text = kota
                tvAddress.text = alamat
                tvNoPhone.text = telepon
            }
        }

        val galleryRef = db.collection("gallery").whereEqualTo("userId", vendorId)
        galleryRef.addSnapshotListener{ snapshot,_ ->
            if (snapshot != null) {
                Log.d(ContentValues.TAG, "Current data: ${snapshot.documents}")
                galleryList.clear()
                for (data in snapshot.documents) {
                    val gallery: Gallery? = data.toObject(Gallery::class.java)
                    if (gallery != null) {
                        gallery.id = data.id
                        galleryList.add(gallery)
                    }
                }

                galleryAdapter.setItems(galleryList)
                galleryAdapter.setOnItemClickCallback(object :
                    GalleryAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Gallery) {
                        val args = Bundle()
                        args.putString("id", data.id)
                        val newFragment: DialogFragment = DetailGaleriFragment()
                        newFragment.arguments = args
                        newFragment.show(supportFragmentManager, "TAG")
                    }
                })
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvVendorGaleri
        recyclerView.layoutManager = GridLayoutManager(this,2)

        galleryAdapter = GalleryAdapter(this)
        recyclerView.adapter = galleryAdapter
        galleryAdapter.setItems(galleryList)
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }

        binding.cvNasiKotak.setOnClickListener {
            val intent = Intent(this, VendorMenuActivity::class.java)
            intent.putExtra(VendorMenuActivity.EXTRA_JENIS, getString(R.string.nasi_kotak))
            intent.putExtra(VendorMenuActivity.EXTRA_FOTO, foto)
            intent.putExtra(VendorMenuActivity.EXTRA_NAMA, nama)
            intent.putExtra(VendorMenuActivity.EXTRA_ID, vendorId)
            startActivity(intent)
        }
        binding.cvTumpeng.setOnClickListener {
            val intent = Intent(this, VendorMenuActivity::class.java)
            intent.putExtra(VendorMenuActivity.EXTRA_JENIS, getString(R.string.tumpeng))
            intent.putExtra(VendorMenuActivity.EXTRA_FOTO, foto)
            intent.putExtra(VendorMenuActivity.EXTRA_NAMA, nama)
            intent.putExtra(VendorMenuActivity.EXTRA_ID, vendorId)
            startActivity(intent)
        }
        binding.cvPrasmanan.setOnClickListener {
            val intent = Intent(this, VendorMenuActivity::class.java)
            intent.putExtra(VendorMenuActivity.EXTRA_JENIS, getString(R.string.prasmanan))
            intent.putExtra(VendorMenuActivity.EXTRA_FOTO, foto)
            intent.putExtra(VendorMenuActivity.EXTRA_NAMA, nama)
            intent.putExtra(VendorMenuActivity.EXTRA_ID, vendorId)
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}