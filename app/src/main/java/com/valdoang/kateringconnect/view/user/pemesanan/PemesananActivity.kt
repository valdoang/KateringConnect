@file:Suppress("DEPRECATION")

package com.valdoang.kateringconnect.view.user.pemesanan

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.PaymentMethod
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.PesananAdapter
import com.valdoang.kateringconnect.databinding.ActivityPemesananBinding
import com.valdoang.kateringconnect.model.Keranjang
import com.valdoang.kateringconnect.utils.*
import com.valdoang.kateringconnect.view.user.custommenu.CustomMenuActivity
import com.valdoang.kateringconnect.view.user.detailvendor.DetailVendorActivity
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.text.SimpleDateFormat
import java.util.*


class PemesananActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {
    private lateinit var binding: ActivityPemesananBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private val calendar = Calendar.getInstance()
    private var namaUser = ""
    private var alamatUser = ""
    private var kotaUser = ""
    private var nomorUser = ""
    private var emailUser = ""
    private var namaKontakAlamat = ""
    private var nomorKontakAlamat = ""
    private var alamatAlamat = ""
    private var kotaAlamat = ""
    private var fotoUser = ""
    private var namaVendor = ""
    private var alamatVendor = ""
    private var fotoVendor = ""
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private var totalHarga = 0L
    private var userId = ""
    private var vendorId: String? = null
    private var alamatId: String? = null
    private var ongkir: String? = null
    private lateinit var tvTanggal: TextView
    private lateinit var tvJam: TextView
    private lateinit var btnPesan: Button
    private lateinit var ivTanggalError: ImageView
    private lateinit var tvTanggalError: TextView
    private lateinit var progressBar: ProgressBar
    private var pesananList: ArrayList<Keranjang> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var pesananAdapter: PesananAdapter
    private var subtotal = 0L
    private lateinit var rgMetodePembayaran: RadioGroup
    private var newPesananId = ""
    private var itemDetails: ArrayList<ItemDetails> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid

        vendorId = intent.getStringExtra(Cons.EXTRA_ID)
        alamatId = intent.getStringExtra(Cons.EXTRA_SEC_ID)
        ongkir = intent.getStringExtra(Cons.EXTRA_ONGKIR)

        tvTanggal = binding.tvTanggal
        tvJam = binding.tvJam
        btnPesan = binding.btnPesan
        ivTanggalError = binding.ivTanggalError
        tvTanggalError = binding.tvTanggalError
        progressBar = binding.progressBar
        rgMetodePembayaran = binding.rgMetodePembayaran

        setupAction()
        setupView()
        initAction()
        setupPemesanan()
        setupRangkumanPesanan()
        datePicker()
        setupMidtrans()
        pemesanan()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupRangkumanPesanan() {
        val pesananRef = db.collection("user").document(userId).collection("keranjang").document(vendorId!!).collection("pesanan")
        pesananRef.addSnapshotListener { pesananSnapshot, _ ->
            if (pesananSnapshot != null) {
                pesananList.clear()
                subtotal = 0L
                for (data in pesananSnapshot.documents) {
                    val pesanan: Keranjang? = data.toObject(Keranjang::class.java)
                    if (pesanan != null) {
                        pesanan.id = data.id
                        pesananList.add(pesanan)
                    }
                    val subtotalTemp = data.data?.get("subtotal").toString().toLong()
                    subtotal += subtotalTemp
                }

                pesananAdapter.setItems(pesananList)
                pesananAdapter.setOnItemClickCallback(object : PesananAdapter.OnItemClickCallback{
                    override fun onItemClicked(data: Keranjang) {
                        val intent = Intent(this@PemesananActivity, CustomMenuActivity::class.java)
                        intent.putExtra(Cons.EXTRA_ID, vendorId)
                        intent.putExtra(Cons.EXTRA_SEC_ID, data.kategoriMenuId)
                        intent.putExtra(Cons.EXTRA_THIRD_ID, data.menuId)
                        intent.putExtra(Cons.EXTRA_FOURTH_ID, alamatId)
                        intent.putExtra(Cons.EXTRA_FIFTH_ID, data.id)
                        intent.putExtra(Cons.EXTRA_ONGKIR, ongkir)
                        startActivity(intent)
                    }
                })

                if (ongkir!!.toLong() > 180000) {
                    binding.tvJarakError.visibility = View.VISIBLE
                } else {
                    binding.tvJarakError.visibility = View.GONE
                }

                binding.tvOngkirValue.text = ongkir!!.withNumberingFormat()

                totalHarga = ongkir!!.toLong() + subtotal
                binding.totalHarga.text = totalHarga.withNumberingFormat()

                if (pesananList.isEmpty()) {
                    binding.scrollView.visibility = View.GONE
                    binding.clPesan.visibility = View.GONE
                    binding.clCariKatering.visibility = View.VISIBLE
                    binding.noDataAnimation.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.VISIBLE
                } else {
                    binding.scrollView.visibility = View.VISIBLE
                    binding.clPesan.visibility = View.VISIBLE
                    binding.clCariKatering.visibility = View.GONE
                    binding.noDataAnimation.visibility = View.GONE
                    binding.tvNoData.visibility = View.GONE
                }
            }
        }
    }

