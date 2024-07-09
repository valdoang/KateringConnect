package com.valdoang.kateringconnect.view.vendor.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.AcGrupOpsiAdapter
import com.valdoang.kateringconnect.adapter.AcKategoriAdapter
import com.valdoang.kateringconnect.databinding.ActivityAddMenuBinding
import com.valdoang.kateringconnect.model.AcKategori
import com.valdoang.kateringconnect.model.GrupOpsi
import com.valdoang.kateringconnect.utils.getImageUri
import com.valdoang.kateringconnect.view.vendor.menu.grupopsi.AddGrupOpsiActivity
import com.valdoang.kateringconnect.view.vendor.menu.kategori.AddKategoriActivity
import java.util.*
import kotlin.collections.ArrayList

class AddMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMenuBinding
    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var etPrice: EditText
    private lateinit var etMinOrder: EditText
    private lateinit var acKategori: AutoCompleteTextView
    private lateinit var acGrupOpsi: AutoCompleteTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private var currentImageUri: Uri? = null
    private lateinit var acKategoriRecyclerView: RecyclerView
    private lateinit var acKategoriList: ArrayList<AcKategori>
    private lateinit var acKategoriAdapter: AcKategoriAdapter
    private var idKategori = ""
    private var userId = ""
    private lateinit var acGrupOpsiRecyclerView: RecyclerView
    private lateinit var acGrupOpsiList: ArrayList<GrupOpsi>
    private lateinit var acGrupOpsiAdapter: AcGrupOpsiAdapter
    private var arrayGrupOpsiId: ArrayList<String> = ArrayList()
    private lateinit var btnSimpan: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        storageRef = FirebaseStorage.getInstance()
        userId = firebaseAuth.currentUser!!.uid

        etName = binding.edAddName
        etDesc = binding.edAddDesc
        etPrice = binding.edAddPrice
        etMinOrder = binding.edAddMinOrder
        acKategori = binding.acAddKategori
        acGrupOpsi = binding.acAddGrupOpsi
        progressBar = binding.progressBar
        btnSimpan = binding.ibSave

        setupAction()
        setupAcKategori()
        setupAcGrupOpsi()
        saveData()
    }

    private fun setupAcKategori() {
        acKategori.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_ac_kategori, null)

            val rvRadioButton = view.findViewById<RecyclerView>(R.id.rv_kategori)
            val llTambahKategori = view.findViewById<LinearLayout>(R.id.ll_tambah_kategori)

            //Setup View
            acKategoriList = arrayListOf()

            acKategoriRecyclerView = rvRadioButton
            acKategoriRecyclerView.layoutManager = LinearLayoutManager(this)

            acKategoriAdapter = AcKategoriAdapter(idKategori)
            acKategoriRecyclerView.adapter = acKategoriAdapter
            acKategoriAdapter.setItems(acKategoriList)

            //Setup Data
            val ref = db.collection("user").document(userId).collection("kategoriMenu")
            ref.addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    acKategoriList.clear()
                    for (data in snapshot.documents) {
                        val acKategori: AcKategori? = data.toObject(AcKategori::class.java)
                        if (acKategori != null) {
                            acKategori.id = data.id
                            acKategoriList.add(acKategori)
                        }
                    }

                    acKategoriList.sortBy { kategori ->
                        kategori.nama
                    }

                    acKategoriAdapter.setItems(acKategoriList)
                    acKategoriAdapter.setOnItemClickCallback(object :
                        AcKategoriAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: AcKategori) {
                            idKategori = data.id!!
                            acKategori.setText(data.nama)
                            dialog.dismiss()
                        }
                    })
                }
            }

            llTambahKategori.setOnClickListener {
                val intent = Intent(this, AddKategoriActivity::class.java)
                startActivity(intent)
            }

            dialog.setContentView(view)
            dialog.show()
        }
    }

    private fun setupAcGrupOpsi() {
        acGrupOpsi.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_ac_grup_opsi, null)

            val rvGrupOpsi = view.findViewById<RecyclerView>(R.id.rv_grup_opsi)
            val llBuatGrupOpsi = view.findViewById<LinearLayout>(R.id.ll_buat_grup_opsi)
            val btnSelesai = view.findViewById<Button>(R.id.btn_selesai)

            //Setup View
            acGrupOpsiList = arrayListOf()

            acGrupOpsiRecyclerView = rvGrupOpsi
            acGrupOpsiRecyclerView.layoutManager = LinearLayoutManager(this)

            acGrupOpsiAdapter = AcGrupOpsiAdapter(arrayGrupOpsiId)
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

            llBuatGrupOpsi.setOnClickListener {
                val intent = Intent(this, AddGrupOpsiActivity::class.java)
                startActivity(intent)
            }

            btnSelesai.setOnClickListener {
                dialog.dismiss()
                acGrupOpsi.setText(
                    getString(
                        R.string.jumlah_grup_opsi_terpilih,
                        arrayGrupOpsiId.size.toString()
                    )
                )
            }

            dialog.setContentView(view)
            dialog.show()
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.tvAddPhoto.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_add_photo, null)

            val cvGaleri = view.findViewById<ConstraintLayout>(R.id.cv_gallery)
            val cvCamera = view.findViewById<ConstraintLayout>(R.id.cv_camera)

            cvGaleri.setOnClickListener {
                launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                dialog.dismiss()
            }

            cvCamera.setOnClickListener {
                currentImageUri = getImageUri(this)
                launcherIntentCamera.launch(currentImageUri)
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()
        }
    }

    private fun saveData() {
        btnSimpan.setOnClickListener {
            val sName = etName.text.toString().trim()
            val sDesc = etDesc.text.toString().trim()
            val sPrice = etPrice.text.toString().trim()
            var sMinOrder = etMinOrder.text.toString().trim()

            val filename = UUID.randomUUID().toString()

            when {
                currentImageUri == null -> {
                    Toast.makeText(this, R.string.can_not_blank, Toast.LENGTH_SHORT).show()
                }
                sName.isEmpty() -> {
                    etName.error = getString(R.string.entry_name)
                }
                sPrice.isEmpty() -> {
                    etPrice.error = getString(R.string.entry_price)
                }
                idKategori.isEmpty() -> {
                    acKategori.error = getString(R.string.entry_kategori)
                }
                else -> {
                    btnSimpan.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE

                    currentImageUri?.let {
                        storageRef.getReference("menuImages").child(userId).child(idKategori)
                            .child(filename)
                            .putFile(it)
                            .addOnSuccessListener { task ->
                                task.metadata!!.reference!!.downloadUrl
                                    .addOnSuccessListener { uri ->

                                        if (sMinOrder.isEmpty()) {
                                            sMinOrder = "1"
                                        }

                                        val mapMenu = mapOf(
                                            "foto" to uri.toString(),
                                            "nama" to sName,
                                            "keterangan" to sDesc,
                                            "harga" to sPrice,
                                            "minOrder" to sMinOrder,
                                            "aktif" to true,
                                            "storageKeys" to filename,
                                            "grupOpsiId" to arrayGrupOpsiId
                                        )
                                        val newMenu = db.collection("user").document(userId)
                                            .collection("kategoriMenu").document(idKategori)
                                            .collection("menu").document()
                                        newMenu.set(mapMenu)
                                            .addOnSuccessListener {
                                                progressBar.visibility = View.GONE

                                                val newMenuId = newMenu.id
                                                for (i in arrayGrupOpsiId) {
                                                    val grupOpsiRef =
                                                        db.collection("user").document(userId)
                                                            .collection("grupOpsi").document(i)
                                                    grupOpsiRef.get()
                                                        .addOnSuccessListener { grupOpsi ->
                                                            if (grupOpsi != null) {
                                                                val menuId =
                                                                    grupOpsi.data?.get("menuId") as? ArrayList<String>
                                                                if (menuId != null) {
                                                                    menuId.add(newMenuId)
                                                                    val grupOpsiMap = mapOf(
                                                                        "menuId" to menuId
                                                                    )
                                                                    grupOpsiRef.update(grupOpsiMap)
                                                                } else {
                                                                    val emptyArray: ArrayList<String> =
                                                                        arrayListOf()
                                                                    emptyArray.add(newMenuId)
                                                                    val grupOpsiMap = mapOf(
                                                                        "menuId" to emptyArray
                                                                    )
                                                                    grupOpsiRef.update(grupOpsiMap)
                                                                }
                                                            }
                                                        }
                                                }

                                                finish()
                                                Toast.makeText(
                                                    this,
                                                    R.string.success_upload_menu,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .addOnFailureListener {
                                                progressBar.visibility = View.GONE
                                                Toast.makeText(
                                                    this,
                                                    R.string.fail_upload_menu,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                            }
                    }
                }
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivEditPhoto.setImageURI(it)
        }
        binding.tvAddPhoto.text = getString(R.string.edit_foto)
    }
}