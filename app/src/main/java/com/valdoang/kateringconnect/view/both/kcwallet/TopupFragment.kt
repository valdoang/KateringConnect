package com.valdoang.kateringconnect.view.both.kcwallet

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
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
import com.valdoang.kateringconnect.databinding.FragmentTopupBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.allChangedListener

class TopupFragment : DialogFragment(), TransactionFinishedCallback {

    private var _binding: FragmentTopupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTopupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

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
        return root
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
            val newSaldo = saldoUser.toLong() + sNominal.toLong()

            val saldoMap = mapOf(
                "saldo" to newSaldo
            )
            userRef.update(saldoMap)
            dismiss()
        } .addOnFailureListener {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), getString(R.string.fail_topup), Toast.LENGTH_SHORT).show()
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
        MidtransSDK.getInstance().startPaymentUiFlow(requireContext())
    }

    private fun setupMidtrans() {
        SdkUIFlowBuilder.init()
            .setClientKey(Cons.MIDTRANS_CLIENT_KEY)
            .setContext(requireContext())
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
                    dismiss()
                }
            }
        }
    }

    private fun closeDialog() {
        binding.ibClose.setOnClickListener{
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}