package com.valdoang.kateringconnect.view.user.alamat

import android.content.Intent
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
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentAlertDialogBinding
import com.valdoang.kateringconnect.model.GrupOpsi
import com.valdoang.kateringconnect.model.Menu
import com.valdoang.kateringconnect.view.vendor.menu.MenuActivity


class DeleteAlamatFragment : DialogFragment() {

    private var _binding: FragmentAlertDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private var userId = ""
    private var alamatId: String? = null
    private lateinit var acGrupOpsiList: ArrayList<GrupOpsi>
    private lateinit var menuList: ArrayList<Menu>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAlertDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser!!.uid
        acGrupOpsiList = arrayListOf()
        menuList = arrayListOf()

        val mArgs = arguments
        alamatId = mArgs!!.getString("alamatId")

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        setTvText()
        closeDialog()
        deleteKategori()

        return root
    }

    private fun setTvText() {
        binding.tvAlert.text = resources.getString(R.string.delete_alamat_alert)
    }

    private fun closeDialog() {
        binding.btnNo.setOnClickListener{
            dismiss()
        }
    }

    private fun deleteKategori() {
        binding.btnYes.setOnClickListener {
            Intent(activity, AlamatActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                activity?.startActivity(intent)
            }
            val alamatRef = db.collection("user").document(userId).collection("alamatTersimpan").document(alamatId!!)
            alamatRef.delete()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}