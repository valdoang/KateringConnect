package com.valdoang.kateringconnect.view.user.custommenu

import android.os.Build
import android.os.Bundle
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
import java.util.stream.Collectors


@RequiresApi(Build.VERSION_CODES.N)
class CustomMenuActivity : AppCompatActivity(), EditTextCatatanFragment.GetCatatan,
    EditTextJumlahFragment.GetJumlah {
    private lateinit var binding: ActivityCustomMenuBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var userId = ""
    private var db = Firebase.firestore
    private lateinit var ivMenu: ImageView
    private lateinit var tvMenuName: TextView
    private lateinit var tvMenuDesc: TextView
    private lateinit var tvMenuPrice: TextView
    private lateinit var tvHargaDasar: TextView
    private lateinit var btnAddKeranjang: Button
    private lateinit var ibLess: ImageButton
    private lateinit var ibMore: ImageButton
    private lateinit var etJumlah: EditText
    private lateinit var etCatatan: EditText
    private var vendorId: String? = null
    private var kategoriId: String? = null
    private var menuId: String? = null
    private var alamatId: String? = null
    private var keranjangId: String? = null
    private var ongkir: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var grupOpsiAdapter: GrupOpsiAdapter
    private var grupOpsiId: ArrayList<String>? = ArrayList()
    private var menuImage = ""
    private var menuPrice = ""
    private var menuName = ""
    private var menuDesc = ""
    private var minOrder = ""
    private var grupOpsiList: ArrayList<GrupOpsi> = ArrayList()
    private var opsiList: ArrayList<Opsi> = ArrayList()
    private var opsiListCheck: ArrayList<Opsi> = ArrayList()
    private var total = 0L
    private var totalHarga = 0L
    private var namaOpsi = ""
    //TODO: BUAT KONDISI KETIKA IDKERANJANG NYA KOSONG, PESAN DIGUNAKAN UNTUK SET KERANJANG BARU
    //TODO: DAN KETIKA IDKERANJANG NYA ADA, UBAH KUSTOMIASI MENU SESUAI ISI KERANJANGNYA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid

        vendorId = intent.getStringExtra(Cons.EXTRA_ID)
        kategoriId = intent.getStringExtra(Cons.EXTRA_SEC_ID)
        menuId = intent.getStringExtra(Cons.EXTRA_THIRD_ID)
        alamatId = intent.getStringExtra(Cons.EXTRA_FOURTH_ID)
        keranjangId = intent.getStringExtra(Cons.EXTRA_FIFTH_ID)
        ongkir = intent.getStringExtra(Cons.EXTRA_ONGKIR)

        ivMenu = binding.ivMenu
        tvMenuName = binding.tvMenuName
        tvMenuDesc = binding.tvMenuDesc
        tvMenuPrice = binding.tvMenuPrice
        tvHargaDasar = binding.tvHargaDasar
        btnAddKeranjang = binding.btnAddKeranjang
        ibLess = binding.ibLess
        ibMore = binding.ibMore
        etJumlah = binding.etJumlah
        etCatatan = binding.etCatatan

        setupAction()
        setupView()
        setupData()
        pesan()
    }

    private fun setupData() {
        val menuRef = db.collection("user").document(vendorId!!).collection("kategoriMenu").document(kategoriId!!).collection("menu").document(menuId!!)
        menuRef.get().addOnSuccessListener {  menuSnapshot ->
            if (menuSnapshot != null) {
                menuImage = menuSnapshot.data?.get("foto").toString()
                menuName = menuSnapshot.data?.get("nama").toString()
                menuDesc = menuSnapshot.data?.get("keterangan").toString()
                menuPrice = menuSnapshot.data?.get("harga").toString()
                grupOpsiId = menuSnapshot.data?.get("grupOpsiId") as? ArrayList<String>
                minOrder = menuSnapshot.data?.get("minOrder").toString()

                setupView()

                Glide.with(this).load(menuImage).error(R.drawable.default_menu).into(ivMenu)
                tvMenuName.text = menuName
                tvMenuDesc.text = menuDesc
                tvMenuPrice.text = menuPrice.withNumberingFormat()
                etJumlah.setText(minOrder)
                setupKeranjang()
                setupGrupOpsi()
                hitungTotal()
                editJumlah()
            }
        }
    }

    private fun setupKeranjang() {
        if (keranjangId != null) {
            val keranjangRef = db.collection("user").document(userId).collection("keranjang").document(vendorId!!).collection("pesanan").document(keranjangId!!)
            keranjangRef.get().addOnSuccessListener { keranjangSnapshot ->
                if (keranjangSnapshot != null) {
                    val jumlah = keranjangSnapshot.data?.get("jumlah").toString()
                    namaOpsi = keranjangSnapshot.data?.get("namaOpsi").toString()
                    val catatan = keranjangSnapshot.data?.get("catatan").toString()
                    etJumlah.setText(jumlah)
                    etCatatan.setText(catatan)
                    setupView()
                }
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
        grupOpsiAdapter = GrupOpsiAdapter(this, vendorId!!, opsiListCheck, btnAddKeranjang, grupOpsiId!!, menuPrice, etJumlah, namaOpsi)
        recyclerView.adapter = grupOpsiAdapter
    }

    private fun editJumlah() {
        etJumlah.allChangedListener { jumlah ->
            if (jumlah.toInt() > minOrder.toInt()) {
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
        var jumlahTotal = etJumlah.text.toString().toLong()

        total = subtotal * jumlahTotal
        btnAddKeranjang.text = getString(R.string.btn_add_keranjang, total.withNumberingFormat())

        etJumlah.allChangedListener {
            jumlahTotal = it.toLong()
            subtotal = menuPrice.toLong()
            btnAddKeranjang.text = getString(R.string.btn_add_keranjang, total.withNumberingFormat())
            if (opsiListCheck.size <= 0) {
                total = subtotal * jumlahTotal
                btnAddKeranjang.text = getString(R.string.btn_add_keranjang, total.withNumberingFormat())
            } else {
                for (i in opsiListCheck) {
                    subtotal += i.harga!!.toLong()
                    total = subtotal * jumlahTotal
                    btnAddKeranjang.text = getString(R.string.btn_add_keranjang, total.withNumberingFormat())
                }
            }
        }

        btnAddKeranjang.isEnabled = grupOpsiId?.size == opsiListCheck.size
    }

    private fun pesan() {
        btnAddKeranjang.setOnClickListener{
            val sJumlah = etJumlah.text.toString().trim()
            val sCatatan = etCatatan.text.toString().trim()
            val namaOpsiList: ArrayList<String> = ArrayList()
            for (i in opsiListCheck) {
                namaOpsiList.add(i.nama!!)
            }
            val sNamaOpsi = namaOpsiList.stream().collect(
                Collectors.joining(", ")
            )!!

            val jumlahTotal = sJumlah.toLong()
            var subtotal = menuPrice.toLong()
            if (opsiListCheck.size <= 0) {
                totalHarga = subtotal * jumlahTotal
            } else {
                for (i in opsiListCheck) {
                    subtotal += i.harga!!.toLong()
                    totalHarga = subtotal * jumlahTotal
                }
            }

            val keranjangMap = mapOf(
                "kategoriMenuId" to kategoriId,
                "menuId" to menuId,
                "namaMenu" to menuName,
                "foto" to menuImage,
                "jumlah" to sJumlah,
                "namaOpsi" to sNamaOpsi,
                "catatan" to sCatatan,
                "hargaPerPorsi" to subtotal.toString(),
                "subtotal" to totalHarga.toString()
            )
            val keranjangRef = db.collection("user").document(userId).collection("keranjang").document(vendorId!!).collection("pesanan").document()
            keranjangRef.set(keranjangMap).addOnSuccessListener {
                finish()
            } .addOnFailureListener {
                Toast.makeText(this, R.string.failed_add_keranjang, Toast.LENGTH_SHORT).show()
            }

            /*val intent = Intent(this, PemesananActivity::class.java)
            intent.putExtra(Cons.EXTRA_ID, vendorId)
            intent.putExtra(Cons.EXTRA_SEC_ID, kategoriId)
            intent.putExtra(Cons.EXTRA_THIRD_ID, menuId)
            intent.putExtra(Cons.EXTRA_FOURTH_ID, alamatId)
            intent.putExtra(Cons.EXTRA_ONGKIR, ongkir)
            intent.putExtra(Cons.EXTRA_NAMA, menuName)
            intent.putExtra(Cons.EXTRA_DESC, menuDesc)
            intent.putExtra(Cons.EXTRA_JUMLAH_PESANAN, sJumlah)
            intent.putStringArrayListExtra(Cons.EXTRA_NAMA_OPSI, sNamaOpsi)
            intent.putExtra(Cons.EXTRA_CATATAN, sCatatan)
            intent.putExtra(Cons.EXTRA_TOTAL, totalHarga.toString())
            intent.putExtra(Cons.EXTRA_SUBTOTAL, subtotal.toString())
            startActivity(intent)*/
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
            dialog.show(this.supportFragmentManager, "editTextCatatanDialog")
        }
        etJumlah.setOnClickListener {
            val args = Bundle()
            val sJumlah = etJumlah.text.toString().trim()
            args.putString("jumlah", sJumlah)
            args.putString("minOrder", minOrder)
            val dialog: DialogFragment = EditTextJumlahFragment()
            dialog.arguments = args
            dialog.show(this.supportFragmentManager, "editTextJumlahDialog")
        }
    }

    override fun getCatatan(catatan: String) {
        etCatatan.setText(catatan)
    }

    override fun getJumlah(jumlah: String) {
        etJumlah.setText(jumlah)
    }
}