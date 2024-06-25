package com.valdoang.kateringconnect.view.user.main.ui.beranda

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.TextView
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
import com.valdoang.kateringconnect.model.Vendor
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.both.chat.ChatActivity
import com.valdoang.kateringconnect.view.user.detailvendor.DetailVendorActivity

class UserBerandaFragment : Fragment() {

    private var _binding: FragmentUserBerandaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userKota = ""
    private var addKota = ""
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

        setupAction()
        setupView()
        setupData()

        return root
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val userId = firebaseAuth.currentUser!!.uid
        db.collection("user").document(userId)
            .addSnapshotListener{ document,_ ->
                progressBar.visibility = View.GONE
                if (document != null) {
                    userKota = document.data?.get("kota").toString()
                    val vendorRef = db.collection("user").whereEqualTo("jenis", "Vendor").whereIn("kota", listOf(userKota, addKota) )
                    vendorRef.addSnapshotListener{ snapshot,_ ->
                        if (snapshot != null) {
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
                                    intent.putExtra(Cons.EXTRA_ID, data.id)
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
                setupData()
                dialog.dismiss()
            }

            tvBatalkan.setOnClickListener {
                acCity.setText(null, false)
                addKota = acCity.text.toString().trim()
                setupData()
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()
        }

        binding.ibChat.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}