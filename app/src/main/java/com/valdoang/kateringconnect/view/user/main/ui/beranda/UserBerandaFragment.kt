package com.valdoang.kateringconnect.view.user.main.ui.beranda

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.UserBerandaAdapter
import com.valdoang.kateringconnect.databinding.FragmentUserBerandaBinding
import com.valdoang.kateringconnect.model.Star
import com.valdoang.kateringconnect.model.Vendor
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.both.chat.ChatActivity
import com.valdoang.kateringconnect.view.user.alamat.AlamatActivity
import com.valdoang.kateringconnect.view.user.detailvendor.DetailVendorActivity
import com.valdoang.kateringconnect.view.user.keranjang.AllKeranjangActivity
import java.util.stream.Collectors
import kotlin.math.roundToLong

class UserBerandaFragment : Fragment() {

    private var _binding: FragmentUserBerandaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userKota = ""
    private var userAlamat = ""
    private var addKota = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var vendorList: ArrayList<Vendor>
    private lateinit var userBerandaAdapter: UserBerandaAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var kategoriMenuList: ArrayList<String>
    private var starList: ArrayList<Star> = ArrayList()
    private var totalNilai = 0.0
    private var alamatId: String? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBerandaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = Firebase.auth
        vendorList = arrayListOf()
        kategoriMenuList = arrayListOf()
        progressBar = binding.progressBar
        searchView = binding.searchBar

        setupTv()
        setupAction()
        setupView()
        setupDataUser()
        searchView()

