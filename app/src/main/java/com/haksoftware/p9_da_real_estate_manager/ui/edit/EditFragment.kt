package com.haksoftware.p9_da_real_estate_manager.ui.edit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.haksoftware.p9_da_real_estate_manager.BuildConfig
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentAddRealEstateBinding
import com.haksoftware.p9_da_real_estate_manager.ui.addrealestate.AddPhotoAdapter
import com.haksoftware.p9_da_real_estate_manager.ui.addrealestate.AddPhotoDialog
import com.haksoftware.p9_da_real_estate_manager.ui.addrealestate.AddPhotoDialogListener
import com.haksoftware.p9_da_real_estate_manager.ui.addrealestate.RealtorAdapter
import com.haksoftware.p9_da_real_estate_manager.ui.addrealestate.RemovePhotoListener
import com.haksoftware.p9_da_real_estate_manager.ui.addrealestate.TypeAdapter
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.RealEstatesViewModel
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory
import com.haksoftware.realestatemanager.utils.Utils
import com.haksoftware.realestatemanager.utils.Utils.getEpochToFormattedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Locale

/**
 * Fragment for editing real estate details.
 */
class EditFragment : Fragment(), GetRealEstateCallBack, AddPhotoDialogListener, RemovePhotoListener {

    private lateinit var viewModel: RealEstatesViewModel
    private lateinit var adapterPhotos: AddPhotoAdapter
    private var _binding: FragmentAddRealEstateBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var placesClient: PlacesClient
    private lateinit var addressAutoCompleteAdapter: ArrayAdapter<String>
    private var initSpinnerCount = 0

