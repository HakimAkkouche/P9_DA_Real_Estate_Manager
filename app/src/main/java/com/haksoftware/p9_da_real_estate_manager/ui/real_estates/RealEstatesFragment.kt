package com.haksoftware.p9_da_real_estate_manager.ui.real_estates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentRealEstatesBinding
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory

class RealEstatesFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentRealEstatesBinding? = null


    private val binding get() = _binding!!
    private lateinit var realEstateAdapter: RealEstateAdapter
    private lateinit var realEstatesViewModel: RealEstatesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        realEstatesViewModel = ViewModelProvider(this, viewModelFactory)[RealEstatesViewModel::class.java]

        _binding = FragmentRealEstatesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        setupFab()

        observeViewModel()
        return root
    }
    private fun setupRecyclerView() {
        realEstateAdapter = RealEstateAdapter(this as OnItemClickListener)
        binding.recyclerViewRealEstates.apply {
            adapter = realEstateAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            val navController = activity?.findNavController(R.id.nav_host_fragment_content_main)
            navController?.navigate(R.id.nav_add)
        }
    }
    private fun observeViewModel() {
        realEstatesViewModel.realEstates.observe(viewLifecycleOwner) { realEstates ->
            realEstateAdapter.submitList(realEstates)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onItemClick(realEstate: RealEstateWithDetails) {
        findNavController().navigate(RealEstatesFragmentDirections.actionNavRealEstatesToNavDetail(realEstate))
    }
}