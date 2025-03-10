package com.valdoang.kateringconnect.view.user.main.ui.riwayat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.UserPesananRiwayatAdapter
import com.valdoang.kateringconnect.databinding.FragmentUserRiwayatBinding
import com.valdoang.kateringconnect.model.Pesanan
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.all.chat.ChatActivity
import com.valdoang.kateringconnect.view.user.detailpemesanan.DetailPemesananActivity

class UserRiwayatFragment : Fragment() {
    private var _binding: FragmentUserRiwayatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var pesananList: ArrayList<Pesanan>
    private lateinit var userPesananRiwayatAdapter: UserPesananRiwayatAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserRiwayatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = Firebase.auth
        pesananList = arrayListOf()
        progressBar = binding.progressBar

        setupView()
        setupData()
        setupAction()

        return root
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val userId = firebaseAuth.currentUser!!.uid
        val vendorRef = db.collection("pesanan").whereEqualTo("userId", userId).whereIn("status", listOf(getString(
            R.string.status_selesai), getString(R.string.status_batal), getString(R.string.status_ditolak)))
        vendorRef.addSnapshotListener{ snapshot,_ ->
            progressBar.visibility = View.GONE
            if (snapshot != null) {
                pesananList.clear()
                for (data in snapshot.documents) {
                    val pesanan: Pesanan? = data.toObject(Pesanan::class.java)
                    if (pesanan != null) {
                        pesanan.id = data.id
                        pesananList.add(pesanan)
                    }
                }

                pesananList.sortByDescending { pesanan ->
                    pesanan.jadwal
                }

                userPesananRiwayatAdapter.setItems(pesananList)
                userPesananRiwayatAdapter.setOnItemClickCallback(object :
                    UserPesananRiwayatAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Pesanan) {
                        val intent = Intent(requireContext(), DetailPemesananActivity::class.java)
                        intent.putExtra(Cons.EXTRA_ID, data.id)
                        startActivity(intent)
                    }
                })

                if (pesananList.isEmpty()) {
                    binding.noHistoryAnimationUser.visibility = View.VISIBLE
                    binding.tvEmptyData.visibility = View.VISIBLE

                }
                else {
                    binding.noHistoryAnimationUser.visibility = View.GONE
                    binding.tvEmptyData.visibility = View.GONE
                }
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvUserRiwayat
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        userPesananRiwayatAdapter = UserPesananRiwayatAdapter(requireContext())
        recyclerView.adapter = userPesananRiwayatAdapter
        userPesananRiwayatAdapter.setItems(pesananList)
    }

    private fun setupAction() {
        binding.ibChat.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            startActivity(intent)
        }
    }
}