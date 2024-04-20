package com.valdoang.kateringconnect.view.user.main.ui.akun

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentUserAkunBinding
import com.valdoang.kateringconnect.view.both.akun.EditAkunActivity
import com.valdoang.kateringconnect.view.both.login.LoginActivity

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

                Glide.with(activity!!).load(foto).error(R.drawable.default_profile).into(ivUserAkun)
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
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(resources.getString(R.string.keluar_title))
            builder.setMessage(resources.getString(R.string.keluar_message))
            builder.setPositiveButton(resources.getString(R.string.keluar)) { _, _ ->
                firebaseAuth.signOut()
                Intent(activity, LoginActivity::class.java).also { intent ->
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity?.startActivity(intent)
                }
                Toast.makeText(activity, R.string.success_signout, Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton(resources.getString(R.string.batal)) { dialog, _ ->
                dialog.dismiss()
            }
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}