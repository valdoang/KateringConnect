package com.valdoang.kateringconnect.view.user.main.ui.akun

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
import com.valdoang.kateringconnect.databinding.FragmentUserAkunBinding
import com.valdoang.kateringconnect.view.both.akun.EditAkunActivity
import com.valdoang.kateringconnect.view.both.alertdialog.LogoutFragment
import com.valdoang.kateringconnect.view.both.chat.ChatActivity

class UserAkunFragment : Fragment() {

    private var _binding: FragmentUserAkunBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private val binding get() = _binding!!
    private lateinit var tvName: TextView
    private lateinit var tvCity: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvNoPhone: TextView
    private lateinit var ivUserAkun: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserAkunBinding.inflate(inflater, container, false)
        val root: View = binding.root
        firebaseAuth = FirebaseAuth.getInstance()

        tvName = binding.tvUserAkunName
        tvCity = binding.tvCity
        tvAddress = binding.tvAddress
        tvNoPhone = binding.tvNoPhone
        ivUserAkun= binding.ivUserAkun


        setupAccount()
        setupAction()
        return root
    }

    private fun setupAccount() {
        val userId = firebaseAuth.currentUser!!.uid
        val ref = db.collection("user").document(userId)
        ref.addSnapshotListener { document,_ ->
            if (document != null) {
                val foto = document.data?.get("foto").toString()
                val nama = document.data?.get("nama").toString()
                val kota = document.data?.get("kota").toString()
                val alamat = document.data?.get("alamat").toString()
                val telepon = document.data?.get("telepon").toString()

                Glide.with(this).load(foto).error(R.drawable.default_profile).into(ivUserAkun)
                tvName.text = nama
                tvCity.text = kota
                tvAddress.text = alamat
                tvNoPhone.text = telepon
            }
        }
    }

    private fun setupAction() {
        binding.btnUserEditAkun.setOnClickListener{
            val intent = Intent(requireContext(), EditAkunActivity::class.java)
            startActivity(intent)
        }
        binding.btnUserLogout.setOnClickListener{
            val dialog = LogoutFragment()
            dialog.show(this.parentFragmentManager, "logoutDialog")
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