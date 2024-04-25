package com.valdoang.kateringconnect.view.vendor.main.ui.akun

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.GalleryAdapter
import com.valdoang.kateringconnect.databinding.FragmentVendorAkunBinding
import com.valdoang.kateringconnect.model.Gallery
import com.valdoang.kateringconnect.view.vendor.galeri.AddGaleriActivity
import com.valdoang.kateringconnect.view.vendor.menu.AddMenuActivity
import com.valdoang.kateringconnect.view.both.akun.EditAkunActivity
import com.valdoang.kateringconnect.view.vendor.galeri.DetailGaleriFragment
import com.valdoang.kateringconnect.view.both.alertdialog.LogoutFragment
import com.valdoang.kateringconnect.view.both.menu.MenuActivity

class VendorAkunFragment : Fragment() {

    private var _binding: FragmentVendorAkunBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private val binding get() = _binding!!
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        
        _binding = FragmentVendorAkunBinding.inflate(inflater, container, false)
        val root: View = binding.root
        firebaseAuth = FirebaseAuth.getInstance()

        tvName = binding.tvVendorAkunName
        tvCity = binding.tvCity
        tvAddress = binding.tvAddress
        tvNoPhone = binding.tvNoPhone
        ivVendorAkun = binding.ivVendorAkun

        galleryList = arrayListOf()

        setupAccount()
        setupView()
        setupAction()
        hideUI()
        return root
    }

    private fun setupAccount() {
        val userId = firebaseAuth.currentUser!!.uid
        val userRef = db.collection("user").document(userId)
        userRef.addSnapshotListener { document,_ ->
            if (document != null) {
                foto = document.data?.get("foto").toString()
                nama = document.data?.get("nama").toString()
                val kota = document.data?.get("kota").toString()
                val alamat = document.data?.get("alamat").toString()
                val telepon = document.data?.get("telepon").toString()

                Glide.with(activity!!).load(foto).error(R.drawable.default_vendor_profile).into(ivVendorAkun)
                tvName.text = nama
                tvCity.text = kota
                tvAddress.text = alamat
                tvNoPhone.text = telepon
            }
        }

        val galleryRef = db.collection("gallery").whereEqualTo("userId", userId)
        galleryRef.addSnapshotListener{ snapshot,_ ->
            if (snapshot != null) {
                Log.d(TAG, "Current data: ${snapshot.documents}")
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
                        newFragment.show(parentFragmentManager, "TAG")
                    }
                })
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvVendorGaleri
        recyclerView.layoutManager = GridLayoutManager(requireContext(),2)

        galleryAdapter = GalleryAdapter(requireContext())
        recyclerView.adapter = galleryAdapter
        galleryAdapter.setItems(galleryList)
    }

    private fun setupAction() {
        binding.btnVendorEditAkun.setOnClickListener{
            val intent = Intent(requireContext(), EditAkunActivity::class.java)
            startActivity(intent)
        }
        binding.btnVendorLogout.setOnClickListener{
            val dialog = LogoutFragment()
            dialog.show(this.parentFragmentManager, "logoutDialog")
        }
        binding.btnAddMenu.setOnClickListener {
            val intent = Intent(requireContext(), AddMenuActivity::class.java)
            startActivity(intent)
        }
        binding.btnAddGaleri.setOnClickListener {
            val intent = Intent(requireContext(), AddGaleriActivity::class.java)
            startActivity(intent)
        }
        binding.cvNasiKotak.setOnClickListener {
            val intent = Intent(requireContext(), MenuActivity::class.java)
            intent.putExtra(MenuActivity.EXTRA_JENIS, getString(R.string.nasi_kotak))
            intent.putExtra(MenuActivity.EXTRA_FOTO, foto)
            intent.putExtra(MenuActivity.EXTRA_NAMA, nama)
            startActivity(intent)
        }
        binding.cvTumpeng.setOnClickListener {
            val intent = Intent(requireContext(), MenuActivity::class.java)
            intent.putExtra(MenuActivity.EXTRA_JENIS, getString(R.string.tumpeng))
            intent.putExtra(MenuActivity.EXTRA_FOTO, foto)
            intent.putExtra(MenuActivity.EXTRA_NAMA, nama)
            startActivity(intent)
        }
        binding.cvPrasmanan.setOnClickListener {
            val intent = Intent(requireContext(), MenuActivity::class.java)
            intent.putExtra(MenuActivity.EXTRA_JENIS, getString(R.string.prasmanan))
            intent.putExtra(MenuActivity.EXTRA_FOTO, foto)
            intent.putExtra(MenuActivity.EXTRA_NAMA, nama)
            startActivity(intent)
        }
    }

    private fun hideUI() {
        binding.ibBack.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}