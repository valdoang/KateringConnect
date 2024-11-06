package com.valdoang.kateringconnect.view.vendor.main.ui.akun

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentVendorAkunBinding
import com.valdoang.kateringconnect.model.Gallery
import com.valdoang.kateringconnect.model.Star
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.roundOffDecimal
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.view.all.editakun.EditAkunActivity
import com.valdoang.kateringconnect.view.all.logout.LogoutFragment
import com.valdoang.kateringconnect.view.all.chat.ChatActivity
import com.valdoang.kateringconnect.view.all.kcwallet.KcwalletActivity
import com.valdoang.kateringconnect.view.all.nilai.NilaiActivity
import com.valdoang.kateringconnect.view.vendor.galeri.GaleriActivity
import com.valdoang.kateringconnect.view.vendor.menu.MenuActivity

class VendorAkunFragment : Fragment() {

    private var _binding: FragmentVendorAkunBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private val binding get() = _binding!!
    private lateinit var tvVendorStar: TextView
    private lateinit var tvName: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvNoPhone: TextView
    private lateinit var ivVendorAkun: ImageView
    private lateinit var starList: ArrayList<Star>
    private lateinit var galleryList: ArrayList<Gallery>
    private var foto: String? = null
    private var nama: String? = null
    private var vendorId = ""
    private var totalNilai = 0.0
    private lateinit var tvSaldo: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        
        _binding = FragmentVendorAkunBinding.inflate(inflater, container, false)
        val root: View = binding.root
        firebaseAuth = FirebaseAuth.getInstance()

        tvVendorStar = binding.tvVendorStar
        tvName = binding.tvVendorAkunName
        tvAddress = binding.tvAddress
        tvNoPhone = binding.tvNoPhone
        ivVendorAkun = binding.ivVendorAkun
        tvSaldo = binding.tvKcwallet

        starList = arrayListOf()
        galleryList = arrayListOf()

        setupAccount()
        setupAction()
        hideUI()
        return root
    }

    private fun setupAccount() {
        vendorId = firebaseAuth.currentUser!!.uid

        val nilaiRef = db.collection("nilai").whereEqualTo("vendorId", vendorId)
        nilaiRef.addSnapshotListener { snapshot,_ ->
            if (snapshot != null) {
                starList.clear()
                totalNilai = 0.0
                for (data in snapshot.documents) {
                    val star: Star? = data.toObject(Star::class.java)
                    if (star != null) {
                        starList.add(star)
                    }
                }

                for (i in starList) {
                    val nilai = i.nilai?.toDouble()
                    if (nilai != null) {
                        totalNilai += nilai
                    }
                }

                val sizeNilai = starList.size
                val nilaiStar = totalNilai/sizeNilai

                if (sizeNilai == 0) {
                    tvVendorStar.text = getString(R.string.tidak_ada_penilaian)
                }
                else {
                    tvVendorStar.text = getString(R.string.vendor_star, nilaiStar.roundOffDecimal(), sizeNilai.toString().withNumberingFormat())
                }
            }
        }

        val userRef = db.collection("user").document(vendorId)
        userRef.addSnapshotListener { document,_ ->
            if (document != null) {
                foto = document.data?.get("foto").toString()
                nama = document.data?.get("nama").toString()
                val kota = document.data?.get("kota").toString()
                val alamat = document.data?.get("alamat").toString()
                val telepon = document.data?.get("telepon").toString()
                var saldo = document.data?.get("saldo")
                if (saldo == null) {
                    saldo = "0"
                }
                tvSaldo.text = getString(R.string.rupiah_text, saldo.toString().withNumberingFormat())

                Glide.with(this).load(foto).error(R.drawable.default_vendor_profile).into(ivVendorAkun)
                tvName.text = nama
                tvAddress.text = getString(R.string.tv_address_city, alamat, kota)
                tvNoPhone.text = telepon
            }
        }
    }

    private fun setupAction() {
        binding.clKcwallet.setOnClickListener {
            val intent = Intent(requireContext(), KcwalletActivity::class.java)
            startActivity(intent)
        }
        binding.clStar.setOnClickListener {
            val intent = Intent(requireContext(), NilaiActivity::class.java)
            intent.putExtra(Cons.EXTRA_ID, vendorId)
            startActivity(intent)
        }
        binding.cvMenu.setOnClickListener {
            val intent = Intent(requireContext(), MenuActivity::class.java)
            startActivity(intent)
        }
        binding.cvGaleri.setOnClickListener {
            val intent = Intent(requireContext(), GaleriActivity::class.java)
            startActivity(intent)
        }
        binding.cvEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditAkunActivity::class.java)
            startActivity(intent)
        }
        binding.cvLogout.setOnClickListener {
            val dialog = LogoutFragment()
            dialog.show(this.parentFragmentManager, "logoutDialog")
        }
        binding.ibChat.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
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