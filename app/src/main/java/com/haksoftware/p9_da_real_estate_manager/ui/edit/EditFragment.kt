package com.haksoftware.p9_da_real_estate_manager.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentEditBinding
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        val editViewModel = ViewModelProvider(this, viewModelFactory)[EditViewModel::class.java]

        _binding = FragmentEditBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}