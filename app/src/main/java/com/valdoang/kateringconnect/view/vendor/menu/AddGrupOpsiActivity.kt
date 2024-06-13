package com.valdoang.kateringconnect.view.vendor.menu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.OpsiAdapter
import com.valdoang.kateringconnect.databinding.ActivityAddGrupOpsiBinding
import com.valdoang.kateringconnect.model.KategoriMenu
import com.valdoang.kateringconnect.model.Opsi
import com.valdoang.kateringconnect.utils.Cons
import java.util.*

class AddGrupOpsiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGrupOpsiBinding
    private lateinit var opsiList: ArrayList<Opsi>
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var recyclerViewOpsi: RecyclerView
    private lateinit var opsiAdapter: OpsiAdapter
    private lateinit var kategoriMenuList: ArrayList<KategoriMenu>
    private lateinit var etNama: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGrupOpsiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        opsiList = arrayListOf()
        kategoriMenuList = arrayListOf()

        etNama = binding.edAddNamaGrupOpsi

        setupView()
        setupAction()
        tambahOpsi()
        editOpsi()
        simpanGrupOpsi()
    }

    private fun tambahOpsi() {
        binding.btnTambahkanOpsi.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_add_opsi, null)

            val edAddNamaOpsi = view.findViewById<EditText>(R.id.ed_add_nama_opsi)
            val edAddHargaOpsi = view.findViewById<EditText>(R.id.ed_add_harga_opsi)
            val btnSimpan = view.findViewById<Button>(R.id.btn_simpan)
            val btnBatalkan = view.findViewById<Button>(R.id.btn_batalkan)

            btnSimpan.setOnClickListener {
                val opsi = Opsi()
                val uuid = UUID.randomUUID().toString()
                val sNama = edAddNamaOpsi.text.toString().trim()
                var sHarga = edAddHargaOpsi.text.toString().trim()

                when {
                    sNama.isEmpty() -> {
                        edAddNamaOpsi.error = getString(R.string.entry_name)
                    }
                    else -> {
                        if (sHarga.isEmpty()) {
                            sHarga = "0"
                        }
                        opsi.id = uuid
                        opsi.nama = sNama
                        opsi.harga = sHarga
                        opsiList.add(opsi)
                        opsiAdapter.setItems(opsiList)
                        dialog.dismiss()
                    }
                }
            }

            btnBatalkan.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()
        }
    }

    private fun editOpsi() {
        opsiAdapter.setOnItemClickCallback(object :
            OpsiAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Opsi) {
                val dialog = BottomSheetDialog(this@AddGrupOpsiActivity)
                val view = layoutInflater.inflate(R.layout.bottom_sheet_add_opsi, null)

                val tvTambahOpsi = view.findViewById<TextView>(R.id.tv_tambah_opsi)
                val edAddNamaOpsi = view.findViewById<EditText>(R.id.ed_add_nama_opsi)
                val edAddHargaOpsi = view.findViewById<EditText>(R.id.ed_add_harga_opsi)
                val btnSimpan = view.findViewById<Button>(R.id.btn_simpan)
                val btnBatalkan = view.findViewById<Button>(R.id.btn_batalkan)

                tvTambahOpsi.text = getString(R.string.ubah_opsi)
                edAddNamaOpsi.setText(data.nama)
                edAddHargaOpsi.setText(data.harga)

                btnSimpan.setOnClickListener {
                    val listPosition= opsiList.indexOfFirst {
                        it.id == data.id
                    }

                    val sNama = edAddNamaOpsi.text.toString().trim()
                    var sHarga = edAddHargaOpsi.text.toString().trim()

                    when {
                        sNama.isEmpty() -> {
                            edAddNamaOpsi.error = getString(R.string.entry_name)
                        }
                        else -> {
                            if (sHarga.isEmpty()) {
                                sHarga = "0"
                            }
                            val updatedList = opsiList[listPosition].apply {
                                nama = sNama
                                harga = sHarga
                            }
                            opsiList[listPosition] = updatedList
                            opsiAdapter.setItems(opsiList)
                            dialog.dismiss()
                        }
                    }
                }

                btnBatalkan.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.setContentView(view)
                dialog.show()
            }
        })
    }

    private fun simpanGrupOpsi() {
        binding.ibSave.setOnClickListener {
            val userId = firebaseAuth.currentUser!!.uid
            val sNama = etNama.text.toString().trim()
            val namaGrupOpsi = binding.edAddNamaGrupOpsi.text.toString().trim()

            when {
                sNama.isEmpty() -> {
                    etNama.error = getString(R.string.entry_name)
                }
                else -> {
                    val grupOpsiMap = mapOf(
                        "nama" to sNama
                    )

                    val newGrupOpsi = db.collection("user").document(userId).collection("grupOpsi").document()
                    newGrupOpsi.set(grupOpsiMap)

                    val grupOpsiId = newGrupOpsi.id

                    for (i in opsiList) {
                        val opsiMap = mapOf(
                            "nama" to i.nama,
                            "harga" to i.harga,
                            "aktif" to i.aktif
                        )

                        val newOpsi = db.collection("user").document(userId).collection("grupOpsi").document(grupOpsiId).collection("opsi").document()
                        newOpsi.set(opsiMap)
                    }
                    val intent = Intent(this, OpsiChooseMenuActivity::class.java)
                    intent.putExtra(Cons.EXTRA_ID, grupOpsiId)
                    intent.putExtra(Cons.EXTRA_NAMA, namaGrupOpsi)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun setupView() {
        recyclerViewOpsi = binding.rvOpsi
        recyclerViewOpsi.layoutManager = LinearLayoutManager(this)

        opsiAdapter = OpsiAdapter(this)
        recyclerViewOpsi.adapter = opsiAdapter
        opsiAdapter.setItems(opsiList)
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }
}