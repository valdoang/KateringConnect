package com.valdoang.kateringconnect.view.user.tambahporsi

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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
import com.valdoang.kateringconnect.databinding.FragmentTambahPorsiBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.allChangedListener
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.view.user.pemesanan.PemesananBerhasilActivity
import java.util.ArrayList

class TambahPorsiFragment : DialogFragment(), TransactionFinishedCallback {
    private var _binding: FragmentTambahPorsiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var userId = ""
    private var db = Firebase.firestore
    private var jumlah = ""
    private var subtotal = ""
    private lateinit var etJumlah: EditText
    private lateinit var tvTotalPembayaran: TextView
    private var total = 0L
    private var sJumlah = 0L
    private var sSubtotal = 0L
    private lateinit var rgMetodePembayaran: RadioGroup
    private lateinit var btnPesan: Button
    private var itemDetails: ArrayList<ItemDetails> = ArrayList()
    private var pesananId: String? = null
    private var menuPesananId: String? = null
    private var namaMenu: String? = null
    private var namaUser = ""
    private var nomorUser = ""
    private var alamatUser = ""
    private var kotaUser = ""
    private var emailUser = ""
    private var metodePembayaran = ""
    private lateinit var tunai: RadioButton
    private lateinit var digital: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTambahPorsiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid

        val mArgs = arguments
        pesananId = mArgs!!.getString("pesananId")
        menuPesananId = mArgs.getString("menuPesananId")
        namaMenu = mArgs.getString("namaMenu")

        etJumlah = binding.edJumlah
        tvTotalPembayaran = binding.tvTotalPembayaran
        rgMetodePembayaran = binding.rgMetodePembayaran
        btnPesan = binding.btnPesan
        tunai = binding.tunai
        digital = binding.digital

        val checkListener =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked && etJumlah.text.toString() != "") {
                    btnPesan.isEnabled = true
                }
            }

        binding.tunai.setOnCheckedChangeListener(checkListener)
        binding.digital.setOnCheckedChangeListener(checkListener)

        setupMidtrans()
        setupUserData()
        closeDialog()
        setupData()
        tambahPorsi()
        return root
    }

    private fun setupUserData() {
        val pesananRef = db.collection("pesanan").document(pesananId!!)
        pesananRef.get().addOnSuccessListener { pesananSnapshot ->
            if (pesananSnapshot != null) {
                namaUser = pesananSnapshot.data?.get("userNama").toString()
                nomorUser = pesananSnapshot.data?.get("userTelepon").toString()
                alamatUser = pesananSnapshot.data?.get("userAlamat").toString()
                kotaUser = pesananSnapshot.data?.get("userKota").toString()
                metodePembayaran = pesananSnapshot.data?.get("metodePembayaran").toString()

                when (metodePembayaran) {
                    getString(R.string.tunai) -> {
                        tunai.isChecked = true
                        digital.isEnabled = false
                    }
                    getString(R.string.digital) -> {
                        digital.isChecked = true
                        tunai.isEnabled = false
                    }
                }
            }
        }

        val userRef = db.collection("user").document(userId)
        userRef.get().addOnSuccessListener { userSnapshot ->
            if (userSnapshot != null) {
                emailUser = userSnapshot.data?.get("email").toString()
            }
        }
    }

    private fun setupData() {
        val ref = db.collection("pesanan").document(pesananId!!).collection("menuPesanan").document(menuPesananId!!)
        ref.get().addOnSuccessListener { document ->
                if (document != null) {
                    jumlah = document.data?.get("jumlah").toString()
                    subtotal = document.data?.get("subtotal").toString()
                    val hargaPerPorsi = document.data?.get("hargaPerPorsi").toString()

                    etJumlah.allChangedListener { etjumlah ->
                        if (etjumlah == "") {
                            tvTotalPembayaran.text = "0"
                            btnPesan.isEnabled = false
                        } else {
                            total = etjumlah.toLong() * hargaPerPorsi.toLong()
                            tvTotalPembayaran.text = total.withNumberingFormat()

                            if (rgMetodePembayaran.checkedRadioButtonId != -1) {
                                btnPesan.isEnabled = true
                            }

                            //untuk updateMap
                            sJumlah = etjumlah.toLong() + jumlah.toLong()
                            sSubtotal = total + subtotal.toLong()
                        }
                    }
                }
            }
    }

    private fun tambahPorsi() {
        binding.btnPesan.setOnClickListener {
            when (metodePembayaran) {
                getString(R.string.tunai) -> {
                    addTambahPorsiIntoDatabase()
                }
                getString(R.string.digital) -> {
                    startMidtransPayment()
                }
            }
        }
    }

    private fun addTambahPorsiIntoDatabase() {
        val updateMap = mapOf(
            "jumlah" to sJumlah.toString(),
            "subtotal" to sSubtotal.toString(),
            "tambahPorsi" to true
        )

        db.collection("pesanan").document(pesananId!!).collection("menuPesanan").document(menuPesananId!!)
            .update(updateMap).addOnSuccessListener {
                dismiss()
                val intent = Intent(requireContext(), PemesananBerhasilActivity::class.java)
                intent.putExtra(Cons.EXTRA_NAMA, getString(R.string.from_tambah_porsi))
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), R.string.fail_penambahan, Toast.LENGTH_SHORT).show()
            }
    }

    private fun startMidtransPayment() {
        itemDetails.clear()
        val sJumlah = etJumlah.text.toString()

        val detail = ItemDetails(
            menuPesananId,
            total.toDouble(),
            1,
            getString(R.string.jumlah_porsi_transaksi, sJumlah, namaMenu)
        )
        itemDetails.add(detail)

        val transactionReq = TransactionRequest(System.currentTimeMillis().toString(), total.toDouble())

        uiKitDetails(transactionReq)
        transactionReq.itemDetails = itemDetails

        MidtransSDK.getInstance().transactionRequest = transactionReq
        MidtransSDK.getInstance().startPaymentUiFlow(context)
    }

    private fun setupMidtrans() {
        SdkUIFlowBuilder.init()
            .setClientKey(Cons.MIDTRANS_CLIENT_KEY)
            .setContext(context)
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
                    addTambahPorsiIntoDatabase()
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