        return root
    }

    private fun searchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                progressBar.visibility = View.VISIBLE
                val filteredVendor = vendorList.filter {
                    it.nama!!.contains(query!!, ignoreCase = true) || it.kategoriMenu!!.contains(query, ignoreCase = true)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    progressBar.visibility = View.GONE
                    userBerandaAdapter.setItems(filteredVendor)
                }, 500)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == "") {
                    progressBar.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        progressBar.visibility = View.GONE
                        userBerandaAdapter.setItems(vendorList)
                    }, 500)
                }
                return false
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupDataUser() {
        val userId = firebaseAuth.currentUser!!.uid
        db.collection("user").document(userId)
            .addSnapshotListener{ document,_ ->
                if (document != null) {
                    userKota = document.data?.get("kota").toString()
                    userAlamat = document.data?.get("alamat").toString()
                    setupDataVendor()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupDataVendor() {
        progressBar.visibility = View.VISIBLE
        val vendorRef = db.collection("user").whereEqualTo("jenis", "Vendor").whereIn("kota", listOf(userKota, addKota) )
        vendorRef.addSnapshotListener{ snapshot,_ ->
            if (snapshot != null) {
                progressBar.visibility = View.GONE
                vendorList.clear()
                for (data in snapshot.documents) {
                    val vendor: Vendor? = data.toObject(Vendor::class.java)
                    if (vendor != null) {
                        vendor.id = data.id
                        vendorList.add(vendor)
                    }
                }

                for (i in vendorList) {
                    val kategoriMenuRef = db.collection("user").document(i.id!!).collection("kategoriMenu")
                    kategoriMenuRef.addSnapshotListener { kategoriSnapshot,_ ->
                        if (kategoriSnapshot != null) {
                            kategoriMenuList.clear()
                            for (data in kategoriSnapshot.documents) {
                                val kategoriMenu: String = data.get("nama").toString()
                                kategoriMenuList.add(kategoriMenu)
                            }
                            kategoriMenuList.sortBy{ kategori ->
                                kategori
                            }
                            val sKategori = kategoriMenuList.stream().collect(
                                Collectors.joining(", ")
                            )
                            i.kategoriMenu = sKategori
                        }
                    }

                    //Hitung Ongkos Kirim
                    val coder = Geocoder(requireContext())
                    try {
                        val userAddress : List<Address> = coder.getFromLocationName(userAlamat,5)!!
                        val userLocation = userAddress[0]
                        val userLat = userLocation.latitude
                        val userLon = userLocation.longitude

                        val vendorAddress : List<Address> = coder.getFromLocationName(i.alamat!!,5)!!
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

                        i.ongkir = ongkir

                    } catch (e: Exception) {
                        Log.d(ContentValues.TAG, e.localizedMessage as String)
                    }

                    val nilaiRef = db.collection("nilai").whereEqualTo("vendorId", i.id)
                    nilaiRef.addSnapshotListener { nilaiSnapshot,_ ->
                        if (nilaiSnapshot != null) {
                            starList.clear()
                            totalNilai = 0.0
                            for (data in nilaiSnapshot.documents) {
                                val star: Star? = data.toObject(Star::class.java)
                                if (star != null) {
                                    starList.add(star)
                                }
                            }

                            for (j in starList) {
                                val nilai = j.nilai?.toDouble()
                                if (nilai != null) {
                                    totalNilai += nilai
                                }
                            }

                            val sizeNilai = starList.size
                            val nilaiStar = totalNilai/sizeNilai

                            i.sizeNilai = sizeNilai
                            i.nilai = nilaiStar

                            vendorList.sortBy { vendor ->
                                vendor.ongkir
                            }

                            userBerandaAdapter.setItems(vendorList)

                            userBerandaAdapter.setOnItemClickCallback(object :
                                UserBerandaAdapter.OnItemClickCallback {
                                override fun onItemClicked(data: Vendor) {
                                    val intent = Intent(requireContext(), DetailVendorActivity::class.java)
                                    intent.putExtra(Cons.EXTRA_ID, data.id)
                                    intent.putExtra(Cons.EXTRA_SEC_ID, alamatId.toString())
                                    intent.putExtra(Cons.EXTRA_ONGKIR, data.ongkir.toString())
                                    startActivity(intent)
                                }
                            })

                            if (vendorList.isEmpty()) {
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

            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvUserBeranda
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        userBerandaAdapter = UserBerandaAdapter(requireContext())
        recyclerView.adapter = userBerandaAdapter
        userBerandaAdapter.setItems(vendorList)
    }

    private fun setupTv() {
        binding.tvAlamatUser.text = getString(R.string.rumah)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupAction() {
        binding.ibCity.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.bottom_sheet_add_city, null)

            val acCity = view.findViewById<AutoCompleteTextView>(R.id.ac_add_city)

            val cities = resources.getStringArray(R.array.Cities)
            val dropdownAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, cities)
            acCity.setAdapter(dropdownAdapter)

            acCity.setText(addKota, false)

            val tvTampilkan = view.findViewById<TextView>(R.id.tv_tampilkan)
            val tvBatalkan = view.findViewById<TextView>(R.id.tv_batalkan)

            if (addKota != "") {
                tvBatalkan.visibility = View.VISIBLE
            }
            else {
                tvBatalkan.visibility = View.GONE
            }

            tvTampilkan.setOnClickListener {
                addKota = acCity.text.toString().trim()
                setupDataVendor()
                dialog.dismiss()
            }

            tvBatalkan.setOnClickListener {
                acCity.setText(null, false)
                addKota = acCity.text.toString().trim()
                setupDataVendor()
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()
        }

        binding.clAntarKe.setOnClickListener {
            val intent = Intent(requireContext(), AlamatActivity::class.java)
            startActivityForResult(intent, Cons.EXTRA_INT)
        }

        binding.ibChat.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            startActivity(intent)
        }

        binding.cvKeranjang.setOnClickListener {
            val intent = Intent(requireContext(), AllKeranjangActivity::class.java)
            intent.putExtra(Cons.EXTRA_ID, alamatId.toString())
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Cons.EXTRA_INT && resultCode == Activity.RESULT_OK) {
            alamatId = data?.getStringExtra(Cons.EXTRA_ID)
            val nama = data?.getStringExtra(Cons.EXTRA_NAMA)
            val kota = data?.getStringExtra(Cons.EXTRA_KOTA)
            val alamat = data?.getStringExtra(Cons.EXTRA_ALAMAT)
            userKota = kota!!
            userAlamat = alamat!!
            setupDataVendor()
            binding.tvAlamatUser.text = nama
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}