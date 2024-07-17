package com.valdoang.kateringconnect.view.vendor.menu.kategori

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


class DeleteKategoriFragment : DialogFragment() {

    private var _binding: FragmentAlertDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private var userId = ""
    private var kategoriMenuId: String? = null
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
        kategoriMenuId = mArgs!!.getString("kategoriMenuId")

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
        binding.tvAlert.text = resources.getString(R.string.delete_kategori_alert)
    }

    private fun closeDialog() {
        binding.btnNo.setOnClickListener{
            dismiss()
        }
    }

    private fun deleteKategori() {
        val menuRef = db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenuId!!).collection("menu")
        menuRef.get().addOnSuccessListener {  menuSnapshot ->
            if (menuSnapshot != null) {
                menuList.clear()
                for (data in menuSnapshot.documents) {
                    val menu: Menu? = data?.toObject(Menu::class.java)
                    if (menu != null) {
                        menu.id = data.id
                        menuList.add(menu)
                    }
                }
            }
        }

        val grupOpsiRef = db.collection("user").document(userId).collection("grupOpsi")
        grupOpsiRef.get().addOnSuccessListener { grupOpsiSnapshot ->
            if (grupOpsiSnapshot != null) {
                acGrupOpsiList.clear()
                for (data in grupOpsiSnapshot.documents) {
                    val acGrupOpsi: GrupOpsi? = data.toObject(GrupOpsi::class.java)
                    if (acGrupOpsi != null) {
                        acGrupOpsi.id = data.id
                        acGrupOpsiList.add(acGrupOpsi)
                    }
                }
            }
        }

        binding.btnYes.setOnClickListener {
            Intent(activity, MenuActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                activity?.startActivity(intent)
            }
            //Delete Grup Opsi
            for (i in menuList) {
                for (j in acGrupOpsiList) {
                    val updateArrayMenuId = arrayListOf<String>()
                    if (j.menuId != null) {
                        j.menuId!!.filterTo(updateArrayMenuId) { it != i.id }
                        j.menuId = updateArrayMenuId
                        val updateMap = mapOf(
                            "menuId" to updateArrayMenuId
                        )
                        db.collection("user").document(userId).collection("grupOpsi").document(j.id!!).update(updateMap)
                    }
                }

                //Delete Image
                storageRef.getReference("menuImages").child(userId).child(kategoriMenuId!!).child(i.storageKeys!!).delete()
            }

            //Delete Kategori
            val kategoriRef = db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenuId!!)
            kategoriRef.delete()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}