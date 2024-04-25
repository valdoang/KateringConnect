package com.valdoang.kateringconnect.view.vendor.galeri

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
import com.valdoang.kateringconnect.view.both.alertdialog.DeleteGalleryFragment
import com.valdoang.kateringconnect.view.both.menu.MenuActivity

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

        ivFoto = binding.ivFoto
        btnDeletePhoto = binding.btnDeletePhoto

        val mArgs = arguments
        val galleryId = mArgs!!.getString("id")

        setCv()
        setData(galleryId!!)
        deleteData(galleryId)


        return root
    }

    private fun setCv() {
        val userId = firebaseAuth.currentUser!!.uid
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

    private fun setData(galleryId: String) {
        Log.d("KODE GALLERY ID", galleryId)
        db.collection("gallery").document(galleryId).get()
            .addOnSuccessListener {document ->
                if (document != null) {
                    val foto = document.data?.get("foto").toString()
                    storageKeys = document.data?.get("storageKeys").toString()
                    Glide.with(activity!!).load(foto).error(R.drawable.galeri).into(ivFoto)
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
            val newFragment: DialogFragment = DeleteGalleryFragment()
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