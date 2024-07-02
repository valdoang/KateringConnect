package com.valdoang.kateringconnect.view.user.custommenu

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.GrupOpsiAdapter
import com.valdoang.kateringconnect.databinding.ActivityCustomMenuBinding
import com.valdoang.kateringconnect.model.GrupOpsi
import com.valdoang.kateringconnect.model.Opsi
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.allChangedListener
import com.valdoang.kateringconnect.utils.withNumberingFormat

@RequiresApi(Build.VERSION_CODES.N)
class CustomMenuActivity : AppCompatActivity(), EditTextCatatanFragment.GetCatatan,
    EditTextJumlahFragment.GetJumlah {
    private lateinit var binding: ActivityCustomMenuBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var ivMenu: ImageView
    private lateinit var tvMenuName: TextView
    private lateinit var tvMenuDesc: TextView
    private lateinit var tvMenuPrice: TextView
    private lateinit var tvHargaDasar: TextView
    private lateinit var btnPesan: Button
    private lateinit var ibLess: ImageButton
    private lateinit var ibMore: ImageButton
    private lateinit var etJumlah: EditText
    private lateinit var etCatatan: EditText
    private var vendorId: String? = null
    private var kategoriId: String? = null
    private var menuId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var grupOpsiAdapter: GrupOpsiAdapter
    private var grupOpsiId: ArrayList<String>? = ArrayList()
    private var menuPrice = ""
    private var grupOpsiList: ArrayList<GrupOpsi> = ArrayList()
    private var opsiList: ArrayList<Opsi> = ArrayList()
    private var opsiListCheck: ArrayList<Opsi> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth

        vendorId = intent.getStringExtra(Cons.EXTRA_ID)
        kategoriId = intent.getStringExtra(Cons.EXTRA_SEC_ID)
        menuId = intent.getStringExtra(Cons.EXTRA_THIRD_ID)

        ivMenu = binding.ivMenu
        tvMenuName = binding.tvMenuName
        tvMenuDesc = binding.tvMenuDesc
        tvMenuPrice = binding.tvMenuPrice
        tvHargaDasar = binding.tvHargaDasar
        btnPesan = binding.btnPesan
        ibLess = binding.ibLess
        ibMore = binding.ibMore
        etJumlah = binding.etJumlah
        etCatatan = binding.etCatatan

        setupAction()
        setupView()
        setupData()
        editJumlah()
        pesan()
    }

    private fun setupData() {
        val menuRef = db.collection("user").document(vendorId!!).collection("kategoriMenu").document(kategoriId!!).collection("menu").document(menuId!!)
        menuRef.get().addOnSuccessListener {  menuSnapshot ->
            if (menuSnapshot != null) {
                val menuImage = menuSnapshot.data?.get("foto").toString()
                val menuName = menuSnapshot.data?.get("nama").toString()
                val menuDesc = menuSnapshot.data?.get("keterangan").toString()
                menuPrice = menuSnapshot.data?.get("harga").toString()
                grupOpsiId = menuSnapshot.data?.get("grupOpsiId") as? ArrayList<String>
                setupView()

                Glide.with(this).load(menuImage).error(R.drawable.default_menu).into(ivMenu)
                tvMenuName.text = menuName
                tvMenuDesc.text = menuDesc
                tvMenuPrice.text = menuPrice.withNumberingFormat()
                setupGrupOpsi()
                hitungTotal()
            }
        }
    }

    private fun setupGrupOpsi() {
        if (grupOpsiId.isNullOrEmpty()) {
            binding.clGrupOpsi.visibility = View.GONE
            tvHargaDasar.visibility = View.GONE
        } else {
            for (i in grupOpsiId!!) {
                val grupOpsiRef = db.collection("user").document(vendorId!!).collection("grupOpsi").document(i)
                grupOpsiRef.get().addOnSuccessListener { snapshot ->
                    if (snapshot != null) {
                        val grupOpsiModel = GrupOpsi()
                        grupOpsiModel.id = i
                        grupOpsiModel.nama = snapshot.data?.get("nama").toString()
                        grupOpsiList.add(grupOpsiModel)
                        Log.d("add", grupOpsiList.toString())

                        grupOpsiList.sortBy {
                            it.nama
                        }

                        val opsiRef = grupOpsiRef.collection("opsi")
                        opsiRef.get().addOnSuccessListener { opsiSnapshot ->
                            if (opsiSnapshot != null) {
                                opsiList.clear()
                                for (data in opsiSnapshot.documents) {
                                    val opsi: Opsi? = data.toObject(Opsi::class.java)
                                    if (opsi != null) {
                                        opsi.id = data.id
                                        opsiList.add(opsi)
                                    }
                                }

                                opsiList.removeIf { opsi ->
                                    opsi.aktif == false
                                }

                                if (opsiList.isEmpty()) {
                                    grupOpsiId!!.remove(i)
                                    grupOpsiList.remove(grupOpsiModel)
                                    Log.d("remove", grupOpsiList.toString())
                                }
                                grupOpsiAdapter.setItems(grupOpsiList)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvGrupOpsi
        recyclerView.layoutManager = LinearLayoutManager(this)
        grupOpsiAdapter = GrupOpsiAdapter(this, vendorId!!, opsiListCheck, btnPesan, grupOpsiId!!, menuPrice, etJumlah)
        recyclerView.adapter = grupOpsiAdapter
    }

    private fun editJumlah() {
        etJumlah.allChangedListener { jumlah ->
            if (jumlah.toInt() > 10) {
                ibLess.visibility = View.VISIBLE
            } else {
                ibLess.visibility = View.GONE
            }
        }

        ibLess.setOnClickListener {
            val jumlah = etJumlah.text.toString().toInt()
            val less = jumlah - 1
            etJumlah.setText(less.toString())
        }
        ibMore.setOnClickListener {
            val jumlah = etJumlah.text.toString().toInt()
            val more = jumlah + 1
            etJumlah.setText(more.toString())
        }
    }

    private fun hitungTotal() {
        var subtotal = menuPrice.toLong()
        var total: Long
        var jumlahTotal = etJumlah.text.toString().toLong()

        total = subtotal * jumlahTotal
        btnPesan.text = getString(R.string.btn_pesan_menu, total.withNumberingFormat())

        etJumlah.allChangedListener {
            jumlahTotal = it.toLong()
            subtotal = menuPrice.toLong()
            if (opsiListCheck.size <= 0) {
                total = subtotal * jumlahTotal
                btnPesan.text = getString(R.string.btn_pesan_menu, total.withNumberingFormat())
            } else {
                for (i in opsiListCheck) {
                    subtotal += i.harga!!.toLong()
                    total = subtotal * jumlahTotal
                    btnPesan.text = getString(R.string.btn_pesan_menu, total.withNumberingFormat())
                }
            }
        }

        btnPesan.isEnabled = grupOpsiId?.size == opsiListCheck.size
    }

    private fun pesan() {
        btnPesan.setOnClickListener{
            Log.d("opsiList", opsiListCheck.toString())
            //TODO: 3. KIRIM NILAI KE PEMESANAN ACTIVITY
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
        etCatatan.setOnClickListener {
            val args = Bundle()
            val sCatatan = etCatatan.text.toString().trim()
            args.putString("catatan", sCatatan)
            val dialog: DialogFragment = EditTextCatatanFragment()
            dialog.arguments = args
            dialog.show(this.supportFragmentManager, "ediTextCatatanDialog")
        }
        etJumlah.setOnClickListener {
            val args = Bundle()
            val sJumlah = etJumlah.text.toString().trim()
            args.putString("jumlah", sJumlah)
            val dialog: DialogFragment = EditTextJumlahFragment()
            dialog.arguments = args
            dialog.show(this.supportFragmentManager, "ediTextJumlahDialog")
        }
    }

    override fun getCatatan(catatan: String) {
        etCatatan.setText(catatan)
    }

    override fun getJumlah(jumlah: String) {
        etJumlah.setText(jumlah)
    }
}