    private fun setupPemesanan() {
        val vendorRef = db.collection("user").document(vendorId!!)
        vendorRef.get().addOnSuccessListener {vendorSnapshot ->
            if (vendorSnapshot != null) {
                namaVendor = vendorSnapshot.data?.get("nama").toString()
                alamatVendor = vendorSnapshot.data?.get("alamat").toString()
                fotoVendor = vendorSnapshot.data?.get("foto").toString()
                binding.tvVendorName.text = namaVendor
            }
        }

        val userRef = db.collection("user").document(userId)
        userRef.get().addOnSuccessListener { userSnapshot ->
            if (userSnapshot != null) {
                namaUser = userSnapshot.data?.get("nama").toString()
                kotaUser = userSnapshot.data?.get("kota").toString()
                alamatUser = userSnapshot.data?.get("alamat").toString()
                nomorUser = userSnapshot.data?.get("telepon").toString()
                fotoUser = userSnapshot.data?.get("foto").toString()
                emailUser = userSnapshot.data?.get("email").toString()

                binding.tvUserName.text = namaUser
                binding.tvAddress.text = getString(R.string.tv_address_city, alamatUser, kotaUser)
                binding.tvNoPhone.text = nomorUser
            }
        }

        if (alamatId != userId && alamatId != "null") {
            val alamatRef = db.collection("user").document(userId).collection("alamatTersimpan").document(alamatId!!)
            alamatRef.get().addOnSuccessListener { alamatSnapshot ->
                if (alamatSnapshot != null) {
                    namaKontakAlamat = alamatSnapshot.data?.get("namaKontak").toString()
                    nomorKontakAlamat = alamatSnapshot.data?.get("nomorKontak").toString()
                    alamatAlamat = alamatSnapshot.data?.get("alamat").toString()
                    kotaAlamat = alamatSnapshot.data?.get("kota").toString()

                    binding.tvUserName.text = namaKontakAlamat
                    binding.tvAddress.text = getString(R.string.tv_address_city, alamatAlamat, kotaAlamat)
                    binding.tvNoPhone.text = nomorKontakAlamat
                }
            }
        }
    }

