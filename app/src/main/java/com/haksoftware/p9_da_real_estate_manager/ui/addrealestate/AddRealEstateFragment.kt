package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.haksoftware.p9_da_real_estate_manager.BuildConfig
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentAddRealEstateBinding
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.AddRealEstateViewModel
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory
import com.haksoftware.realestatemanager.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Fragment for adding a real estate listing. Allows the user to input various details,
 * select a realtor, type, points of interest, and add photos to the listing.
 */
class AddRealEstateFragment : Fragment(), AddPhotoDialogListener, RemovePhotoListener {

    private lateinit var viewModel: AddRealEstateViewModel
    private var _binding: FragmentAddRealEstateBinding? = null
    private val binding get() = _binding!!
    private lateinit var placesClient: PlacesClient
    private lateinit var addressAutoCompleteAdapter: ArrayAdapter<String>
    private lateinit var adapterPhotos: AddPhotoAdapter

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory)[AddRealEstateViewModel::class.java]
        _binding = FragmentAddRealEstateBinding.inflate(inflater, container, false)
        binding.numberPickerRoomCount.value = 0
        binding.numberPickerBathroomCount.value = 0

        // Initialize the Places API with the API key
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.GOOGLE_MAPS_API_KEY)
        }
        placesClient = Places.createClient(requireContext())

        binding.buttonAddImage.setOnClickListener {
            val dialog = AddPhotoDialog()
            dialog.setListener(this)
            dialog.show(parentFragmentManager, "AddPhotoDialog")
        }

        CoroutineScope(Dispatchers.Main).launch {
            if (Utils.isInternetAvailable()) {
                setupAutoCompleteTextView()
            }
        }
        return binding.root
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored into the view.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRealtor()
        initTypes()
        initChips()
        setupRecyclerView()
        setupTextWatchers()
        setupObservers()

        binding.buttonSubmit.setOnClickListener {
            viewModel.saveRealEstate(requireContext())
        }

        lifecycleScope.launch {
            viewModel.isFormValid.collect { isValid ->
                binding.buttonSubmit.isEnabled = isValid
            }
        }
    }

    /**
     * Initializes the Realtor spinner with data from the ViewModel.
     */
    private fun initRealtor() {
        viewModel.realtorLiveData.observe(viewLifecycleOwner) { realtorList ->
            val adapter = RealtorAdapter(requireContext(), realtorList)
            binding.spinnerRealtor.adapter = adapter
        }
    }

    /**
     * Initializes the Type spinner with data from the ViewModel.
     */
    private fun initTypes() {
        viewModel.typesLiveData.observe(viewLifecycleOwner) { typeList ->
            val adapter = TypeAdapter(requireContext(), typeList)
            binding.spinnerType.adapter = adapter
        }
    }

    /**
     * Initializes the ChipGroup with points of interest data from the ViewModel.
     */
    private fun initChips() {
        viewModel.pOILiveData.observe(viewLifecycleOwner) { poiList ->
            val chipGroup = binding.chipGroupPointOfInterest
            chipGroup.removeAllViews() // Clear any existing chips
            for (pOI in poiList) {
                val chip = Chip(chipGroup.context)
                chip.text = pOI.namePoi
                chip.isCheckable = true
                chipGroup.addView(chip)
            }
        }
    }

    /**
     * Sets up observers for LiveData from the ViewModel.
     */
    private fun setupObservers() {
        viewModel.insertSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Real estate saved successfully", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(AddRealEstateFragmentDirections.actionNavAddToNavRealEstates())
            } else {
                Snackbar.make(binding.root, "Failed to save real estate", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Sets up the RecyclerView for displaying photos.
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        val recyclerViewPhotos: RecyclerView = binding.photoGallery
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewPhotos.layoutManager = layoutManager
        adapterPhotos = AddPhotoAdapter(mutableListOf(), this)
        recyclerViewPhotos.adapter = adapterPhotos

        viewModel.photoEntityListLiveData.observe(viewLifecycleOwner) {
            adapterPhotos.notifyDataSetChanged()
        }
    }

    /**
     * Sets up the AutoCompleteTextView for address input with Google Places API.
     */
    @Suppress("DEPRECATION")
    private fun setupAutoCompleteTextView() {
        addressAutoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)
        binding.inputLayoutAddressAutocomplete.visibility = View.VISIBLE
        binding.layoutAddressTextviews.visibility = View.GONE
        binding.autocompleteAddress.setAdapter(addressAutoCompleteAdapter)

        binding.autocompleteAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(s.toString())
                        .build()

                    placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                        val predictions = response.autocompletePredictions
                        val suggestionList = predictions.map { it.getFullText(null).toString() }
                        addressAutoCompleteAdapter.clear()
                        addressAutoCompleteAdapter.addAll(suggestionList)
                        addressAutoCompleteAdapter.notifyDataSetChanged()
                    }.addOnFailureListener { exception ->
                        if (exception is ApiException) {
                            Toast.makeText(context, "Error: ${exception.statusCode}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })

        binding.autocompleteAddress.setOnItemClickListener { _, _, position, _ ->
            val selectedAddress = addressAutoCompleteAdapter.getItem(position)
            binding.autocompleteAddress.setText(selectedAddress)

            // Optionally, use Geocoder to get more detailed information if needed
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocationName(selectedAddress!!, 1)

            if (addresses!!.isNotEmpty()) {
                val address = addresses[0]
                viewModel.updateAddress(address.subThoroughfare + " " + address.thoroughfare)
                val city = if (address.subLocality != null) {
                    address.subLocality
                } else {
                    address.locality
                }
                viewModel.updateCity(city)
                viewModel.updateZipCode(address.postalCode)
                viewModel.updateCountry(address.countryName)
            }
        }
    }

    /**
     * Sets up TextWatchers for various EditText fields.
     */
    private fun setupTextWatchers() {
        binding.editPrice.addTextChangedListener(createTextWatcher {
            viewModel.updatePrice(it)
            if (it.isNotEmpty()) {
                updatePriceInEuro(it)
            }
        })
        binding.editSurface.addTextChangedListener(createTextWatcher {
            viewModel.updateSurface(it)
            if (it.length > 2) {
                updateSurfaceInM2(it)
            }
        })
        binding.editDescription.addTextChangedListener(createTextWatcher { viewModel.updateDescription(it) })

        binding.editAddress.addTextChangedListener(createTextWatcher { viewModel.updateAddress(it) })
        binding.editPostalCode.addTextChangedListener(createTextWatcher { viewModel.updateZipCode(it) })
        binding.editCity.addTextChangedListener(createTextWatcher { viewModel.updateCity(it) })
        binding.editCountry.addTextChangedListener(createTextWatcher { viewModel.updateCountry(it) })

        binding.numberPickerRoomCount.setValueChangedListener { roomCount, _ ->
            viewModel.updateRoomCount(roomCount)
        }
        binding.numberPickerBathroomCount.setValueChangedListener { bathroomCount, _ ->
            viewModel.updateBathroomCount(bathroomCount)
        }

        binding.spinnerRealtor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRealtor = parent?.getItemAtPosition(position) as RealtorEntity
                viewModel.updateIdRealtor(selectedRealtor.idRealtor)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedType = parent?.getItemAtPosition(position) as TypeEntity
                viewModel.updateIdType(selectedType.idType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.chipGroupPointOfInterest.setOnCheckedStateChangeListener { group, _ ->
            val checkedIds = group.checkedChipIds
            viewModel.updatePointsOfInterest(checkedIds)
        }
    }

    /**
     * Updates the price in Euro based on the given price in dollars.
     */
    private fun updatePriceInEuro(price: String) {
        val priceInEuro = try {
            Utils.convertDollarToEuro(price.toFloat())
        } catch (e: NumberFormatException) {
            0.0
        }
        binding.textviewPriceInEuro.text = priceInEuro.toString()
    }

    /**
     * Updates the surface in square meters based on the given surface in square feet.
     */
    private fun updateSurfaceInM2(surface: String) {
        val surfaceInM2 = try {
            Utils.convertFtSquareToMSquare(surface.toFloat())
        } catch (e: NumberFormatException) {
            0.0
        }
        binding.textviewSurfaceInM2.text = String.format(Locale.getDefault(), "%d m²", surfaceInM2)
    }

    /**
     * Creates a TextWatcher with the specified update function.
     */
    private fun createTextWatcher(update: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                update(s.toString())
            }
        }
    }

    /**
     * Called when the view hierarchy associated with the fragment is being removed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Called when a photo is added via the AddPhotoDialog.
     */
    override fun onPhotoDialogAdded(photoEntity: PhotoEntity) {
        viewModel.addPhotoEntity(photoEntity)
        adapterPhotos.addPhoto(photoEntity)
    }

    /**
     * Called when a photo is removed.
     */
    override fun onPhotoRemoved(photoEntity: PhotoEntity) {
        viewModel.removePhotoEntity(photoEntity)
        adapterPhotos.removePhoto(photoEntity)
    }
}
