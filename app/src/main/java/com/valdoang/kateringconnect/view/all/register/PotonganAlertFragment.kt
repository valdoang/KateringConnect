package com.valdoang.kateringconnect.view.all.register

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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


class PotonganAlertFragment : DialogFragment() {

    private var _binding: FragmentAlertDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var mCallback: GetAnswer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAlertDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root
        firebaseAuth = FirebaseAuth.getInstance()

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        setTvText()
        closeDialog()
        register()

        return root
    }

    private fun setTvText() {
        val adminRef = db.collection("user").document(Cons.ADMIN_ID)
        adminRef.addSnapshotListener { adminSnapshot, _ ->
            if (adminSnapshot != null) {
                val potongan = adminSnapshot.data?.get("potongan").toString()
                binding.tvAlert.text = resources.getString(R.string.potongan_alert, potongan)
            }
        }
    }

    private fun closeDialog() {
        binding.btnNo.setOnClickListener{
            mCallback?.getAnswer(getString(R.string.tidak))
            dismiss()
        }
    }

    private fun register() {
        binding.btnYes.setOnClickListener{
            mCallback?.getAnswer(getString(R.string.ya))
            dismiss()
        }
    }

    interface GetAnswer {
        fun getAnswer(answer: String)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mCallback = activity as GetAnswer
        } catch (e: ClassCastException) {
            Log.d("MyDialog", "Activity doesn't implement the GetCatatan interface")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}