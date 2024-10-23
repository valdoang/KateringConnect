package com.valdoang.kateringconnect.view.both.detailgaleri

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentDetailGaleriBinding
import com.valdoang.kateringconnect.view.vendor.galeri.DeleteGaleriFragment

class DetailGaleriFragment : DialogFragment() {
    private var _binding: FragmentDetailGaleriBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private lateinit var ivFoto: ImageView
    private lateinit var btnDeletePhoto: Button
    private var storageKeys = ""
    private var userJenis = ""
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailGaleriBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        firebaseAuth = Firebase.auth
        storageRef = FirebaseStorage.getInstance()
        userId = firebaseAuth.currentUser!!.uid

        ivFoto = binding.ivFoto
        btnDeletePhoto = binding.btnDeletePhoto

        val mArgs = arguments
        val galleryId = mArgs!!.getString("galleryId")
        val vendorId = mArgs.getString("vendorId")

        setCv()
        setData(galleryId!!, vendorId!!)
        deleteData(galleryId)


        return root
    }

    private fun setCv() {
        db.collection("user").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userJenis = document.data?.get("jenis").toString()
                    if (userJenis == getString(R.string.pembeli)) {
                        btnDeletePhoto.visibility = View.GONE
                    }
                }
            }
    }

    private fun setData(galleryId: String, vendorId: String) {
        val galeriRef = db.collection("user").document(vendorId).collection("galeri").document(galleryId)
        galeriRef.get().addOnSuccessListener {document ->
            if (document != null) {
                val foto = document.data?.get("foto").toString()
                storageKeys = document.data?.get("storageKeys").toString()
                Glide.with(activity!!).load(foto).error(R.drawable.gallery).into(ivFoto)
            }
        }
        .addOnFailureListener {
            Toast.makeText(requireContext(), "Gagal!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteData(galleryId: String) {
        btnDeletePhoto.setOnClickListener {
            val args = Bundle()
            args.putString("id", galleryId)
            val newFragment: DialogFragment = DeleteGaleriFragment()
            newFragment.arguments = args
            newFragment.show(parentFragmentManager, "TAG")
            dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}