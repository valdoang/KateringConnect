package com.valdoang.kateringconnect.view.vendor.menu.grupopsi

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.OpsiShowMenuAdapter
import com.valdoang.kateringconnect.databinding.ActivityOpsiChooseMenuBinding
import com.valdoang.kateringconnect.model.KategoriMenu
import com.valdoang.kateringconnect.model.Menu
import com.valdoang.kateringconnect.utils.Cons

class OpsiChooseMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpsiChooseMenuBinding
    private var grupOpsiId: String? = null
    private var namaGrupOpsi: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var opsiShowMenuAdapter: OpsiShowMenuAdapter
    private lateinit var kategoriMenuList: ArrayList<KategoriMenu>
    private var db = Firebase.firestore
    private var arrayMenuId: ArrayList<String> = ArrayList()
    private var arrayMenu: ArrayList<Menu> = ArrayList()
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var btnSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpsiChooseMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        kategoriMenuList = arrayListOf()

        grupOpsiId = intent.getStringExtra(Cons.EXTRA_ID)
        namaGrupOpsi = intent.getStringExtra(Cons.EXTRA_NAMA)
        btnSimpan = binding.btnSimpan

        setupData()
        setupTv()
        setupView()
        saveData()
        setupAction()
    }

    private fun setupView() {
        recyclerView = binding.rvMenu
        recyclerView.layoutManager = LinearLayoutManager(this)

        opsiShowMenuAdapter = OpsiShowMenuAdapter(this, arrayMenuId, grupOpsiId!!, btnSimpan, arrayMenu)
        recyclerView.adapter = opsiShowMenuAdapter
        opsiShowMenuAdapter.setItems(kategoriMenuList)
    }

    private fun setupData() {
        val ref = db.collection("user").document(userId).collection("kategoriMenu")
        ref.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                kategoriMenuList.clear()
                for (data in snapshot.documents) {
                    val kategoriMenu: KategoriMenu? = data.toObject(KategoriMenu::class.java)
                    if (kategoriMenu != null) {
                        kategoriMenu.id = data.id
                        kategoriMenuList.add(kategoriMenu)
                    }
                }
                kategoriMenuList.sortBy {
                    it.nama
                }
                opsiShowMenuAdapter.setItems(kategoriMenuList)
            }

        }
    }

    private fun setupTv() {
        binding.tvDescPilihHidangan.text = getString(R.string.tv_desc_pilih_hidangan, namaGrupOpsi)
    }

    private fun saveData() {
        btnSimpan.setOnClickListener {
            val grupOpsiRef = db.collection("user").document(userId).collection("grupOpsi").document(grupOpsiId!!)
            grupOpsiRef.update("menuId", arrayMenuId)

            for (i in arrayMenu) {
                val menuRef = db.collection("user").document(userId).collection("kategoriMenu").document(i.kategoriMenuId!!).collection("menu").document(i.id!!)
                menuRef.get().addOnSuccessListener { menu ->
                    if (menu != null) {
                        val arrayGrupOpsiId = menu.data?.get("grupOpsiId") as? ArrayList<String>
                        if (arrayGrupOpsiId != null) {
                            if (!arrayGrupOpsiId.contains(grupOpsiId)) {
                                arrayGrupOpsiId.add(grupOpsiId!!)
                                val grupOpsiMap = mapOf(
                                    "grupOpsiId" to arrayGrupOpsiId
                                )
                                menuRef.update(grupOpsiMap)
                            }
                        } else {
                            val emptyArray: ArrayList<String> = arrayListOf()
                            emptyArray.add(grupOpsiId!!)
                            val grupOpsiMap = mapOf(
                                "grupOpsiId" to emptyArray
                            )
                            menuRef.update(grupOpsiMap)
                        }
                    }
                }
            }
            finish()
        }
    }

    private fun setupAction() {
        binding.ibBack.visibility = View.GONE
        binding.tvLewatkan.setOnClickListener {
            finish()
        }
    }
}