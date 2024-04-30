package com.valdoang.kateringconnect.view.vendor.main.ui.riwayat

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.VendorBerandaRiwayatAdapter
import com.valdoang.kateringconnect.databinding.FragmentVendorRiwayatBinding
import com.valdoang.kateringconnect.model.Pesanan
import com.valdoang.kateringconnect.view.vendor.detailriwayat.DetailRiwayatPesananActivity

class VendorRiwayatFragment : Fragment() {

    private var _binding: FragmentVendorRiwayatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var pesananList: ArrayList<Pesanan>
    private lateinit var vendorBerandaRiwayatAdapter: VendorBerandaRiwayatAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentVendorRiwayatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = Firebase.auth
        pesananList = arrayListOf()
        progressBar = binding.progressBar

        setupView()
        setupData()

        return root
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val userId= firebaseAuth.currentUser!!.uid
        val ref = db.collection("pesanan").whereEqualTo("vendorId", userId).whereIn("status", listOf(getString(
            R.string.status_selesai), getString(R.string.status_batal)))
        ref.addSnapshotListener{ snapshot,_ ->
            progressBar.visibility = View.GONE
            if (snapshot != null) {
                pesananList.clear()
                for (data in snapshot.documents) {
                    val pesanan: Pesanan? = data.toObject(Pesanan::class.java)
                    if (pesanan != null) {
                        pesanan.id = data.id
                        pesananList.add(pesanan)
                    }
                }

                pesananList.sortByDescending { pesanan ->
                    pesanan.jadwal
                }

                vendorBerandaRiwayatAdapter.setItems(pesananList)
                vendorBerandaRiwayatAdapter.setOnItemClickCallback(object :
                    VendorBerandaRiwayatAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Pesanan) {
                        val intent = Intent(requireContext(), DetailRiwayatPesananActivity::class.java)
                        intent.putExtra(DetailRiwayatPesananActivity.EXTRA_ID, data.id)
                        startActivity(intent)
                    }
                })

                if (pesananList.isEmpty()) {
                    binding.noHistoryAnimationVendor.visibility = View.VISIBLE
                    binding.tvEmptyData.visibility = View.VISIBLE

                }
                else {
                    binding.noHistoryAnimationVendor.visibility = View.GONE
                    binding.tvEmptyData.visibility = View.GONE
                }
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvVendorRiwayat
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        vendorBerandaRiwayatAdapter = VendorBerandaRiwayatAdapter(requireContext())
        recyclerView.adapter = vendorBerandaRiwayatAdapter
        vendorBerandaRiwayatAdapter.setItems(pesananList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}