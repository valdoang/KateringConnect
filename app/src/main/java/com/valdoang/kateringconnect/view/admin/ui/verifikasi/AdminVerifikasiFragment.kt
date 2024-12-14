package com.valdoang.kateringconnect.view.admin.ui.verifikasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.VerifikasiAdapter
import com.valdoang.kateringconnect.databinding.FragmentAdminVerifikasiBinding
import com.valdoang.kateringconnect.model.User
import com.valdoang.kateringconnect.utils.Cons

class AdminVerifikasiFragment : Fragment() {

    private var _binding: FragmentAdminVerifikasiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private var userList: ArrayList<User> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var verifikasiAdapter: VerifikasiAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAdminVerifikasiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        progressBar = binding.progressBar

        setupView()
        setupData()

        return root
    }

    private fun setupView() {
        recyclerView = binding.rvVerifikasi
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        verifikasiAdapter = VerifikasiAdapter(requireContext())
        recyclerView.adapter = verifikasiAdapter
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val userRef = db.collection("user").whereEqualTo("statusPendaftaran", getString(R.string.verifikasi))
        userRef.addSnapshotListener { userSnapshot, _ ->
            progressBar.visibility = View.GONE
            if (userSnapshot != null) {
                userList.clear()
                for (data in userSnapshot.documents) {
                    val user: User? = data.toObject(User::class.java)
                    if (user != null) {
                        user.id = data.id
                        userList.add(user)
                    }
                }

                verifikasiAdapter.setItems(userList)
                verifikasiAdapter.setOnItemClickCallback(object :
                    VerifikasiAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: User) {
                        val intent = Intent(requireContext(), DetailVerifikasiActivity::class.java)
                        intent.putExtra(Cons.EXTRA_ID, data.id)
                        startActivity(intent)
                    }
                })

                if (userList.isEmpty()) {
                    binding.noDataAnimation.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.VISIBLE

                }
                else {
                    binding.noDataAnimation.visibility = View.GONE
                    binding.tvNoData.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}