package com.valdoang.kateringconnect.view.admin.ui.pengaturan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.FragmentAdminPengaturanBinding
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.view.all.alertdialog.LogoutFragment

class AdminPengaturanFragment : Fragment() {

    private var _binding: FragmentAdminPengaturanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var adminId = firebaseAuth.currentUser!!.uid
    private var db = Firebase.firestore
    private var potongan: String? = null
    private var minTopUp: String? = null
    private var adminTopUp: String? = null
    private var minTarikDana: String? = null
    private var adminTarikDana: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPengaturanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupData()
        setupAction()
        logout()

        return root
    }

    private fun setupData() {
        val adminRef = db.collection("user").document(adminId)
        adminRef.addSnapshotListener { adminSnapshot, _ ->
            if (adminSnapshot != null) {
                potongan = adminSnapshot.data?.get("potongan").toString()
                minTopUp = adminSnapshot.data?.get("minTopUp").toString()
                adminTopUp = adminSnapshot.data?.get("adminTopUp").toString()
                minTarikDana = adminSnapshot.data?.get("minTarikDana").toString()
                adminTarikDana = adminSnapshot.data?.get("adminTarikDana").toString()

                binding.tvPotonganValue.text = getString(R.string.persen_text, potongan)
                binding.tvMinTopUpValue.text = getString(R.string.rupiah_text, minTopUp!!.withNumberingFormat())
                binding.tvAdminTopUpValue.text = getString(R.string.rupiah_text, adminTopUp!!.withNumberingFormat())
                binding.tvMinTarikDanaValue.text = getString(R.string.rupiah_text, minTarikDana!!.withNumberingFormat())
                binding.tvAdminTarikDanaValue.text = getString(R.string.rupiah_text, adminTarikDana!!.withNumberingFormat())
            }
        }
    }

    private fun setupAction() {
        binding.tvAturPotongan.setOnClickListener {
            val intent = Intent(requireContext(), AturPotonganActivity::class.java)
            intent.putExtra(Cons.EXTRA_NAMA, potongan)
            startActivity(intent)
        }
    }

    private fun logout() {
        binding.ibLogout.setOnClickListener{
            val dialog = LogoutFragment()
            dialog.show(this.parentFragmentManager, "logoutDialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}