package com.valdoang.kateringconnect.view.vendor.galeri

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentAlertDialogBinding

class DeleteGaleriFragment: DialogFragment() {

    private var _binding: FragmentAlertDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAlertDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        storageRef = FirebaseStorage.getInstance()

        val mArgs = arguments
        val galleryId = mArgs!!.getString("id")

        setTvText()
        closeDialog()
        deleteData(galleryId!!)

        return root
    }

    private fun setTvText() {
        binding.tvAlert.text = resources.getString(R.string.delete_photo_alert)
    }

    private fun closeDialog() {
        binding.btnNo.setOnClickListener{
            dismiss()
        }
    }

    private fun deleteData(galleryId: String) {
        binding.btnYes.setOnClickListener{
            val galeriRef = db.collection("user").document(userId).collection("galeri").document(galleryId)
            galeriRef.get().addOnSuccessListener {
                if (it != null) {
                    val storageKeys = it.data?.get("storageKeys").toString()
                    galeriRef.delete()
                    storageRef.getReference("vendorGallery").child(userId).child(storageKeys).delete()
                        .addOnSuccessListener {
                            dismiss()
                            Toast.makeText(requireContext(), R.string.success_delete_photo, Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}