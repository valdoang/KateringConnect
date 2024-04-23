package com.valdoang.kateringconnect.view.vendor.menu

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentEditMenuBinding


class EditMenuFragment : DialogFragment() {
    private var _binding: FragmentEditMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private lateinit var ivFoto: ImageView
    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var etPrice: EditText
    private lateinit var acJenis: AutoCompleteTextView
    private var storageKeys = ""
    private var jenis = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        storageRef = FirebaseStorage.getInstance()

        //ivFoto = binding.ivFoto
        etName = binding.edAddName
        etDesc = binding.edAddDesc
        etPrice = binding.edAddPrice
        acJenis = binding.acAddJenis

        val mArgs = arguments
        val menuId = mArgs!!.getString("id")

        setData(menuId!!)
        saveData()
        deleteData()
        closeDialog()

        return root
    }

    private fun setData(menuId: String) {
        db.collection("menu").document(menuId).get()
            .addOnSuccessListener {document ->
                if (document != null) {
                    //val foto = document.data?.get("foto").toString()
                    val nama = document.data?.get("nama").toString()
                    val desc = document.data?.get("keterangan").toString()
                    val price = document.data?.get("harga").toString()
                    jenis = document.data?.get("jenis").toString()
                    //storageKeys = document.data?.get("storageKeys").toString()
                    //Glide.with(activity!!).load(foto).error(R.drawable.galeri).into(ivFoto)
                    etName.setText(nama)
                    etDesc.setText(desc)
                    etPrice.setText(price)
                    acJenis.setText(jenis, false)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveData() {
        binding.btnSimpan.setOnClickListener{
            dismiss()
        }
    }

    private fun deleteData() {
        binding.btnHapus.setOnClickListener{
            dismiss()
        }
    }

    private fun closeDialog() {
        binding.ibClose.setOnClickListener{
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}