package com.valdoang.kateringconnect.view.both.alertdialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentAlertDialogBinding
import com.valdoang.kateringconnect.view.both.login.LoginActivity


class LogoutFragment : DialogFragment() {

    private var _binding: FragmentAlertDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

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
        logout()

        return root
    }

    private fun setTvText() {
        binding.tvAlert.text = resources.getString(R.string.logout_alert)
    }

    private fun closeDialog() {
        binding.btnNo.setOnClickListener{
            dismiss()
        }
    }

    private fun logout() {
        binding.btnYes.setOnClickListener{
            firebaseAuth.signOut()
            Intent(activity, LoginActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity?.startActivity(intent)
            }
            Toast.makeText(activity, R.string.success_signout, Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}