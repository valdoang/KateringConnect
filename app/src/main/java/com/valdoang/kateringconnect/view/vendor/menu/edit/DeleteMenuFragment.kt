package com.valdoang.kateringconnect.view.vendor.menu.edit

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.valdoang.kateringconnect.view.both.login.LoginActivity
import com.valdoang.kateringconnect.view.vendor.menu.MenuActivity


class DeleteMenuFragment : DialogFragment() {

    private var _binding: FragmentAlertDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private lateinit var acGrupOpsiList: ArrayList<GrupOpsi>
    private var userId = ""
    private var kategoriMenuId: String? = null
    private var menuId: String? = null
    private var storageKeys: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAlertDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        acGrupOpsiList = arrayListOf()
        userId = firebaseAuth.currentUser!!.uid

        val mArgs = arguments
        kategoriMenuId = mArgs!!.getString("kategoriMenuId")
        menuId = mArgs.getString("menuId")
        storageKeys = mArgs.getString("storageKeys")

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        setTvText()
        closeDialog()
        deleteMenu()

        return root
    }

    private fun setTvText() {
        binding.tvAlert.text = resources.getString(R.string.delete_menu_alert)
    }

    private fun closeDialog() {
        binding.btnNo.setOnClickListener{
            dismiss()
        }
    }

    private fun deleteMenu() {
        val ref = db.collection("user").document(userId).collection("grupOpsi")
        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot != null) {
                acGrupOpsiList.clear()
                for (data in snapshot.documents) {
                    val acGrupOpsi: GrupOpsi? = data.toObject(GrupOpsi::class.java)
                    if (acGrupOpsi != null) {
                        acGrupOpsi.id = data.id
                        acGrupOpsiList.add(acGrupOpsi)
                    }
                }
            }
        }

        binding.btnYes.setOnClickListener{
            Intent(activity, MenuActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                activity?.startActivity(intent)
            }
            //Delete Grup Opsi
            for (i in acGrupOpsiList) {
                val updateArrayMenuId = arrayListOf<String>()
                if (i.menuId != null) {
                    i.menuId!!.filterTo(updateArrayMenuId) { it != menuId }
                    val updateMap = mapOf(
                        "menuId" to updateArrayMenuId
                    )
                    db.collection("user").document(userId).collection("grupOpsi").document(i.id!!).update(updateMap)
                }
            }

            //Delete Image
            storageRef.getReference("menuImages").child(userId).child(kategoriMenuId!!).child(storageKeys!!).delete()

            //Delete Menu
            val menuRef = db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenuId!!).collection("menu").document(menuId!!)
            menuRef.delete().addOnSuccessListener {
                Toast.makeText(activity, R.string.success_delete_data, Toast.LENGTH_SHORT).show()
            } .addOnFailureListener {
                Toast.makeText(activity, R.string.fail_delete_data, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}