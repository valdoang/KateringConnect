package com.valdoang.kateringconnect.view.user.keranjang

import android.content.ContentValues
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.AllKeranjangAdapter
import com.valdoang.kateringconnect.databinding.ActivityAllKeranjangBinding
import com.valdoang.kateringconnect.model.AllKeranjang
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.roundOffDecimal
import com.valdoang.kateringconnect.view.user.pemesanan.PemesananActivity
import kotlin.math.roundToLong

class AllKeranjangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllKeranjangBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var allKeranjangAdapter: AllKeranjangAdapter
    private var allKeranjangList: ArrayList<AllKeranjang> = ArrayList()
    private var db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var userId = ""
    private var alamatId: String? = null
    private lateinit var tvEdit: TextView
    private lateinit var clEdit: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var btnHapus: Button
    private lateinit var cbPilihSemua: CheckBox
    private var arrayVendorId: ArrayList<String> = ArrayList()
    private var alamatUser = ""
    private var alamatVendor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllKeranjangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        
        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid

        alamatId = intent.getStringExtra(Cons.EXTRA_ID)
        tvEdit = binding.tvEdit
        clEdit = binding.clEdit
        progressBar = binding.progressBar
        btnHapus = binding.btnHapus
        cbPilihSemua = binding.cbPilihSemua

        setupAction()
        setupView(false)
        setupAllKeranjang()
        setupEditKeranjang()
        deleteKeranjang()
    }
    
    private fun setupAllKeranjang() {
        progressBar.visibility = View.VISIBLE
        val allKeranjangRef = db.collection("user").document(userId).collection("keranjang")
        allKeranjangRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                allKeranjangList.clear()
                for (data in snapshot.documents) {
                    val allKeranjang: AllKeranjang? = data.toObject(AllKeranjang::class.java)
                    if (allKeranjang != null) {
                        allKeranjang.vendorId = data.id
                        allKeranjangList.add(allKeranjang)

                        if (alamatId != userId && alamatId != "null") {
                            val alamatRef = db.collection("user").document(userId).collection("alamatTersimpan").document(alamatId!!)
                            alamatRef.get().addOnSuccessListener { alamatSnapshot ->
                                if (alamatSnapshot != null) {
                                    alamatUser = alamatSnapshot.data?.get("alamat").toString()
                                    val vendorRef = db.collection("user").document(data.id)
                                    vendorRef.get().addOnSuccessListener {vendorSnapshot ->
                                        progressBar.visibility = View.GONE
                                        if (vendorSnapshot != null) {
                                            alamatVendor = vendorSnapshot.data?.get("alamat").toString()

                                            //Hitung Ongkos Kirim
                                            val coder = Geocoder(this)
                                            try {
                                                val userAddress : List<Address> = coder.getFromLocationName(alamatUser,5)!!
                                                val userLocation = userAddress[0]
                                                val userLat = userLocation.latitude
                                                val userLon = userLocation.longitude

                                                val vendorAddress : List<Address> = coder.getFromLocationName(alamatVendor,5)!!
                                                val vendorLocation = vendorAddress[0]
                                                val vendorLat = vendorLocation.latitude
                                                val vendorLon = vendorLocation.longitude

                                                val userPoint = Location("locationA")
                                                userPoint.latitude = userLat
                                                userPoint.longitude = userLon

                                                val vendorPoint = Location("locationB")
                                                vendorPoint.latitude = vendorLat
                                                vendorPoint.longitude = vendorLon

                                                val jarak = userPoint.distanceTo(vendorPoint) / 1000

                                                val ongkir = jarak.roundToLong() * 3000
                                                allKeranjang.ongkir = ongkir.toString()
                                                allKeranjang.jarak = jarak.toDouble().roundOffDecimal()

                                                allKeranjangAdapter.setItems(allKeranjangList)
                                                allKeranjangAdapter.setOnItemClickCallback(object : AllKeranjangAdapter.OnItemClickCallback{
                                                    override fun onItemClicked(data: AllKeranjang) {
                                                        val intent = Intent(this@AllKeranjangActivity, PemesananActivity::class.java)
                                                        intent.putExtra(Cons.EXTRA_ID, data.vendorId)
                                                        intent.putExtra(Cons.EXTRA_SEC_ID, alamatId)
                                                        intent.putExtra(Cons.EXTRA_ONGKIR, data.ongkir)
                                                        startActivity(intent)
                                                    }
                                                })

                                            } catch (e: Exception) {
                                                Log.d(ContentValues.TAG, e.localizedMessage as String)
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            val userRef = db.collection("user").document(userId)
                            userRef.get().addOnSuccessListener { userSnapshot ->
                                if (userSnapshot != null) {
                                    alamatUser = userSnapshot.data?.get("alamat").toString()
                                    val vendorRef = db.collection("user").document(data.id)
                                    vendorRef.get().addOnSuccessListener {vendorSnapshot ->
                                        progressBar.visibility = View.GONE
                                        if (vendorSnapshot != null) {
                                            alamatVendor = vendorSnapshot.data?.get("alamat").toString()

                                            //Hitung Ongkos Kirim
                                            val coder = Geocoder(this)
                                            try {
                                                val userAddress : List<Address> = coder.getFromLocationName(alamatUser,5)!!
                                                val userLocation = userAddress[0]
                                                val userLat = userLocation.latitude
                                                val userLon = userLocation.longitude

                                                val vendorAddress : List<Address> = coder.getFromLocationName(alamatVendor,5)!!
                                                val vendorLocation = vendorAddress[0]
                                                val vendorLat = vendorLocation.latitude
                                                val vendorLon = vendorLocation.longitude

                                                val userPoint = Location("locationA")
                                                userPoint.latitude = userLat
                                                userPoint.longitude = userLon

                                                val vendorPoint = Location("locationB")
                                                vendorPoint.latitude = vendorLat
                                                vendorPoint.longitude = vendorLon

                                                val jarak = userPoint.distanceTo(vendorPoint) / 1000

                                                val ongkir = jarak.roundToLong() * 3000
                                                allKeranjang.ongkir = ongkir.toString()
                                                allKeranjang.jarak = jarak.toDouble().roundOffDecimal()

                                                allKeranjangAdapter.setItems(allKeranjangList)
                                                allKeranjangAdapter.setOnItemClickCallback(object : AllKeranjangAdapter.OnItemClickCallback{
                                                    override fun onItemClicked(data: AllKeranjang) {
                                                        val intent = Intent(this@AllKeranjangActivity, PemesananActivity::class.java)
                                                        intent.putExtra(Cons.EXTRA_ID, data.vendorId)
                                                        intent.putExtra(Cons.EXTRA_SEC_ID, alamatId)
                                                        intent.putExtra(Cons.EXTRA_ONGKIR, data.ongkir)
                                                        startActivity(intent)
                                                    }
                                                })

                                            } catch (e: Exception) {
                                                Log.d(ContentValues.TAG, e.localizedMessage as String)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (snapshot.size() == 0) {
                    progressBar.visibility = View.GONE
                    binding.noDataAnimation.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.VISIBLE
                    tvEdit.visibility = View.GONE
                    allKeranjangAdapter.setItems(allKeranjangList)
                    binding.tvTemukanSekarang.visibility = View.VISIBLE
                }
                else {
                    progressBar.visibility = View.GONE
                    binding.noDataAnimation.visibility = View.GONE
                    binding.tvNoData.visibility = View.GONE
                }
            }
        }
    }

    private fun setupEditKeranjang() {
        val checkListener =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                cbPilihSemua.setOnClickListener {
                    if (isChecked) {
                        for (i in allKeranjangList) {
                            arrayVendorId.add(i.vendorId!!)
                        }
                        setupView(true)
                        btnHapus.text = getString(R.string.hapus_jumlah_keranjang, allKeranjangList.size.toString())
                        btnHapus.isEnabled = true
                    } else {
                        arrayVendorId.clear()
                        setupView(true)
                        btnHapus.text = getString(R.string.hapus)
                        btnHapus.isEnabled = false
                    }
                }
            }

        cbPilihSemua.setOnCheckedChangeListener(checkListener)
        tvEdit.setOnClickListener {
            when (tvEdit.text) {
                getString(R.string.atur) -> {
                    tvEdit.text = getString(R.string.batalkan)
                    setupView(true)
                    clEdit.visibility = View.VISIBLE
                }
                getString(R.string.batalkan) -> {
                    tvEdit.text = getString(R.string.atur)
                    setupView(false)
                    allKeranjangAdapter.setOnItemClickCallback(object : AllKeranjangAdapter.OnItemClickCallback{
                        override fun onItemClicked(data: AllKeranjang) {
                            val intent = Intent(this@AllKeranjangActivity, PemesananActivity::class.java)
                            intent.putExtra(Cons.EXTRA_ID, data.vendorId)
                            intent.putExtra(Cons.EXTRA_FOURTH_ID, alamatId)
                            intent.putExtra(Cons.EXTRA_ONGKIR, data.ongkir)
                            startActivity(intent)
                        }

                    })
                    clEdit.visibility = View.GONE
                }
            }

        }
    }

    private fun deleteKeranjang() {
        btnHapus.setOnClickListener {
            val args = Bundle()
            args.putStringArrayList("arrayVendorId", arrayVendorId)
            args.putString("alamatId", alamatId)
            val dialog = DeleteKeranjangFragment()
            dialog.arguments = args
            dialog.show(supportFragmentManager, "deleteKeranjangDialog")
        }
    }
    
    private fun setupView(edit: Boolean) {
        recyclerView = binding.rvKeranjang
        recyclerView.layoutManager = LinearLayoutManager(this)
        allKeranjangAdapter = AllKeranjangAdapter(this, edit, arrayVendorId, btnHapus, cbPilihSemua)
        recyclerView.adapter = allKeranjangAdapter
        allKeranjangAdapter.setItems(allKeranjangList)
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
        binding.tvTemukanSekarang.setOnClickListener {
            finish()
        }
    }
}