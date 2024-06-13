package com.valdoang.kateringconnect.view.vendor.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.adapter.GrupOpsiAdapter
import com.valdoang.kateringconnect.databinding.FragmentGrupOpsiBinding
import com.valdoang.kateringconnect.model.GrupOpsi

class GrupOpsiFragment : Fragment() {

    private var _binding: FragmentGrupOpsiBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var grupOpsiAdapter: GrupOpsiAdapter
    private lateinit var grupOpsiList: ArrayList<GrupOpsi>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGrupOpsiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = Firebase.auth
        grupOpsiList = arrayListOf()

        setupView()
        setupData()
        setupAction()

        return root
    }

    private fun setupView() {
        recyclerView = binding.rvGrupOpsi
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        grupOpsiAdapter = GrupOpsiAdapter(requireContext())
        recyclerView.adapter = grupOpsiAdapter
        grupOpsiAdapter.setItems(grupOpsiList)
    }

    private fun setupData() {
        val userId = firebaseAuth.currentUser!!.uid
        val ref = db.collection("user").document(userId).collection("grupOpsi")
        ref.addSnapshotListener{ snapshot,_ ->
            if (snapshot != null) {
                grupOpsiList.clear()
                for (data in snapshot.documents) {
                    val grupOpsi: GrupOpsi? = data.toObject(GrupOpsi::class.java)
                    if (grupOpsi != null) {
                        grupOpsi.id = data.id
                        grupOpsiList.add(grupOpsi)
                    }
                }

                grupOpsiAdapter.setItems(grupOpsiList)
            }
        }
    }

    private fun setupAction() {
        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddGrupOpsiActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}