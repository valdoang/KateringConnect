package com.valdoang.kateringconnect.view.user.pemesanan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityPembayaranDigitalBinding
import com.valdoang.kateringconnect.utils.Cons

class PembayaranDigitalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPembayaranDigitalBinding
    //TODO: LANJUTKAN DI PART 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranDigitalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupMidtrans()
    }

    private fun setupMidtrans() {
        SdkUIFlowBuilder.init()
            .setClientKey(Cons.MIDTRANS_CLIENT_KEY)
            .setContext(applicationContext)
            .setTransactionFinishedCallback(TransactionFinishedCallback { result ->
                //logic code
            })
            .setMerchantBaseUrl(Cons.MIDTRANS_BASE_URL)
            .enableLog(true)
            .setLanguage("id")
            .buildSDK()
    }
}