package com.valdoang.kateringconnect.view.all.kcwallet

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityTarikDanaBinding
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

        edNominal.allChangedListener { nominal ->
            val minTopUp = 20000.0

            if (nominal == "") {
                btnKonfirmasi.isEnabled = false
            } else {
                btnKonfirmasi.isEnabled = nominal.toLong() >= minTopUp
            }
        }

        setupMidtrans()
        setupDataUser()
        topUp()
        closeDialog()
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
        val sJenis = getString(R.string.kredit)
        val sKeterangan = getString(R.string.topup_kc_wallet)
        val sNominal = edNominal.text.toString().trim()

        val mutasiMap = hashMapOf(
            "tanggal" to sDate,
            "jenis" to sJenis,
            "keterangan" to sKeterangan,
            "nominal" to sNominal,
        )

        progressBar.visibility = View.VISIBLE
        newMutasi.set(mutasiMap).addOnSuccessListener {
            val newSaldo = saldoUser.toDouble() + sNominal.toDouble()

            val saldoMap = mapOf(
                "saldo" to newSaldo.toString()
            )
            userRef.update(saldoMap)
            finish()
        } .addOnFailureListener {
            progressBar.visibility = View.GONE
            Toast.makeText(this, getString(R.string.fail_topup), Toast.LENGTH_SHORT).show()
        }

    }

    private fun startMidtransPayment() {
        val nominal = edNominal.text.toString().trim()
        val biayaAdmin = 1000.0
        itemDetails.clear()

        val topUpDetail = ItemDetails(
            "topUp",
            nominal.toDouble(),
            1,
            getString(R.string.topup_transaksi)
        )
        itemDetails.add(topUpDetail)

        val adminDetail = ItemDetails(
            "admin",
            biayaAdmin,
            1,
            getString(R.string.admin_transaksi)
        )
        itemDetails.add(adminDetail)

        totalHarga = nominal.toDouble() + biayaAdmin

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