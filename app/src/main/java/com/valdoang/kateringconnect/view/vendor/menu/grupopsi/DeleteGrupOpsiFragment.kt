package com.valdoang.kateringconnect.view.vendor.menu.grupopsi

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentAlertDialogBinding
import com.valdoang.kateringconnect.model.KategoriMenu
import com.valdoang.kateringconnect.model.Menu
import com.valdoang.kateringconnect.view.vendor.menu.MenuActivity


class DeleteGrupOpsiFragment : DialogFragment() {

    private var _binding: FragmentAlertDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId = ""
    private var grupOpsiId: String? = null
    private var kategoriMenuList: ArrayList<KategoriMenu> = ArrayList()
    private var arrayMenuId: ArrayList<String> = ArrayList()
    private var arrayMenu: ArrayList<Menu> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAlertDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser!!.uid

        val mArgs = arguments
        grupOpsiId = mArgs!!.getString("grupOpsiId")

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
        binding.tvAlert.text = resources.getString(R.string.delete_grup_opsi_alert)
    }

    private fun closeDialog() {
        binding.btnNo.setOnClickListener{
            dismiss()
        }
    }

    private fun deleteKategori() {
        val kategoriMenuRef = db.collection("user").document(userId).collection("kategoriMenu")
        kategoriMenuRef.get().addOnSuccessListener { snapshot->
            if (snapshot != null) {
                kategoriMenuList.clear()
                for (data in snapshot.documents) {
                    val kategoriMenu: KategoriMenu? = data.toObject(KategoriMenu::class.java)
                    if (kategoriMenu != null) {
                        kategoriMenu.id = data.id
                        kategoriMenuList.add(kategoriMenu)
                    }
                }
            }
        }

        val grupOpsiRef = db.collection("user").document(userId).collection("grupOpsi").document(grupOpsiId!!)
        grupOpsiRef.get().addOnSuccessListener {
            if (it != null) {
                val menuId = it.data?.get("menuId") as? ArrayList<String>
                if (menuId != null) {
                    arrayMenuId = menuId
                }

                for (i in arrayMenuId) {
                    for (j in kategoriMenuList) {
                        val menuRef = db.collection("user").document(userId).collection("kategoriMenu").document(j.id!!).collection("menu").document(i)
                        menuRef.get().addOnSuccessListener { menuSnapshot ->
                            if (menuSnapshot != null) {
                                val menu = Menu(
                                    id = menuSnapshot.id,
                                    foto = menuSnapshot.data?.get("foto").toString(),
                                    nama = menuSnapshot.data?.get("nama").toString(),
                                    keterangan = menuSnapshot.data?.get("keterangan").toString(),
                                    harga = menuSnapshot.data?.get("harga").toString(),
                                    aktif = menuSnapshot.data?.get("aktif") as Boolean?,
                                    storageKeys = menuSnapshot.data?.get("storageKeys").toString(),
                                    grupOpsiId = menuSnapshot.data?.get("grupOpsiId") as ArrayList<String>?,
                                    kategoriMenuId = j.id
                                )
                                if (menu.foto != "null") {
                                    arrayMenu.add(menu)
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.btnYes.setOnClickListener {
            Intent(activity, MenuActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                activity?.startActivity(intent)
            }
            grupOpsiRef.delete().addOnSuccessListener {
                for (i in arrayMenu) {
                    val menuRef = db.collection("user").document(userId).collection("kategoriMenu").document(i.kategoriMenuId!!).collection("menu").document(i.id!!)
                    menuRef.get().addOnSuccessListener {
                        if (it != null) {
                            val arrayGrupOpsiId = it.data?.get("grupOpsiId") as? ArrayList<String>
                            arrayGrupOpsiId?.remove(grupOpsiId)
                            val updateMap = mapOf(
                                "grupOpsiId" to arrayGrupOpsiId
                            )
                            menuRef.update(updateMap)
                        }
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