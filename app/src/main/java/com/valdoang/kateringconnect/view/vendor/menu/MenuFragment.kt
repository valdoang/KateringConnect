package com.valdoang.kateringconnect.view.vendor.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.KategoriVendorMenuAdapter
import com.valdoang.kateringconnect.databinding.FragmentMenuBinding
import com.valdoang.kateringconnect.model.KategoriMenu
import com.valdoang.kateringconnect.view.vendor.menu.kategori.AddKategoriActivity

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var kategoriMenuList: ArrayList<KategoriMenu>
    private lateinit var kategoriVendorMenuAdapter: KategoriVendorMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = Firebase.auth
        kategoriMenuList = arrayListOf()

        setupAction()
        setupView()
        setupData()

        return root
    }

    private fun setupAction() {
        binding.btnAdd.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.bottom_sheet_menu_kategori, null)

            val clHidangan = view.findViewById<ConstraintLayout>(R.id.cl_hidangan)
            val clKategori = view.findViewById<ConstraintLayout>(R.id.cl_kategori)

            clHidangan.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(requireContext(), AddMenuActivity::class.java)
                startActivity(intent)
            }
            clKategori.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(requireContext(), AddKategoriActivity::class.java)
                startActivity(intent)
            }

            dialog.setContentView(view)
            dialog.show()
        }
    }

    private fun setupView() {
        recyclerView = binding.rvMenu
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        kategoriVendorMenuAdapter = KategoriVendorMenuAdapter(requireContext())
        recyclerView.adapter = kategoriVendorMenuAdapter
        kategoriVendorMenuAdapter.setItems(kategoriMenuList)
    }

    private fun setupData() {
        val userId = firebaseAuth.currentUser!!.uid
        val ref = db.collection("user").document(userId).collection("kategoriMenu")
        ref.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                kategoriMenuList.clear()
                for (data in snapshot.documents) {
                    val kategoriMenu: KategoriMenu? = data.toObject(KategoriMenu::class.java)
                    if (kategoriMenu != null) {
                        kategoriMenu.id = data.id
                        kategoriMenuList.add(kategoriMenu)
                    }
                }
                kategoriMenuList.sortBy { kategori ->
                    kategori.nama
                }
                kategoriVendorMenuAdapter.setItems(kategoriMenuList)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}