package com.valdoang.kateringconnect.view.vendor.detailpesanan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityTagihTunaiBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.withNumberingFormat

class TagihTunaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTagihTunaiBinding
    private var pesananId: String? = null
    private var total: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTagihTunaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        pesananId = intent.getStringExtra(Cons.EXTRA_ID)
        total = intent.getStringExtra(Cons.EXTRA_SEC_ID)

        setupView()
        setupAction()
    }

    private fun setupView() {
        binding.tvTotal.text = getString(R.string.rupiah_text, total?.withNumberingFormat())
    }

    private fun setupAction() {
        binding.btnLanjut.setOnClickListener {
            val intent = Intent(this, SelesaikanPesananActivity::class.java)
            intent.putExtra(Cons.EXTRA_ID, pesananId)
            startActivity(intent)
        }
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}