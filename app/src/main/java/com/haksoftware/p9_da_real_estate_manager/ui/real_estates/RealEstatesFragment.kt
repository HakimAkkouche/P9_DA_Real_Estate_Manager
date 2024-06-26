package com.haksoftware.p9_da_real_estate_manager.ui.real_estates

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentRealEstatesBinding
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.RealEstatesViewModel
import com.haksoftware.p9_da_real_estate_manager.ui.search.SearchCallback
import com.haksoftware.p9_da_real_estate_manager.ui.search.SearchDialogFragment
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory

/**
 * A fragment representing a list of real estates.
 * Implements MenuProvider, SearchCallback, and RealEstateInteractionListener interfaces.
 */
class RealEstatesFragment : Fragment(), MenuProvider, SearchCallback, RealEstateInteractionListener {

    private var _binding: FragmentRealEstatesBinding? = null
    private val binding get() = _binding!!
    private lateinit var realEstateAdapter: RealEstateAdapter
    private val realEstatesViewModel: RealEstatesViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireActivity().application)
    }

    private lateinit var recyclerViewRealEstates: RecyclerView
    private var countViewCreated = 0
    private var idRealEstateSelected = 0

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        realEstatesViewModel.realEstates.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                realEstatesViewModel.updateCurrentRealEstate(it[0])
                requireActivity().invalidateOptionsMenu()
            }
        }

        _binding = FragmentRealEstatesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerViewRealEstates = binding.recyclerViewRealEstates
        observeViewModel()
        countViewCreated++
        return root
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned,
     * but before any saved state has been restored in to the view.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slidingPaneLayout = binding.slidingPaneLayout
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            RealEstateOnBackPressedCallback(slidingPaneLayout, requireActivity())
        )
        setupRecyclerView()
    }

    /**
     * Sets up the RecyclerView with the RealEstateAdapter and layout manager.
     */
    private fun setupRecyclerView() {
        realEstateAdapter = RealEstateAdapter {
            realEstatesViewModel.updateCurrentRealEstate(it)
            idRealEstateSelected = it.realEstate.idRealEstate
            binding.slidingPaneLayout.openPane()
        }
        recyclerViewRealEstates.apply {
            adapter = realEstateAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * Observes the ViewModel's LiveData objects for real estates and search results.
     */
    private fun observeViewModel() {
        realEstatesViewModel.realEstates.observe(viewLifecycleOwner) { realEstates ->
            realEstateAdapter.submitList(realEstates)
        }
        realEstatesViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            realEstateAdapter.submitList(searchResults)

            searchResults?.let {
                Log.d("RealEstatesFragment", "searchResults updated: ${it.size} items")
            } ?: run {
                Log.d("RealEstatesFragment", "searchResults is null")
            }
        }
    }

    /**
     * Called when the view created by this fragment has been detached from the fragment.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Called by the MenuHost to allow the MenuProvider to inflate MenuItems into the menu.
     */
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main, menu)
        if (binding.slidingPaneLayout.isOpen && idRealEstateSelected != 0) {
            menuInflater.inflate(R.menu.fragment_edit_menu, menu)
        }
    }

    /**
     * Called by the MenuHost when a MenuItem is selected from the menu.
     */
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_add -> {
                val navController = activity?.findNavController(R.id.nav_host_fragment_content_main)
                navController?.navigate(R.id.nav_add)
                true
            }
            R.id.action_search -> {
                val dialog = SearchDialogFragment(this)
                dialog.show(this.parentFragmentManager, "AdvancedSearchDialog")
                true
            }
            else -> false
        }
    }

    /**
     * Callback function to handle search result updates.
     */
    override fun onSearchResultCallback(result: List<RealEstateWithDetails>) {
        realEstateAdapter.submitList(result)
    }

    /**
     * Callback function to handle edit real estate request.
     */
    override fun onEditRealEstateRequested() {
        val action = RealEstatesFragmentDirections.actionNavRealEstatesToNavEdit(idRealEstateSelected)
        findNavController().navigate(action)
    }
}

/**
 * Custom OnBackPressedCallback for handling back button presses when a SlidingPaneLayout is open.
 */
class RealEstateOnBackPressedCallback(
    private val slidingPaneLayout: SlidingPaneLayout, private val activity: FragmentActivity
) : OnBackPressedCallback(slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen), SlidingPaneLayout.PanelSlideListener {

    /**
     * Handles the back button press to close the SlidingPaneLayout.
     */
    override fun handleOnBackPressed() {
        slidingPaneLayout.closePane()
    }

    /**
     * Called when a detail view's position changes.
     */
    override fun onPanelSlide(panel: View, slideOffset: Float) {}

    /**
     * Called when a detail view becomes slid completely open.
     */
    override fun onPanelOpened(panel: View) {
        isEnabled = true
        activity.invalidateOptionsMenu()
    }

    /**
     * Called when a detail view becomes slid completely closed.
     */
    override fun onPanelClosed(panel: View) {
        isEnabled = false
        activity.invalidateOptionsMenu()
    }

    init {
        slidingPaneLayout.addPanelSlideListener(this)
    }
}
