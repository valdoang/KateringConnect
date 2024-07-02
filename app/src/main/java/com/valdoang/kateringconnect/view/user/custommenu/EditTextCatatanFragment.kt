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
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.valdoang.kateringconnect.databinding.FragmentEtCatatanBinding


class EditTextCatatanFragment : DialogFragment() {

    private var _binding: FragmentEtCatatanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var etCatatan: EditText
    private var mCallback: GetCatatan? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEtCatatanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        etCatatan = binding.etCatatan

        val mArgs = arguments
        val catatan = mArgs!!.getString("catatan")
        etCatatan.setText(catatan)

        setCatatan()
        closeDialog()
        return root
    }

    private fun setCatatan() {
        binding.btnKonfirmasi.setOnClickListener {
            val sCatatan = etCatatan.text.toString().trim()
            mCallback?.getCatatan(sCatatan)
            dismiss()
        }
    }

    private fun closeDialog() {
        binding.ibClose.setOnClickListener{
            dismiss()
        }
    }

    interface GetCatatan {
        fun getCatatan(catatan: String)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mCallback = activity as GetCatatan
        } catch (e: ClassCastException) {
            Log.d("MyDialog", "Activity doesn't implement the GetCatatan interface")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}