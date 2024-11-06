package com.valdoang.kateringconnect.view.admin.ui.pengaturan

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ActivityEditTextBinding
import com.valdoang.kateringconnect.utils.Cons

class AturPengaturanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTextBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var adminId = firebaseAuth.currentUser!!.uid
    private var db = Firebase.firestore
    private var value: String? = null
    private var aturApa: String? = null
    private lateinit var ibSave: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var editText: EditText
    private lateinit var tvTitle: TextView
    private lateinit var tvName: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        ibSave = binding.ibSave
        progressBar = binding.progressBar
        editText = binding.edAddName
        tvTitle = binding.titleEditText
        tvName = binding.tvName

        value = intent.getStringExtra(Cons.EXTRA_NAMA)
        aturApa = intent.getStringExtra(Cons.EXTRA_SEC_NAMA)

        setupView()
        updatePotongan()
        closeActivity()
    }

    private fun setupView() {
        editText.setText(value)
        editText.inputType = InputType.TYPE_CLASS_NUMBER

        when(aturApa) {
            getString(R.string.atur_potongan_kapital) -> {
                tvTitle.text = getString(R.string.atur_potongan_kapital)
                tvName.text = getString(R.string.atur_potongan_persen)
            }
            getString(R.string.atur_min_top_up) -> {
                tvTitle.text = getString(R.string.atur_min_top_up)
                tvName.text = getString(R.string.min_top_up_rupiah)
            }
            getString(R.string.atur_admin_top_up) -> {
                tvTitle.text = getString(R.string.atur_admin_top_up)
                tvName.text = getString(R.string.admin_top_up_rupiah)
            }
            getString(R.string.atur_min_tarik_dana) -> {
                tvTitle.text = getString(R.string.atur_min_tarik_dana)
                tvName.text = getString(R.string.min_tarik_dana_rupiah)
            }
            getString(R.string.atur_admin_tarik_dana) -> {
                tvTitle.text = getString(R.string.atur_admin_tarik_dana)
                tvName.text = getString(R.string.admin_tarik_dana_rupiah)
            }
        }
    }

    private fun updatePotongan() {
        ibSave.setOnClickListener {
            val sValue = editText.text.toString().trim()
            var sKey = ""
            when(aturApa) {
                getString(R.string.atur_potongan_kapital) -> {
                    sKey = getString(R.string.potongan_db)
                }
                getString(R.string.atur_min_top_up) -> {
                    sKey = getString(R.string.min_top_up_db)
                }
                getString(R.string.atur_admin_top_up) -> {
                    sKey = getString(R.string.admin_top_up_db)
                }
                getString(R.string.atur_min_tarik_dana) -> {
                    sKey = getString(R.string.min_tarik_dana_db)
                }
                getString(R.string.atur_admin_tarik_dana) -> {
                    sKey = getString(R.string.admin_tarik_dana_db)
                }
            }

            val updateMap = mapOf(
                sKey to sValue
            )

            when {
                sValue.isEmpty() -> {
                    editText.error = getString(R.string.tidak_boleh_kosong)
                }
                else -> {
                    ibSave.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE

                    val adminRef = db.collection("user").document(adminId)
                    adminRef.update(updateMap).addOnSuccessListener {
                        finish()
                    } .addOnFailureListener {
                        Toast.makeText(this, R.string.gagal_mengubah, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun closeActivity() {
        binding.ibClose.setOnClickListener {
            finish()
        }
    }
}