package com.valdoang.kateringconnect.view.vendor.menu.edit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.AcGrupOpsiAdapter
import com.valdoang.kateringconnect.databinding.ActivityEditRvBinding
import com.valdoang.kateringconnect.model.GrupOpsi
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.vendor.menu.grupopsi.AddGrupOpsiActivity

class EditGrupOpsiMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditRvBinding
    private var arrayGrupOpsiMenu: ArrayList<String>? = null
    private var arrayGrupOpsiMenuTemp: ArrayList<String>? = null
    private var kategoriMenuId: String? = null
    private var menuId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId = ""
    private lateinit var ibSave: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var acGrupOpsiRecyclerView: RecyclerView
    private lateinit var acGrupOpsiList: ArrayList<GrupOpsi>
    private lateinit var acGrupOpsiAdapter: AcGrupOpsiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditRvBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid

        arrayGrupOpsiMenu = intent.getStringArrayListExtra(Cons.EXTRA_NAMA)
        arrayGrupOpsiMenuTemp = intent.getStringArrayListExtra(Cons.EXTRA_SEC_NAMA)
        kategoriMenuId = intent.getStringExtra(Cons.EXTRA_ID)
        menuId = intent.getStringExtra(Cons.EXTRA_SEC_ID)

        ibSave = binding.ibSave
        progressBar = binding.progressBar

        setupRv()
        updateGrupOpsi()
        buatGrupOpsi()
        closeActivity()
    }

    private fun setupRv() {
        //Setup View
        acGrupOpsiList = arrayListOf()

        acGrupOpsiRecyclerView = binding.rvEditRv
        acGrupOpsiRecyclerView.layoutManager = LinearLayoutManager(this)

        acGrupOpsiAdapter = AcGrupOpsiAdapter(arrayGrupOpsiMenu!!)
        acGrupOpsiRecyclerView.adapter = acGrupOpsiAdapter
        acGrupOpsiAdapter.setItems(acGrupOpsiList)

        //Setup Data
        val ref = db.collection("user").document(userId).collection("grupOpsi")
        ref.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                acGrupOpsiList.clear()
                for (data in snapshot.documents) {
                    val acGrupOpsi: GrupOpsi? = data.toObject(GrupOpsi::class.java)
                    if (acGrupOpsi != null) {
                        acGrupOpsi.id = data.id
                        acGrupOpsiList.add(acGrupOpsi)
                    }
                }

                acGrupOpsiAdapter.setItems(acGrupOpsiList)
            }
        }
    }

    private fun updateGrupOpsi() {
        ibSave.setOnClickListener {
            ibSave.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            val updateMap = mapOf(
                "grupOpsiId" to arrayGrupOpsiMenu
            )
            val ref = db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenuId!!).collection("menu").document(menuId!!)
            ref.update(updateMap).addOnSuccessListener {

                val res: ArrayList<String> = arrayListOf()
                for (id in arrayGrupOpsiMenuTemp!!) {
                    if (!arrayGrupOpsiMenu!!.contains(id)) {
                        res.add(id)
                    }
                }

                for (i in res) {
                    val grupOpsiRef = db.collection("user").document(userId).collection("grupOpsi").document(i)
                    grupOpsiRef.get().addOnSuccessListener { grupOpsi ->
                        if (grupOpsi != null) {
                            val arrayMenuId = grupOpsi.data?.get("menuId") as? ArrayList<String>
                            if (arrayMenuId != null) {
                                arrayMenuId.remove(menuId!!)
                                val grupOpsiMap = mapOf(
                                    "menuId" to arrayMenuId
                                )
                                grupOpsiRef.update(grupOpsiMap)
                            }
                        }
                    }
                }

                for (i in arrayGrupOpsiMenu!!) {
                    val grupOpsiRef = db.collection("user").document(userId).collection("grupOpsi").document(i)
                    grupOpsiRef.get().addOnSuccessListener { grupOpsi ->
                        if (grupOpsi != null) {
                            val arrayMenuId = grupOpsi.data?.get("menuId") as? ArrayList<String>
                            if (arrayMenuId != null) {
                                if (!arrayMenuId.contains(menuId)) {
                                    arrayMenuId.add(menuId!!)
                                    val grupOpsiMap = mapOf(
                                        "menuId" to arrayMenuId
                                    )
                                    grupOpsiRef.update(grupOpsiMap)
                                }
                            } else {
                                val emptyArray: ArrayList<String> = arrayListOf()
                                emptyArray.add(menuId!!)
                                val grupOpsiMap = mapOf(
                                    "menuId" to emptyArray
                                )
                                grupOpsiRef.update(grupOpsiMap)
                            }
                        }
                    }
                }
                finish()
            } .addOnFailureListener {
                Toast.makeText(this, R.string.gagal_mengubah, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buatGrupOpsi() {
        binding.llBuatGrupOpsi.setOnClickListener {
            val intent = Intent(this, AddGrupOpsiActivity::class.java)
            startActivity(intent)
        }
    }

    private fun closeActivity() {
        binding.ibClose.setOnClickListener {
            finish()
        }
    }
}