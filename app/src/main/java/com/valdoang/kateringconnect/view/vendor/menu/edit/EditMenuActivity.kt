package com.valdoang.kateringconnect.view.vendor.menu.edit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityEditMenuBinding
import com.valdoang.kateringconnect.model.GrupOpsi
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.getImageUri
import com.valdoang.kateringconnect.utils.withCurrencyFormat


class EditMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMenuBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private lateinit var ivFoto: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvDeskripsi: TextView
    private lateinit var tvHarga: TextView
    private lateinit var tvGrupOpsi: TextView
    private lateinit var tvHapus: TextView
    private lateinit var cvNama: CardView
    private lateinit var cvDeskripsi: CardView
    private lateinit var cvHarga: CardView
    private lateinit var cvGrupOpsi: CardView
    private var storageKeys = ""
    private var nama = ""
    private var desc = ""
    private var price = ""
    private var kategoriMenuId: String? = null
    private var menuId: String? = null
    private var currentImageUri: Uri? = null
    private lateinit var progressBar: ProgressBar
    private var userId = ""
    private lateinit var acGrupOpsiList: ArrayList<GrupOpsi>
    private var arrayGrupOpsiId: ArrayList<String> = ArrayList()
    private lateinit var tvTitle: TextView
    private lateinit var tvEditPhoto: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        storageRef = FirebaseStorage.getInstance()
        userId = firebaseAuth.currentUser!!.uid
        acGrupOpsiList = arrayListOf()

        progressBar = binding.progressBar
        ivFoto = binding.ivEditPhoto
        tvNama = binding.tvNamaHidangan
        tvDeskripsi = binding.tvDeskripsi
        tvHarga = binding.tvHarga
        tvGrupOpsi = binding.tvGrupOpsi
        tvHapus = binding.tvHapus
        tvTitle = binding.titleTambahMenu
        tvEditPhoto = binding.tvAddPhoto
        cvNama = binding.cvNamaHidangan
        cvDeskripsi = binding.cvDeskripsi
        cvHarga = binding.cvHarga
        cvGrupOpsi = binding.cvGrupOpsi

        kategoriMenuId = intent.getStringExtra(Cons.EXTRA_ID)
        menuId = intent.getStringExtra(Cons.EXTRA_SEC_ID)

        setData(kategoriMenuId!!, menuId!!)
        editFoto()
        editEach()
        deleteData()
        closeDialog()
    }

    private fun setData(kategoriMenuId: String, menuId: String) {
        val menuRef = db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenuId).collection("menu").document(menuId)
        menuRef.addSnapshotListener { menuDoc,_ ->
                if (menuDoc != null) {
                    val foto = menuDoc.data?.get("foto").toString()
                    nama = menuDoc.data?.get("nama").toString()
                    desc = menuDoc.data?.get("keterangan").toString()
                    price = menuDoc.data?.get("harga").toString()
                    storageKeys = menuDoc.data?.get("storageKeys").toString()
                    val grupOpsiId = menuDoc.data?.get("grupOpsiId") as? ArrayList<String>

                    if (grupOpsiId != null) {
                        arrayGrupOpsiId = grupOpsiId
                    }

                    Glide.with(applicationContext).load(foto).error(R.drawable.addphoto).into(ivFoto)
                    tvNama.text = nama
                    tvDeskripsi.text = desc
                    tvHarga.text = price
                    tvGrupOpsi.text = getString(R.string.jumlah_grup_opsi_terpilih, arrayGrupOpsiId.size.toString())
                }
            }
    }

    private fun editEach() {
        cvNama.setOnClickListener {
            val intent = Intent(this, EditNamaMenuActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, nama)
            intent.putExtra(Cons.EXTRA_ID, kategoriMenuId)
            intent.putExtra(Cons.EXTRA_SEC_ID, menuId)
            startActivity(intent)
        }
        cvDeskripsi.setOnClickListener {
            val intent = Intent(this, EditDeskripsiMenuActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, desc)
            intent.putExtra(Cons.EXTRA_ID, kategoriMenuId)
            intent.putExtra(Cons.EXTRA_SEC_ID, menuId)
            startActivity(intent)
        }
        cvHarga.setOnClickListener {
            val intent = Intent(this, EditHargaMenuActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, price)
            intent.putExtra(Cons.EXTRA_ID, kategoriMenuId)
            intent.putExtra(Cons.EXTRA_SEC_ID, menuId)
            startActivity(intent)
        }
        cvGrupOpsi.setOnClickListener {
            val intent = Intent(this, EditGrupOpsiMenuActivity::class.java)
            intent.putStringArrayListExtra(Cons.EXTRA_NAMA, arrayGrupOpsiId)
            intent.putStringArrayListExtra(Cons.EXTRA_SEC_NAMA, arrayGrupOpsiId)
            intent.putExtra(Cons.EXTRA_ID, kategoriMenuId)
            intent.putExtra(Cons.EXTRA_SEC_ID, menuId)
            startActivity(intent)
        }
    }

    private fun deleteData() {
        tvHapus.setOnClickListener{
            val args = Bundle()
            args.putString("kategoriMenuId", kategoriMenuId)
            args.putString("menuId", menuId)
            args.putString("storageKeys", storageKeys)
            val dialog = DeleteMenuFragment()
            dialog.arguments = args
            dialog.show(supportFragmentManager, "deleteMenuDialog")
        }
    }

    private fun closeDialog() {
        binding.ibBack.setOnClickListener{
            onBackPressed()
        }
    }

    private fun editFoto() {
        tvEditPhoto.setOnClickListener {
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

    private fun uploadPhoto() {
        progressBar.visibility = View.VISIBLE
        currentImageUri?.let {
            storageRef.getReference("menuImages").child(userId).child(kategoriMenuId!!).child(storageKeys)
                .putFile(it)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {uri ->
                            val mapImage = mapOf(
                                "foto" to uri.toString()
                            )
                            db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenuId!!).collection("menu").document(menuId!!).update(mapImage)
                                .addOnSuccessListener {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(this, R.string.success_upload_photo, Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{
                                    Toast.makeText(this, R.string.fail_upload_photo, Toast.LENGTH_SHORT).show()
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
            uploadPhoto()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            uploadPhoto()
        }
    }
}