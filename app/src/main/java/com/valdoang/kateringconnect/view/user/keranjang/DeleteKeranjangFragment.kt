package com.valdoang.kateringconnect.view.user.keranjang

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentAlertDialogBinding
import com.valdoang.kateringconnect.utils.Cons


class DeleteKeranjangFragment : DialogFragment() {

    private var _binding: FragmentAlertDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId = ""
    private var arrayVendorId: ArrayList<String> = ArrayList()
    private var alamatId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAlertDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser!!.uid

        val mArgs = arguments
        arrayVendorId = mArgs!!.getStringArrayList("arrayVendorId")!!
        alamatId = mArgs.getString("alamatId")

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        setTvText()
        closeDialog()
        deleteKeranjang()

        return root
    }

    private fun setTvText() {
        binding.tvAlert.text = resources.getString(R.string.delete_keranjang_alert)
    }

    private fun closeDialog() {
        binding.btnNo.setOnClickListener{
            dismiss()
        }
    }

    private fun deleteKeranjang() {
        binding.btnYes.setOnClickListener {
            Intent(activity, AllKeranjangActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra(Cons.EXTRA_ID, alamatId)
                activity?.startActivity(intent)
            }
            for (i in arrayVendorId) {
                val keranjangRef = db.collection("user").document(userId).collection("keranjang").document(i)
                val pesananRef = keranjangRef.collection("pesanan")
                pesananRef.get().addOnSuccessListener {
                    if (it != null) {
                        for (data in it.documents) {
                            pesananRef.document(data.id).delete()
                        }
                    }
                }
                keranjangRef.delete()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}