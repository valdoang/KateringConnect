package com.valdoang.kateringconnect.view.admin.ui.verifikasi

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentDetailGaleriBinding

class DetailPhotoFragment : DialogFragment() {
    private var _binding: FragmentDetailGaleriBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var ivFoto: ImageView
    private lateinit var btnDeletePhoto: Button
    private var foto: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailGaleriBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        ivFoto = binding.ivFoto
        btnDeletePhoto = binding.btnDeletePhoto
        btnDeletePhoto.visibility = View.GONE

        val mArgs = arguments
        foto = mArgs!!.getString("foto")

        setData()
        return root
    }

    private fun setData() {
        Glide.with(requireContext()).load(foto).placeholder(R.drawable.gallery).error(R.drawable.gallery).into(ivFoto)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}