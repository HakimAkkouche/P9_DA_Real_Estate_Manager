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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentDetailBinding
import com.haksoftware.p9_da_real_estate_manager.ui.edit.GetRealEstateCallBack
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory
import com.haksoftware.realestatemanager.utils.Utils.formatNumberToUSStyle
import com.haksoftware.realestatemanager.utils.Utils.getEpochToFormattedDate

class DetailFragment : Fragment(), MenuProvider, GetRealEstateCallBack {

    private var _binding: FragmentDetailBinding? = null
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var realEstateWithDetails: RealEstateWithDetails
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val navigationArgs: DetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        val viewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        detailViewModel = ViewModelProvider(this, viewModelFactory)[DetailViewModel::class.java]

        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        detailViewModel.getRealEstateWithDetails(navigationArgs.realEstate.realEstate.idRealEstate, this)


        return root
    }
    private fun setupRecyclerView() {
        val recyclerViewPhotos: RecyclerView = binding.photoGallery
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewPhotos.layoutManager = layoutManager
        val adapterPhotos = DetailsPhotoAdapter(requireContext(), realEstateWithDetails.photos)
        recyclerViewPhotos.adapter = adapterPhotos

    }
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
        menuInflater.inflate(R.menu.fragment_edit_menu, menu)
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
            R.id.action_edit -> {
                val action = DetailFragmentDirections.actionNavDetailToNavEdit(realEstateWithDetails)
                findNavController().navigate(action)
                true
            }
            else -> false
        }
    }

    override fun onGetRealEstateReponse(realEstateWithDetails: RealEstateWithDetails) {
        this.realEstateWithDetails = realEstateWithDetails
        binding.textType.text = realEstateWithDetails.type.nameType
        binding.textDescription.text = realEstateWithDetails.realEstate.descriptionRealEstate
        binding.textPrice.text = formatNumberToUSStyle(realEstateWithDetails.realEstate.price)
        binding.textSquareFeet.text = realEstateWithDetails.realEstate.squareFeet.toString()
        binding.textRoomCount.text = realEstateWithDetails.realEstate.roomCount.toString()
        binding.textBathroomCount.text = realEstateWithDetails.realEstate.bathroomCount.toString()
        binding.textAddress.text = realEstateWithDetails.realEstate.address
        val zipCity = realEstateWithDetails.realEstate.postalCode + realEstateWithDetails.realEstate.city
        binding.textPostalCodeCity.text = zipCity
        binding.textState.text = realEstateWithDetails.realEstate.state
        val realtorName = realEstateWithDetails.realtor.title + " " + realEstateWithDetails.realtor.lastname.uppercase() + " " +
                realEstateWithDetails.realtor.firstname
        binding.textRealtor.text = realtorName
        binding.textRealtorNumber.text = realEstateWithDetails.realtor.phoneNumber
        binding.textRealtorEmail.text = realEstateWithDetails.realtor.email

        binding.textCreationDate.text = getEpochToFormattedDate(realEstateWithDetails.realEstate.creationDate)
        if(realEstateWithDetails.realEstate.soldDate!= null) {
            binding.layoutSold.visibility = View.VISIBLE
            binding.textSoldDate.text = getEpochToFormattedDate(realEstateWithDetails.realEstate.soldDate!!)
        }

        val address = realEstateWithDetails.realEstate.address + " " +realEstateWithDetails.realEstate.city

        val  mapUrl = detailViewModel.getMapUrl( address)
        if (mapUrl.isNotEmpty()) {
            Glide.with(this)
                .load(detailViewModel.getMapUrl( address))
                .into(binding.mapImageView)
        }

        setupRecyclerView()
        initChips()
    }
}