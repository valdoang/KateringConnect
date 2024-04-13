package com.valdoang.kateringconnect.view.user.main.ui.akun

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentUserAkunBinding
import com.valdoang.kateringconnect.view.both.akun.EditAkunActivity
import com.valdoang.kateringconnect.view.both.login.LoginActivity

class UserAkunFragment : Fragment() {

    private var _binding: FragmentUserAkunBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserAkunBinding.inflate(inflater, container, false)
        val root: View = binding.root
        firebaseAuth = FirebaseAuth.getInstance()

        setupAction()
        return root
    }

    private fun setupAction() {
        binding.btnUserEditAkun.setOnClickListener{
            val intent = Intent(requireContext(), EditAkunActivity::class.java)
            startActivity(intent)
        }
        binding.btnUserLogout.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(resources.getString(R.string.keluar_title))
            builder.setMessage(resources.getString(R.string.keluar_message))
            builder.setPositiveButton(resources.getString(R.string.keluar)) { _, _ ->
                firebaseAuth.signOut()
                Intent(activity, LoginActivity::class.java).also { intent ->
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity?.startActivity(intent)
                }
                Toast.makeText(activity, R.string.success_signout, Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton(resources.getString(R.string.batal)) { dialog, _ ->
                dialog.dismiss()
            }
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}