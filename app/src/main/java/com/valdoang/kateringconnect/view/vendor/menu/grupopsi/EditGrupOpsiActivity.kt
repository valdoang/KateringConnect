package com.valdoang.kateringconnect.view.vendor.menu.grupopsi

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.OpsiAdapter
import com.valdoang.kateringconnect.databinding.ActivityEditGrupOpsiBinding
import com.valdoang.kateringconnect.model.Opsi
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.beforeChangedListener
import java.util.*

class EditGrupOpsiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditGrupOpsiBinding
    private var grupOpsiId: String? = null
    private lateinit var etNama: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId = ""
    private lateinit var opsiList: ArrayList<Opsi>
    private lateinit var recyclerView: RecyclerView
    private lateinit var opsiAdapter: OpsiAdapter
    private lateinit var btnAddOpsi: Button
    private lateinit var btnSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditGrupOpsiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid
        opsiList = arrayListOf()

        etNama = binding.edAddNamaGrupOpsi
        btnAddOpsi = binding.btnTambahkanOpsi
        btnSimpan = binding.btnSimpan

        grupOpsiId = intent.getStringExtra(Cons.EXTRA_ID)

        setupView()
        setupData()
        addOpsi()
        editOpsi()
        updateGrupOpsi()
        setupAction()
    }

    private fun setupView() {
        recyclerView = binding.rvOpsi
        recyclerView.layoutManager = LinearLayoutManager(this)
        opsiAdapter = OpsiAdapter(this)
        recyclerView.adapter = opsiAdapter
    }

    private fun setupData() {
        val grupOpsiRef = db.collection("user").document(userId).collection("grupOpsi").document(grupOpsiId!!)
        grupOpsiRef.get().addOnSuccessListener {  grupOpsiDoc ->
            if (grupOpsiDoc != null) {
                val namaGrupOpsi = grupOpsiDoc.data?.get("nama").toString()
                etNama.setText(namaGrupOpsi)
            }
        }

        grupOpsiRef.collection("opsi").get().addOnSuccessListener { opsiSnapshot ->
            if (opsiSnapshot != null){
                opsiList.clear()
                for (data in opsiSnapshot.documents) {
                    val opsi: Opsi? = data.toObject(Opsi::class.java)
                    if (opsi != null) {
                        opsi.id = data.id
                        opsiList.add(opsi)
                    }

                    opsiList.sortBy {
                        it.nama
                    }

                    opsiAdapter.setItems(opsiList)
                }
            }
        }
    }

    private fun addOpsi() {
        btnAddOpsi.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_add_opsi, null)

            val edAddNamaOpsi = view.findViewById<EditText>(R.id.ed_add_nama_opsi)
            val edAddHargaOpsi = view.findViewById<EditText>(R.id.ed_add_harga_opsi)
            val btnSimpan = view.findViewById<Button>(R.id.btn_simpan)
            val btnBatalkan = view.findViewById<Button>(R.id.btn_batalkan)
            val tvHapus = view.findViewById<TextView>(R.id.tv_hapus)

            edAddNamaOpsi.beforeChangedListener(btnSimpan)

            tvHapus.visibility = View.GONE

            btnSimpan.setOnClickListener {
                val opsi = Opsi()
                val uuid = UUID.randomUUID().toString()
                val sNama = edAddNamaOpsi.text.toString().trim()
                var sHarga = edAddHargaOpsi.text.toString().trim()

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
                val dialog = BottomSheetDialog(this@EditGrupOpsiActivity)
                val view = layoutInflater.inflate(R.layout.bottom_sheet_add_opsi, null)

                val tvTambahOpsi = view.findViewById<TextView>(R.id.tv_tambah_opsi)
                val edAddNamaOpsi = view.findViewById<EditText>(R.id.ed_add_nama_opsi)
                val edAddHargaOpsi = view.findViewById<EditText>(R.id.ed_add_harga_opsi)
                val btnSimpan = view.findViewById<Button>(R.id.btn_simpan)
                val btnBatalkan = view.findViewById<Button>(R.id.btn_batalkan)
                val tvNamaOpsi = view.findViewById<TextView>(R.id.tv_nama_opsi)
                val tvHapus = view.findViewById<TextView>(R.id.tv_hapus)

                tvTambahOpsi.text = getString(R.string.ubah_opsi)
                tvNamaOpsi.text = getString(R.string.tv_nama)
                edAddNamaOpsi.setText(data.nama)
                edAddHargaOpsi.setText(data.harga)

                edAddNamaOpsi.beforeChangedListener(btnSimpan)

                tvHapus.setOnClickListener {
                    val listPosition = opsiList.indexOfFirst {
                        it.id == data.id
                    }

                    opsiList.remove(opsiList[listPosition])
                    opsiAdapter.setItems(opsiList)
                    dialog.dismiss()
                }

                btnSimpan.setOnClickListener {
                    val listPosition= opsiList.indexOfFirst {
                        it.id == data.id
                    }

                    val sNama = edAddNamaOpsi.text.toString().trim()
                    var sHarga = edAddHargaOpsi.text.toString().trim()

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

                btnBatalkan.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.setContentView(view)
                dialog.show()
            }
        })
    }

    private fun updateGrupOpsi() {
        etNama.beforeChangedListener(btnSimpan)
        btnSimpan.setOnClickListener {
            //Nah kerjain ini
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener{
            finish()
        }
    }
}