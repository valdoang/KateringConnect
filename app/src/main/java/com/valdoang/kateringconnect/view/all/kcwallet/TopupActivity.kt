package com.valdoang.kateringconnect.view.all.kcwallet

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.valdoang.kateringconnect.databinding.ActivityTopupBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.allChangedListener
import com.valdoang.kateringconnect.utils.withNumberingFormat


class TopupActivity : AppCompatActivity(), TransactionFinishedCallback {
    private lateinit var binding: ActivityTopupBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var db = Firebase.firestore
    private var userId = firebaseAuth.currentUser!!.uid
    private val userRef = db.collection("user").document(userId)
    private val newMutasi = userRef.collection("mutasi").document()
    private var newMutasiId = newMutasi.id
    private var itemDetails: ArrayList<ItemDetails> = ArrayList()
    private var namaUser = ""
    private var alamatUser = ""
    private var kotaUser = ""
    private var nomorUser = ""
    private var emailUser = ""
    private var saldoUser = ""
    private var minTopUp = ""
    private var adminTopUp = ""
    private var totalPemasukanAdmin = ""
    private var totalHarga = 0.0
    private lateinit var progressBar: ProgressBar
    private lateinit var edNominal: EditText
    private lateinit var btnKonfirmasi: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        progressBar = binding.progressBar
        edNominal = binding.edNominal
        btnKonfirmasi = binding.btnKonfirmasi

        setupView()
        setupMidtrans()
        setupDataUser()
        topUp()
        closeDialog()
    }

    private fun setupView() {
        val adminRef = db.collection("user").document(Cons.ADMIN_ID)
        adminRef.addSnapshotListener { adminSnapshot, _ ->
            if (adminSnapshot != null) {
                minTopUp = adminSnapshot.data?.get("minTopUp").toString()
                adminTopUp = adminSnapshot.data?.get("adminTopUp").toString()
                totalPemasukanAdmin = adminSnapshot.data?.get("totalPemasukan").toString()

                binding.tvMinTopup.text = getString(com.valdoang.kateringconnect.R.string.min_top_up, minTopUp.withNumberingFormat())
                binding.tvAdminTopup.text = getString(com.valdoang.kateringconnect.R.string.tv_admin, adminTopUp.withNumberingFormat())

                edNominal.allChangedListener { nominal ->
                    if (nominal == "") {
                        btnKonfirmasi.isEnabled = false
                    } else {
                        btnKonfirmasi.isEnabled = nominal.toDouble() >= minTopUp.toDouble()
                    }
                }
            }
        }
    }

    private fun setupDataUser() {
        val userRef = db.collection("user").document(userId)
        userRef.get().addOnSuccessListener { userSnapshot ->
            if (userSnapshot != null) {
                namaUser = userSnapshot.data?.get("nama").toString()
                kotaUser = userSnapshot.data?.get("kota").toString()
                alamatUser = userSnapshot.data?.get("alamat").toString()
                nomorUser = userSnapshot.data?.get("telepon").toString()
                emailUser = userSnapshot.data?.get("email").toString()
                saldoUser = userSnapshot.data?.get("saldo").toString()

                if (saldoUser == "null") {
                    saldoUser = "0"
                }
            }
        }
    }

    private fun topUp() {
        btnKonfirmasi.setOnClickListener {
            startMidtransPayment()
        }
    }

    private fun addMutasiIntoDatabase() {
        val sDate = System.currentTimeMillis().toString()
        val sJenis = getString(com.valdoang.kateringconnect.R.string.kredit)
        val sKeterangan = getString(com.valdoang.kateringconnect.R.string.topup_kc_wallet)
        val sNominal = edNominal.text.toString().trim()

        val mutasiMap = hashMapOf(
            "tanggal" to sDate,
            "jenis" to sJenis,
            "keterangan" to sKeterangan,
            "nominal" to sNominal,
        )

        progressBar.visibility = View.VISIBLE
        newMutasi.set(mutasiMap).addOnSuccessListener {
            val newSaldo = saldoUser.toLong() + sNominal.toLong()

            val saldoMap = mapOf(
                "saldo" to newSaldo.toString()
            )
            userRef.update(saldoMap)
            adminAddPemasukan()
            finish()
        } .addOnFailureListener {
            progressBar.visibility = View.GONE
            Toast.makeText(this, getString(com.valdoang.kateringconnect.R.string.fail_topup), Toast.LENGTH_SHORT).show()
        }

    }

    private fun adminAddPemasukan() {
        val sDate = System.currentTimeMillis().toString()
        val sKeterangan = getString(com.valdoang.kateringconnect.R.string.biaya_admin_topup)
        val sNominal = adminTopUp

        val mutasiMap = hashMapOf(
            "tanggal" to sDate,
            "keterangan" to sKeterangan,
            "nominal" to sNominal,
        )

        val adminRef = db.collection("user").document(Cons.ADMIN_ID)
        val newPemasukanRef = adminRef.collection("pemasukan").document()
        newPemasukanRef.set(mutasiMap).addOnSuccessListener {
            val newTotalPemasukan = totalPemasukanAdmin.toLong() + sNominal.toLong()

            val totalPemasukanMap = mapOf(
                "totalPemasukan" to newTotalPemasukan.toString()
            )
            adminRef.update(totalPemasukanMap)
        }
    }

    private fun startMidtransPayment() {
        val nominal = edNominal.text.toString().trim()
        itemDetails.clear()

        val topUpDetail = ItemDetails(
            "topUp",
            nominal.toDouble(),
            1,
            getString(com.valdoang.kateringconnect.R.string.topup_transaksi)
        )
        itemDetails.add(topUpDetail)

        val adminDetail = ItemDetails(
            "admin",
            adminTopUp.toDouble(),
            1,
            getString(com.valdoang.kateringconnect.R.string.admin_transaksi)
        )
        itemDetails.add(adminDetail)

        totalHarga = nominal.toDouble() + adminTopUp.toDouble()

        val transactionReq = TransactionRequest(newMutasiId, totalHarga)

        uiKitDetails(transactionReq)
        transactionReq.itemDetails = itemDetails

        MidtransSDK.getInstance().transactionRequest = transactionReq
        MidtransSDK.getInstance().startPaymentUiFlow(this)
    }

    private fun setupMidtrans() {
        SdkUIFlowBuilder.init()
            .setClientKey(Cons.MIDTRANS_CLIENT_KEY)
            .setContext(this)
            .setTransactionFinishedCallback(this)
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

    override fun onTransactionFinished(result: TransactionResult?) {
        if (result != null) {
            if (result.response != null) {
                if (result.status == TransactionResult.STATUS_SUCCESS) {
                    addMutasiIntoDatabase()
                } else if (result.status == TransactionResult.STATUS_PENDING || result.status == TransactionResult.STATUS_FAILED) {
                    finish()
                }
            }
        }
    }

    private fun closeDialog() {
        binding.ibBack.setOnClickListener{
            finish()
        }
    }
}