    private fun pemesanan() {
        val newPesanan = db.collection("pesanan").document()
        newPesananId = newPesanan.id

        var sMetodePembayaran = ""
        rgMetodePembayaran.setOnCheckedChangeListener { _, checkedId ->
            val rbJenisAkun: RadioButton = findViewById(checkedId)
            when (rbJenisAkun.text) {
                getString(R.string.tunai) -> sMetodePembayaran = getString(R.string.tunai)
                getString(R.string.digital) -> sMetodePembayaran = getString(R.string.digital)
            }
        }

        btnPesan.setOnClickListener {
            when(sMetodePembayaran) {
                getString(R.string.tunai) -> {
                    val intent = Intent(this, PemesananBerhasilActivity::class.java)
                    intent.putExtra(Cons.EXTRA_NAMA, getString(R.string.from_pemesanan))
                    startActivity(intent)
                }
                getString(R.string.digital) -> {
                    itemDetails.clear()
                    for (i in pesananList) {
                        val detail = ItemDetails(
                            i.id,
                            i.subtotal!!.toDouble(),
                            1,
                            getString(R.string.jumlah_porsi_transaksi, i.jumlah, i.namaMenu)
                        )
                        itemDetails.add(detail)
                    }

                    val ongkirDetail = ItemDetails(System.currentTimeMillis().toString(), ongkir!!.toDouble(), 1, getString(R.string.ongkos_kirim_transaksi))
                    itemDetails.add(ongkirDetail)

                    val transactionReq = TransactionRequest(newPesananId, totalHarga.toDouble())

                    uiKitDetails(transactionReq)
                    transactionReq.itemDetails = itemDetails

                    MidtransSDK.getInstance().transactionRequest = transactionReq
                    MidtransSDK.getInstance().startPaymentUiFlow(this)
                    finish()
                }
            }

            val sStatus = getString(R.string.status_proses)
            val sDate = calendar.timeInMillis.toString()

            val pemesananMap = hashMapOf(
                "userId" to userId,
                "userNama" to namaUser,
                "userKota" to kotaUser,
                "userAlamat" to alamatUser,
                "userTelepon" to nomorUser,
                "userFoto" to fotoUser,
                "vendorFoto" to fotoVendor,
                "vendorId" to vendorId,
                "vendorNama" to namaVendor,
                "vendorAlamat" to alamatVendor,
                "status" to sStatus,
                "jadwal" to sDate,
                "ongkir" to ongkir.toString(),
                "metodePembayaran" to sMetodePembayaran
            )

            progressBar.visibility = View.VISIBLE
            newPesanan.set(pemesananMap).addOnSuccessListener {
                for (i in pesananList) {
                    val menuPesananRef = db.collection("pesanan").document(newPesananId).collection("menuPesanan").document(i.id!!)
                    val pesananKeranjangRef = db.collection("user").document(userId).collection("keranjang").document(vendorId!!).collection("pesanan").document(i.id!!)

                    val menuPesananMap = hashMapOf(
                        "kategoriMenuId" to i.kategoriMenuId,
                        "menuId" to i.menuId,
                        "namaMenu" to i.namaMenu,
                        "namaOpsi" to i.namaOpsi,
                        "jumlah" to i.jumlah,
                        "catatan" to i.catatan,
                        "foto" to i.foto,
                        "subtotal" to i.subtotal,
                        "hargaPerPorsi" to i.hargaPerPorsi
                    )
                    menuPesananRef.set(menuPesananMap)
                    pesananKeranjangRef.delete()
                }

                val keranjangRef = db.collection("user").document(userId).collection("keranjang").document(vendorId!!)
                keranjangRef.delete()
            }.addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, getString(R.string.fail_pemesanan), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMidtrans() {
        /*UiKitApi.Builder()
            .withMerchantClientKey(Cons.MIDTRANS_CLIENT_KEY)
            .withContext(this)
            .withMerchantUrl(Cons.MIDTRANS_BASE_URL)
            .enableLog(true)
            .build()*/
        SdkUIFlowBuilder.init()
            .setClientKey(Cons.MIDTRANS_CLIENT_KEY)
            .setContext(this)
            .setTransactionFinishedCallback(TransactionFinishedCallback { result ->
                Log.w(TAG, result.statusMessage)
            })
            .setMerchantBaseUrl(Cons.MIDTRANS_BASE_URL)
            .enableLog(true)
            .setLanguage("id")
            .buildSDK()
    }

    private fun uiKitDetails(transactionRequest: TransactionRequest) {
        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = namaUser
        customerDetails.phone = nomorUser
        customerDetails.firstName = namaUser
        customerDetails.email = emailUser
        val shippingAddress = ShippingAddress()
        shippingAddress.address = alamatUser
        shippingAddress.city = kotaUser
        customerDetails.shippingAddress = shippingAddress
        val billingAddress = BillingAddress()
        billingAddress.address = alamatUser
        billingAddress.city = kotaUser
        customerDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customerDetails
    }

    private fun setupView() {
        recyclerView = binding.rvPesanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        pesananAdapter = PesananAdapter(this)
        recyclerView.adapter = pesananAdapter
        pesananAdapter.setItems(pesananList)
    }

    private fun initAction() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.LEFT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                pesananAdapter.deleteItem(viewHolder.adapterPosition, vendorId!!)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(
                        ContextCompat.getColor(
                            this@PemesananActivity,
                            R.color.red
                        )
                    )
                    .addSwipeLeftLabel(getString(R.string.hapus))
                    .setSwipeLeftLabelColor(ContextCompat.getColor(
                        this@PemesananActivity,
                        R.color.white
                    ))
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun datePicker() {
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val today = dateFormat.format(System.currentTimeMillis()).withDateFormat()
            val datePicked = dateFormat.format(calendar.time).withDateFormat()
            tvTanggal.text = datePicked
            tvTanggal.setTextColor(resources.getColor(R.color.black))
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                ivTanggalError.visibility = View.VISIBLE
                tvTanggalError.visibility = View.VISIBLE
                btnPesan.isEnabled = false
            } else if(today == datePicked) {
                ivTanggalError.visibility = View.VISIBLE
                tvTanggalError.visibility = View.VISIBLE
                btnPesan.isEnabled = false
            } else {
                ivTanggalError.visibility = View.GONE
                tvTanggalError.visibility = View.GONE
                if (tvJam.currentTextColor == resources.getColor(R.color.black)) {
                    btnPesan.isEnabled = true
                }
                timePicker()
            }
        }

        binding.clPilihTanggal.setOnClickListener {
            DatePickerDialog(
                this,
                datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun timePicker() {
        binding.clPilihJam.setOnClickListener{
            val dialogFragment = TimePickerFragment()
            dialogFragment.show(supportFragmentManager, "timePicker")
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        tvJam.text = timeFormat.format(calendar.time)
        tvJam.setTextColor(resources.getColor(R.color.black))
        val today = dateFormat.format(System.currentTimeMillis()).withDateFormat()
        val datePicked = dateFormat.format(calendar.time).withDateFormat()
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            ivTanggalError.visibility = View.VISIBLE
            tvTanggalError.visibility = View.VISIBLE
            btnPesan.isEnabled = false
        } else if(today == datePicked) {
            ivTanggalError.visibility = View.VISIBLE
            tvTanggalError.visibility = View.VISIBLE
            btnPesan.isEnabled = false
        } else {
            ivTanggalError.visibility = View.GONE
            tvTanggalError.visibility = View.GONE
            btnPesan.isEnabled = true
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
        binding.btnCariKatering.setOnClickListener {
            finish()
        }
        binding.tvTambahPesanan.setOnClickListener {
            val intent = Intent(this, DetailVendorActivity::class.java)
            intent.putExtra(Cons.EXTRA_ID, vendorId)
            intent.putExtra(Cons.EXTRA_SEC_ID, alamatId)
            intent.putExtra(Cons.EXTRA_ONGKIR, ongkir)
            startActivity(intent)
        }
    }
}