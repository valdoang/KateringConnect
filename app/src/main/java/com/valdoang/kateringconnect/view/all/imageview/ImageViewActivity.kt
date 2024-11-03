package com.valdoang.kateringconnect.view.all.imageview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.module.AppGlideModule
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityImageViewBinding
import com.valdoang.kateringconnect.utils.Cons

class ImageViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewBinding
    private var foto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        foto = intent.getStringExtra(Cons.EXTRA_NAMA)

        setupData()
        setupAction()
    }

    private fun setupData() {
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(this).load(foto).error(R.drawable.failed_load).placeholder(circularProgressDrawable).into(binding.imageView)
    }

    private fun setupAction() {
        binding.ibClose.setOnClickListener {
            finish()
        }
    }
}