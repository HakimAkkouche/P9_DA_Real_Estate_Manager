package com.haksoftware.p9_da_real_estate_manager.ui.real_estates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentRealEstatesBinding
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory

class RealEstatesFragment : Fragment(), MenuProvider, OnItemClickListener {

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

    /**
     * Called by the [MenuHost] to allow the [MenuProvider]
     * to inflate [MenuItem]s into the menu.
     *
     * @param menu         the menu to inflate the new menu items into
     * @param menuInflater the inflater to be used to inflate the updated menu
     */
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main, menu)
    }

    /**
     * Called by the [MenuHost] when a [MenuItem] is selected from the menu.
     *
     * @param menuItem the menu item that was selected
     * @return `true` if the given menu item is handled by this menu provider,
     * `false` otherwise
     */
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_add -> {
                val navController = activity?.findNavController(R.id.nav_host_fragment_content_main)
                navController?.navigate(R.id.nav_add)
                true
            }
            else -> false
        }
    }
}