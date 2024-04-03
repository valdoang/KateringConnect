package com.dicoding.kateringconnect.view.main.ui.akun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.kateringconnect.databinding.FragmentAkunBinding

class AkunFragment : Fragment() {

    private var _binding: FragmentAkunBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val akunViewModel =
            ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
                AkunViewModel::class.java
            )

        _binding = FragmentAkunBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        akunViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}