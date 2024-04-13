package com.valdoang.kateringconnect.view.both.akun

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityEditAkunBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class EditAkunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAkunBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAkunBinding.inflate(layoutInflater)
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