    private lateinit var realEstateWithDetails: RealEstateWithDetails
    private val navigationArgs: EditFragmentArgs by navArgs()

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory)[RealEstatesViewModel::class.java]
        _binding = FragmentAddRealEstateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.getRealEstateWithDetails(
            navigationArgs.realEstateId,
            this
        )


        return root
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
        adapterPhotos = AddPhotoAdapter(realEstateWithDetails.photos.toMutableList(), this)
        recyclerViewPhotos.adapter = adapterPhotos
        // Observer for photo list updates
        viewModel.photosEntityListLiveData.observe(viewLifecycleOwner) {
            adapterPhotos.notifyDataSetChanged()
        }
        adapterPhotos.notifyDataSetChanged()
    }

    /**
     * Initializes the Realtor spinner with data.
     */
    private fun initRealtor() {
        viewModel.realtorLiveData.observe(viewLifecycleOwner) { realtorList ->
            val adapter = RealtorAdapter(requireContext(), realtorList)
            binding.spinnerRealtor.adapter = adapter

            val selectedRealtorIndex = realtorList.indexOfFirst { it.idRealtor == realEstateWithDetails.realEstate.idRealtor }
            if (selectedRealtorIndex != -1) {
                binding.spinnerRealtor.setSelection(selectedRealtorIndex)
            }
        }
    }

    /**
     * Initializes the Type spinner with data.
     */
    private fun initTypes() {
        viewModel.typesLiveData.observe(viewLifecycleOwner) { typeList ->
            val adapter = TypeAdapter(requireContext(), typeList)
            binding.spinnerType.adapter = adapter

            val selectedTypeIndex = typeList.indexOfFirst { it.idType == realEstateWithDetails.realEstate.idType }
            if (selectedTypeIndex != -1) {

                binding.spinnerType.setSelection(selectedTypeIndex)
            }
        }
    }

    /**
     * Sets up the AutoCompleteTextView for address input.
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

                val city = if(address.subLocality != null) {
                    address.subLocality
                } else {
                    address.locality
                }

                realEstateWithDetails.realEstate.address = address.subThoroughfare+ " " + address.thoroughfare
                realEstateWithDetails.realEstate.postalCode = address.postalCode
                realEstateWithDetails.realEstate.city = city
                realEstateWithDetails.realEstate.state = address.countryName

                viewModel.updateRealEstateWithDetails(realEstateWithDetails)

                enableSubmitButton()
            }
        }
    }

    /**
     * Initializes the ChipGroup with points of interest.
     */
    private fun initChips(){
        viewModel.pOILiveData.observe(viewLifecycleOwner) { poiList ->
            val chipGroup = binding.chipGroupPointOfInterest
            chipGroup.removeAllViews()  // Clear any existing chips
            for (pOI in poiList){
                val chip = Chip(chipGroup.context)
                chip.text = pOI.namePoi
                chip.isCheckable = true
                chip.tag = pOI.idPoi
                for (pOIChecked in realEstateWithDetails.pointsOfInterest) {
                    if(pOI.namePoi == pOIChecked.namePoi) {
                        chip.isChecked = true
                    }
                }
                chipGroup.addView(chip)
            }
        }
        binding.chipGroupPointOfInterest.setOnCheckedStateChangeListener {  group, _ ->
            val checkedIds = group.checkedChipIds.map { id ->
                group.findViewById<Chip>(id).tag as Int
            }
            viewModel.updatePointsOfInterest(checkedIds)
            enableSubmitButton()
        }
    }
    /**
     * Sets up the DatePicker for the sold date field.
     */
    private fun setupDatePicker() {
        binding.editSoldDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    /**
     * Shows the DatePickerDialog for selecting a date.
     */
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.editSoldDate.setText(selectedDate)
                enableSubmitButton()

                realEstateWithDetails.realEstate.soldDate = LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDay, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
                viewModel.updateRealEstateWithDetails(realEstateWithDetails)
            }, year, month, day)
        datePickerDialog.show()
    }
    /**
     * Sets up observers for ViewModel data changes.
     */
    private fun setupObservers() {
        viewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Real estate updated successfully", Snackbar.LENGTH_SHORT).show()
                //findNavController().navigate(AddRealEstateFragmentDirections.actionNavAddToNavRealEstates())
            } else {
                Snackbar.make(binding.root, "Failed to update real estate", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Callback method for receiving the real estate details.
     */
    override fun onGetRealEstateResponse(realEstateWithDetails: RealEstateWithDetails) {
        this.realEstateWithDetails = realEstateWithDetails

        viewModel.setOriginalRealEstateId(realEstateWithDetails.realEstate.idRealEstate)


        // Initialize the Places API with the API key
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.GOOGLE_MAPS_API_KEY)
        }
        placesClient = Places.createClient(requireContext())

        binding.editDescription.setText(realEstateWithDetails.realEstate.descriptionRealEstate)
        binding.editPrice.setText( realEstateWithDetails.realEstate.price.toString())
        binding.textviewPriceInEuro.text = Utils.convertDollarToEuro(realEstateWithDetails.realEstate.price.toFloat())
        binding.editSurface.setText( realEstateWithDetails.realEstate.squareFeet.toString())
        binding.textviewSurfaceInM2.text = Utils.convertFtSquareToMSquare(realEstateWithDetails.realEstate.squareFeet.toFloat())
        binding.numberPickerRoomCount.value = realEstateWithDetails.realEstate.roomCount
        binding.numberPickerBathroomCount.value = realEstateWithDetails.realEstate.bathroomCount

        val address = realEstateWithDetails.realEstate.address + "," + realEstateWithDetails.realEstate.city
        binding.autocompleteAddress.setText(address, false)

        binding.editAddress.setText(realEstateWithDetails.realEstate.address)
        binding.editPostalCode.setText(realEstateWithDetails.realEstate.postalCode)
        binding.editCity.setText(realEstateWithDetails.realEstate.city)
        binding.editCountry.setText(realEstateWithDetails.realEstate.state)

        binding.editSoldDate.visibility = View.VISIBLE
        binding.editSoldDate.setText(if (realEstateWithDetails.realEstate.soldDate != null)
        {
            getEpochToFormattedDate(realEstateWithDetails.realEstate.soldDate!!)
        }   else(""))

        setupRecyclerView()
        initRealtor()
        initTypes()
        initChips()
        setupDatePicker()
        setupObservers()

        addTextWatchers()
        addSpinnerListeners()

        binding.buttonAddImage.setOnClickListener {
            val dialog = AddPhotoDialog()
            dialog.setListener(this)
            dialog.show(parentFragmentManager, "AddPhotoDialog")
        }
        CoroutineScope(Dispatchers.Main).launch {
            if(Utils.isInternetAvailable()) {
                setupAutoCompleteTextView()
            }
        }


        binding.buttonSubmit.setOnClickListener {
            viewModel.updateRealEstate()
            viewModel.updateISNextTo()
            viewModel.addPhotos(requireContext())
            viewModel.removePhotos()
            viewModel.updateCurrentRealEstate(realEstateWithDetails)
            findNavController().navigate(R.id.nav_real_estates)
        }
    }
    /**
     * Called when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Adds text watchers to EditText fields to update the ViewModel.
     */
    private fun addTextWatchers() {
        binding.editPrice.addTextChangedListener(createTextWatcher { text ->
            if(text.isNotEmpty()) {
                realEstateWithDetails.realEstate.price = text.toInt()
                viewModel.updateRealEstateWithDetails(realEstateWithDetails)

                binding.textviewPriceInEuro.text = Utils.convertDollarToEuro(text.toFloat())
            }
        })

        binding.editSurface.addTextChangedListener(createTextWatcher { text ->
            if(text.isNotEmpty()) {
                realEstateWithDetails.realEstate.squareFeet = text.toInt()
                viewModel.updateRealEstateWithDetails(realEstateWithDetails)
                binding.textviewSurfaceInM2.text = Utils.convertFtSquareToMSquare(text.toFloat())
            }
        })

        binding.editDescription.addTextChangedListener(createTextWatcher { text ->
            realEstateWithDetails.realEstate.descriptionRealEstate = text
            viewModel.updateRealEstateWithDetails(realEstateWithDetails)
        })

        binding.editAddress.addTextChangedListener(createTextWatcher { text ->
            realEstateWithDetails.realEstate.address = text
            viewModel.updateRealEstateWithDetails(realEstateWithDetails)
        })

        binding.editPostalCode.addTextChangedListener(createTextWatcher { text ->
            realEstateWithDetails.realEstate.postalCode = text
            viewModel.updateRealEstateWithDetails(realEstateWithDetails)
        })

        binding.editCity.addTextChangedListener(createTextWatcher { text ->
            realEstateWithDetails.realEstate.city = text
            viewModel.updateRealEstateWithDetails(realEstateWithDetails)
        })

        binding.editCountry.addTextChangedListener(createTextWatcher { text ->
            realEstateWithDetails.realEstate.state = text
            viewModel.updateRealEstateWithDetails(realEstateWithDetails)
        })
        binding.numberPickerRoomCount.setValueChangedListener { roomCount, _ ->
            realEstateWithDetails.realEstate.roomCount = roomCount
            viewModel.updateRealEstateWithDetails(realEstateWithDetails)
            enableSubmitButton()
        }

        binding.numberPickerBathroomCount.setValueChangedListener { bathroomCount, _ ->
            realEstateWithDetails.realEstate.bathroomCount = bathroomCount
            viewModel.updateRealEstateWithDetails(realEstateWithDetails)
            enableSubmitButton()
        }
    }
    /**
     * Creates a TextWatcher to handle text changes and update the ViewModel.
     */
    private fun createTextWatcher(update: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                update(s.toString())
                enableSubmitButton()
            }
        }
    }

    /**
     * Adds listeners to the spinners to update the ViewModel.
     */
    private fun addSpinnerListeners() {
        binding.spinnerRealtor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(initSpinnerCount == 2) {
                    val selectedRealtor = parent.getItemAtPosition(position) as RealtorEntity

                    realEstateWithDetails.realEstate.idRealtor = selectedRealtor.idRealtor
                    viewModel.updateRealEstateWithDetails(realEstateWithDetails)
                    enableSubmitButton()
                } else initSpinnerCount++
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(initSpinnerCount == 2)
                {
                    val selectedType = parent.getItemAtPosition(position) as TypeEntity
                    realEstateWithDetails.realEstate.idType = selectedType.idType
                    viewModel.updateRealEstateWithDetails(realEstateWithDetails)

                    enableSubmitButton()
                }else {
                    initSpinnerCount++
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
    /**
     * Enables the submit button.
     */
    private fun enableSubmitButton() {
        binding.buttonSubmit.isEnabled = true
    }

    /**
     * Callback method for adding a photo.
     */
    override fun onPhotoDialogAdded(photoEntity: PhotoEntity) {
        viewModel.photoToAddEntity(photoEntity)
        adapterPhotos.addPhoto(photoEntity)
        enableSubmitButton()
    }

    /**
     * Callback method for removing a photo.
     */
    override fun onPhotoRemoved(photoEntity: PhotoEntity) {
        viewModel.photoToRemove(photoEntity)
        adapterPhotos.removePhoto(photoEntity)
        enableSubmitButton()
    }
}