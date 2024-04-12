package com.dicoding.kateringconnect.view.vendor.galeri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.dicoding.kateringconnect.R
import com.dicoding.kateringconnect.databinding.ActivityAddGaleriBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddGaleriActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddGaleriBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGaleriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSimpan.setOnClickListener {
            onBackPressed()
        }

        binding.tvAddPhoto.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_add_photo, null)

            val cvGaleri = view.findViewById<CardView>(R.id.cv_gallery)
            val cvCamera = view.findViewById<CardView>(R.id.cv_camera)

            cvGaleri.setOnClickListener {
                dialog.dismiss()
            }

            cvCamera.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()
        }
    }
}