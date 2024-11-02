package com.valdoang.kateringconnect.view.admin.ui.transferdana

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
import com.valdoang.kateringconnect.adapter.RiwayatTarikDanaAdapter
import com.valdoang.kateringconnect.adapter.TransferDanaAdapter
import com.valdoang.kateringconnect.adapter.UserBerandaAdapter
import com.valdoang.kateringconnect.databinding.FragmentAdminTransferDanaBinding
import com.valdoang.kateringconnect.model.TarikDana
import com.valdoang.kateringconnect.model.Vendor
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.admin.detailtransferdana.DetailTransferDanaActivity
import com.valdoang.kateringconnect.view.user.detailvendor.DetailVendorActivity

class AdminTransferDanaFragment : Fragment() {

    private var _binding: FragmentAdminTransferDanaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId = ""
    private var transferDanaList: ArrayList<TarikDana> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var transferDanaAdapter: TransferDanaAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAdminTransferDanaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid
        progressBar = binding.progressBar

        setupView()
        setupData()

        return root
    }

    private fun setupView() {
        recyclerView = binding.rvAdminTransferDana
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        transferDanaAdapter = TransferDanaAdapter(requireContext())
        recyclerView.adapter = transferDanaAdapter
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val tarikDanaRef = db.collection("tarikDana").whereEqualTo("status", getString(R.string.status_proses))
        tarikDanaRef.addSnapshotListener { transferDanaSnapshot, _ ->
            progressBar.visibility = View.GONE
            if (transferDanaSnapshot != null) {
                transferDanaList.clear()
                for (data in transferDanaSnapshot.documents) {
                    val transferDana: TarikDana? = data.toObject(TarikDana::class.java)
                    if (transferDana != null) {
                        transferDana.id = data.id
                        transferDanaList.add(transferDana)
                    }
                }

                transferDanaList.sortBy {
                    it.tanggalPengajuan
                }

                transferDanaAdapter.setItems(transferDanaList)
                transferDanaAdapter.setOnItemClickCallback(object :
                    TransferDanaAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: TarikDana) {
                        val intent = Intent(requireContext(), DetailTransferDanaActivity::class.java)
                        intent.putExtra(Cons.EXTRA_ID, data.id)
                        startActivity(intent)
                    }
                })

                if (transferDanaList.isEmpty()) {
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