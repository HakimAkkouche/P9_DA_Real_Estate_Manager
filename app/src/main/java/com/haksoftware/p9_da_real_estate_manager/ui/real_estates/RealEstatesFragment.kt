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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentRealEstatesBinding
import com.haksoftware.p9_da_real_estate_manager.ui.search.SearchCallback
import com.haksoftware.p9_da_real_estate_manager.ui.search.SearchDialogFragment
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory


class RealEstatesFragment : Fragment(), MenuProvider, /*OnItemClickListener, */SearchCallback, RealEstateInteractionListener  {

    private var _binding: FragmentRealEstatesBinding? = null

    private val binding get() = _binding!!
    private lateinit var realEstateAdapter: RealEstateAdapter
    private lateinit var realEstatesViewModel: RealEstatesViewModel
    private lateinit var recyclerViewRealEstates: RecyclerView
    private var countViewCreated = 0
    private var idRealEstateSelected = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("RealEstatesFragment", "onCreateView called")

        val fragmentManager = parentFragmentManager
        val fragments = fragmentManager.fragments
        Log.d("RealEstatesFragment", "Current fragments in fragmentManager: ${fragments.size}")
        for (fragment in fragments) {
            Log.d("RealEstatesFragment", "Fragment: ${fragment.javaClass.simpleName}")
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        val viewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        realEstatesViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[RealEstatesViewModel::class.java]
        realEstatesViewModel.realEstates.observe(viewLifecycleOwner) {
            if(!it.isEmpty()) {
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slidingPaneLayout = binding.slidingPaneLayout
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            RealEstateOnBackPressedCallback(slidingPaneLayout, requireActivity())
        )
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        realEstateAdapter = RealEstateAdapter {
            realEstatesViewModel.updateCurrentRealEstate(it)
            binding.slidingPaneLayout.openPane()
        }
        recyclerViewRealEstates.apply {
            adapter = realEstateAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    private fun observeViewModel() {
        realEstatesViewModel.realEstates.observe(viewLifecycleOwner) { realEstates ->
            realEstateAdapter.submitList(realEstates)
        }
        realEstatesViewModel.searchResults.observe(viewLifecycleOwner) {searchResults ->
            realEstateAdapter.submitList(searchResults)

            searchResults?.let {
                Log.d("RealEstatesFragment", "searchResults updated: ${it.size} items")
            } ?: run {
                Log.d("RealEstatesFragment", "searchResults is null")
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        if (binding.slidingPaneLayout.isOpen && idRealEstateSelected != 0) {
            menuInflater.inflate(R.menu.fragment_edit_menu, menu)
        }
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
            R.id.action_search -> {
                val dialog = SearchDialogFragment(this)
                dialog.show(this.parentFragmentManager, "AdvancedSearchDialog")
                true
            }
            else -> false
        }
    }
    override fun onSearchResultCallback(result: List<RealEstateWithDetails>) {
        realEstateAdapter.submitList(result)
    }
    override fun onEditRealEstateRequested() {
        val action = RealEstatesFragmentDirections.actionNavRealEstatesToNavEdit(idRealEstateSelected)
        findNavController().navigate(action)
    }
}
class RealEstateOnBackPressedCallback(
    private val slidingPaneLayout: SlidingPaneLayout, private val activity: FragmentActivity
): OnBackPressedCallback(slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen), SlidingPaneLayout.PanelSlideListener {
    override fun handleOnBackPressed() {
        slidingPaneLayout.closePane()
    }

    /**
     * Called when a detail view's position changes.
     *
     * @param panel       The child view that was moved
     * @param slideOffset The new offset of this sliding pane within its range, from 0-1
     */
    override fun onPanelSlide(panel: View, slideOffset: Float) {
    }

    /**
     * Called when a detail view becomes slid completely open.
     *
     * @param panel The detail view that was slid to an open position
     */
    override fun onPanelOpened(panel: View) {
        isEnabled = true
        activity.invalidateOptionsMenu()
    }

    /**
     * Called when a detail view becomes slid completely closed.
     *
     * @param panel The detail view that was slid to a closed position
     */
    override fun onPanelClosed(panel: View) {
        isEnabled = false
        activity.invalidateOptionsMenu()
    }
    init {
        slidingPaneLayout.addPanelSlideListener(this)
    }
}