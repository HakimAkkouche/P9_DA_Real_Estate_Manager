package com.haksoftware.p9_da_real_estate_manager.ui.detail

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentDetailBinding
import com.haksoftware.p9_da_real_estate_manager.ui.real_estates.RealEstateInteractionListener
import com.haksoftware.p9_da_real_estate_manager.ui.real_estates.RealEstatesFragmentDirections
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.RealEstatesViewModel
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory
import com.haksoftware.realestatemanager.utils.Utils.convertDollarToEuro
import com.haksoftware.realestatemanager.utils.Utils.formatNumberToUSStyle
import com.haksoftware.realestatemanager.utils.Utils.getEpochToFormattedDate
import com.haksoftware.realestatemanager.utils.Utils.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fragment for displaying detailed information about a real estate item.
 */
class DetailFragment : Fragment(), MenuProvider {

    private var _binding: FragmentDetailBinding? = null
    private val realEstatesViewModel: RealEstatesViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireActivity().application)
    }
    private lateinit var realEstateWithDetails: RealEstateWithDetails

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var isRealEstateLoaded: Boolean = false
    private var interactionListener: RealEstateInteractionListener? = null

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
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        realEstatesViewModel.currentRealEstate.observe(viewLifecycleOwner) {
            realEstateWithDetails = it
            bindRealEstate(realEstateWithDetails)
        }
        return binding.root
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored into the view.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isRealEstateLoaded && this.isInLayout) {
            bindRealEstate(realEstateWithDetails)
        }
    }

    /**
     * Sets up the RecyclerView for displaying photos of the real estate.
     */
    private fun setupRecyclerView() {
        val recyclerViewPhotos: RecyclerView = binding.photoGallery
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewPhotos.layoutManager = layoutManager
        val adapterPhotos = DetailsPhotoAdapter(requireContext(), realEstateWithDetails.photos)
        recyclerViewPhotos.adapter = adapterPhotos
    }

    /**
     * Initializes the ChipGroup with points of interest related to the real estate.
     */
    private fun initChips(){
        val chipGroup = binding.chipGroupPointOfInterest
        chipGroup.removeAllViews()  // Clear any existing chips
        for (pOI in realEstateWithDetails.pointsOfInterest){
            val chip = Chip(chipGroup.context)
            chip.text = pOI.namePoi
            chip.isCheckable = false
            chipGroup.addView(chip)
        }
    }

    /**
     * Called when the view previously created by onCreateView has been detached from the fragment.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Called by the MenuHost to allow the MenuProvider to inflate MenuItems into the menu.
     */
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        // No menu items to inflate in this example
    }

    /**
     * Called by the MenuHost when a MenuItem is selected from the menu.
     */
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_edit -> {
                val action = RealEstatesFragmentDirections.actionNavRealEstatesToNavEdit(realEstateWithDetails.realEstate.idRealEstate)
                findNavController().navigate(action)
                true
            }
            else -> false
        }
    }

    /**
     * Binds the real estate details to the UI components.
     */
    private fun bindRealEstate(realEstateWithDetails: RealEstateWithDetails) {
        binding.detailLayout.visibility = View.VISIBLE
        binding.textType.text = realEstateWithDetails.type.nameType
        binding.textDescription.text = realEstateWithDetails.realEstate.descriptionRealEstate
        binding.textPrice.text = formatNumberToUSStyle(realEstateWithDetails.realEstate.price)
        binding.textPriceEuro.text =
            convertDollarToEuro(realEstateWithDetails.realEstate.price.toFloat())
        binding.textSquareFeet.text = realEstateWithDetails.realEstate.squareFeet.toString()
        binding.textRoomCount.text = realEstateWithDetails.realEstate.roomCount.toString()
        binding.textBathroomCount.text = realEstateWithDetails.realEstate.bathroomCount.toString()
        binding.textAddress.text = realEstateWithDetails.realEstate.address
        val zipCity =
            realEstateWithDetails.realEstate.postalCode + " " + realEstateWithDetails.realEstate.city
        binding.textPostalCodeCity.text = zipCity
        binding.textState.text = realEstateWithDetails.realEstate.state
        val realtorName =
            realEstateWithDetails.realtor.title + " " + realEstateWithDetails.realtor.lastname.uppercase() + " " + realEstateWithDetails.realtor.firstname
        binding.textRealtor.text = realtorName
        binding.textRealtorNumber.text = realEstateWithDetails.realtor.phoneNumber
        binding.textRealtorEmail.text = realEstateWithDetails.realtor.email

        binding.textCreationDate.text =
            getEpochToFormattedDate(realEstateWithDetails.realEstate.creationDate)
        if (realEstateWithDetails.realEstate.soldDate != null) {
            binding.layoutSold.visibility = View.VISIBLE
            binding.textSoldDate.text =
                getEpochToFormattedDate(realEstateWithDetails.realEstate.soldDate!!)
        } else {

            binding.layoutSold.visibility = View.GONE
            binding.textSoldDate.text = ""
        }


        val address = realEstateWithDetails.realEstate.address + " " +realEstateWithDetails.realEstate.city
        CoroutineScope(Dispatchers.Main).launch {
            if (isInternetAvailable()) {
                val mapUrl = realEstatesViewModel.getMapUrl(address)
                if (mapUrl.isNotEmpty()) {
                    Glide.with(requireContext())
                        .load(mapUrl)
                        .into(binding.mapImageView)
                }
            }
        }

        setupRecyclerView()
        initChips()
    }

    /**
     * Called when the fragment is no longer attached to its activity.
     */
    override fun onDetach() {
        super.onDetach()
        interactionListener = null
    }
}
