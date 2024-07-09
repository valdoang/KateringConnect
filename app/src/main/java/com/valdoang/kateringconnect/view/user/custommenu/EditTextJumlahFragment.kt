package com.valdoang.kateringconnect.view.user.custommenu

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.valdoang.kateringconnect.databinding.FragmentEtJumlahBinding
import com.valdoang.kateringconnect.utils.allChangedListener


class EditTextJumlahFragment : DialogFragment() {

    private var _binding: FragmentEtJumlahBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var etJumlah: EditText
    private lateinit var btnKonfirmasi: Button
    private var mCallback: GetJumlah? = null
    private var minOrder: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEtJumlahBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        etJumlah = binding.etJumlah
        btnKonfirmasi = binding.btnKonfirmasi

        val mArgs = arguments
        val jumlah = mArgs!!.getString("jumlah")
        minOrder = mArgs.getString("minOrder")
        etJumlah.setText(jumlah)

        setCatatan()
        closeDialog()
        return root
    }

    private fun setCatatan() {
        etJumlah.allChangedListener { jumlah ->
            if (jumlah == "") {
                btnKonfirmasi.isEnabled = false
            } else {
                btnKonfirmasi.isEnabled = jumlah.toLong() >= minOrder!!.toLong()
            }
        }
        btnKonfirmasi.setOnClickListener {
            val sJumlah = etJumlah.text.toString().trim()
            mCallback?.getJumlah(sJumlah)
            dismiss()
        }
    }

    private fun closeDialog() {
        binding.ibClose.setOnClickListener{
            dismiss()
        }
    }

    interface GetJumlah {
        fun getJumlah(jumlah: String)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mCallback = activity as GetJumlah
        } catch (e: ClassCastException) {
            Log.d("MyDialog", "Activity doesn't implement the GetJumlah interface")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}