package com.valdoang.kateringconnect.view.user.main.ui.beranda

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.adapter.UserBerandaAdapter
import com.valdoang.kateringconnect.databinding.FragmentUserBerandaBinding
import com.valdoang.kateringconnect.model.Vendor
import com.valdoang.kateringconnect.view.user.DetailVendorActivity

class UserBerandaFragment : Fragment() {

    private var _binding: FragmentUserBerandaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userKota = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var vendorList: ArrayList<Vendor>
    private lateinit var userBerandaAdapter: UserBerandaAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBerandaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = Firebase.auth
        vendorList = arrayListOf()
        progressBar = binding.progressBar

        setupView()
        setupData()

        return root
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val userId = firebaseAuth.currentUser!!.uid
        db.collection("user").document(userId).get()
            .addOnSuccessListener { document ->
                progressBar.visibility = View.GONE
                if (document != null) {
                    userKota = document.data?.get("kota").toString()
                    val vendorRef = db.collection("user").whereEqualTo("jenis", "Vendor").whereEqualTo("kota", userKota)
                    vendorRef.addSnapshotListener{ snapshot,_ ->
                        if (snapshot != null) {
                            Log.d(TAG,"Current data: ${snapshot.documents}")
                            vendorList.clear()
                            for (data in snapshot.documents) {
                                val vendor: Vendor? = data.toObject(Vendor::class.java)
                                if (vendor != null) {
                                    vendor.id = data.id
                                    vendorList.add(vendor)
                                }
                            }

                            userBerandaAdapter.setItems(vendorList)
                            userBerandaAdapter.setOnItemClickCallback(object :
                                UserBerandaAdapter.OnItemClickCallback {
                                override fun onItemClicked(data: Vendor) {
                                    val intent = Intent(requireContext(), DetailVendorActivity::class.java)
                                    intent.putExtra(DetailVendorActivity.EXTRA_ID, data.id)
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

    private fun setupView() {
        recyclerView = binding.rvUserBeranda
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        userBerandaAdapter = UserBerandaAdapter(requireContext())
        recyclerView.adapter = userBerandaAdapter
        userBerandaAdapter.setItems(vendorList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}