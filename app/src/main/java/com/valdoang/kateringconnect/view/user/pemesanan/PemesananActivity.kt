@file:Suppress("DEPRECATION")

package com.valdoang.kateringconnect.view.user.pemesanan

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
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
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.PesananAdapter
import com.valdoang.kateringconnect.databinding.ActivityPemesananBinding
import com.valdoang.kateringconnect.model.Keranjang
import com.valdoang.kateringconnect.utils.*
import com.valdoang.kateringconnect.view.user.custommenu.CustomMenuActivity
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

        setupAction()
        setupView()
        initAction()
        setupPemesanan()
        setupRangkumanPesanan()
        datePicker()
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
        btnPesan.setOnClickListener {
            val sStatus = getString(R.string.status_proses)
            val sDate = calendar.timeInMillis.toString()
            val sMetodePembayaran = getString(R.string.tunai)

            //TODO: PERBAIKI PEMESANANMAP NYA KIRIM KE DOCUMENT DALAM COLLECTION PESANAN
            //TODO: UNTUK SAVE DATABASE NYA KIRIM KE COLLECTION PESANAN DAN BUAT COLLECTION BARU DENGAN NAMA SELURUHPESANAN
            //TODO: PADA SELURUHMAP GUNAKAN PERULANGAN
            //TODO: TAMBAHKAN METODE PEMBAYARAN MENGGUNAKAN MIDTRANS

            /*val pemesananMap = hashMapOf(
                "kategoriId" to kategoriId,
                "menuId" to menuId,
                "menuNama" to namaMenu,
                "namaOpsi" to sNamaOpsi,
                "menuKeterangan" to deskMenu,
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
                "jumlah" to jumlahPesanan,
                "catatan" to catatan,
                "jadwal" to sDate,
                "ongkir" to ongkir.toString(),
                "subtotal" to subtotal,
                "hargaPerPorsi" to hargaPerPorsi,
                "metodePembayaran" to sMetodePembayaran,
                "totalHarga" to totalHarga.toString()
            )

            progressBar.visibility = View.VISIBLE
            db.collection("pesanan").document()
                .set(pemesananMap).addOnSuccessListener {
                    val newFragment: DialogFragment = PemesananBerhasilFragment()
                    newFragment.show(supportFragmentManager, "TAG")
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, R.string.fail_pemesanan, Toast.LENGTH_SHORT).show()
                }*/
        }
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
    }
}