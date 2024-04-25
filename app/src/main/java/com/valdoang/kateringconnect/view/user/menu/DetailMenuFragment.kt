package com.valdoang.kateringconnect.view.user.menu

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.databinding.FragmentDetailMenuBinding
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.view.user.PemesananActivity

class DetailMenuFragment : DialogFragment() {
    private var _binding: FragmentDetailMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private lateinit var ivFoto: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvPrice: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        ivFoto = binding.ivMenu
        tvName = binding.tvMenuName
        tvDesc = binding.tvMenuDesc
        tvPrice = binding.tvMenuPrice

        val mArgs = arguments
        val menuId = mArgs!!.getString("id")

        setupMenu(menuId!!)
        pesanMenu(menuId)
        setupAction()

        return root
    }

    private fun setupMenu(menuId: String) {
        val ref = db.collection("menu").document(menuId)
        ref.get().addOnSuccessListener { document ->
            if (document != null) {
                val foto = document.data?.get("foto").toString()
                val nama = document.data?.get("nama").toString()
                val desc = document.data?.get("keterangan").toString()
                val price = document.data?.get("harga").toString()
                Glide.with(activity!!).load(foto).into(ivFoto)
                tvName.text = nama
                tvDesc.text = desc
                tvPrice.text = price.withNumberingFormat()
            }
        }
    }

    private fun pesanMenu(menuId: String) {
        binding.btnPesan.setOnClickListener {
            val intent = Intent(requireContext(), PemesananActivity::class.java)
            intent.putExtra(PemesananActivity.EXTRA_ID, menuId)
            startActivity(intent)
        }
    }

    private fun setupAction() {
        binding.ibClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}