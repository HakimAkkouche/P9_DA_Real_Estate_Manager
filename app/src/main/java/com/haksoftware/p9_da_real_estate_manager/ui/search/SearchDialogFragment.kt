package com.haksoftware.p9_da_real_estate_manager.ui.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.haksoftware.p9_da_real_estate_manager.R
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentSearchDialogBinding
import com.haksoftware.p9_da_real_estate_manager.ui.addrealestate.TypeAdapter
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.RealEstatesViewModel
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory
import com.haksoftware.realestatemanager.utils.Utils
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Calendar
/**
 * DialogFragment for searching real estate properties.
 * @property searchCallBack Callback for search results.
 */
class SearchDialogFragment(private val searchCallBack: SearchCallback) : DialogFragment() {

    private lateinit var binding: FragmentSearchDialogBinding
    private lateinit var viewModel: RealEstatesViewModel
    private val pOI: MutableList<Int> = mutableListOf()
    private var fromDate: Long? = null
    private var toDate: Long? = null
    private var roomCount: Int? = null
    private var bathroomCount: Int? = null
    private var minPhotoCount: Int? = null
    private var typeId: Int = 0

    /**
     * Inflates the layout for the fragment and initializes ViewModel and UI components.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchDialogBinding.inflate(inflater, container, false)
        val viewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory)[RealEstatesViewModel::class.java]

        initTypes()
        initChips()
        setupDatePicker()
        initNumberPickers()

        binding.buttonSearch.setOnClickListener {
            val minSurface = binding.editSurfaceMin.text.toString().toIntOrNull()
            val maxSurface = binding.editSurfaceMax.text.toString().toIntOrNull()
            val minPrice = binding.editPriceMin.text.toString().toIntOrNull()
            val maxPrice = binding.editPriceMax.text.toString().toIntOrNull()
            val isSold = binding.checkedIsSold.isChecked

            viewModel.searchRealEstates(searchCallBack, typeId, fromDate, toDate, minSurface, maxSurface,
                minPrice, maxPrice, roomCount, bathroomCount, minPhotoCount, pOI, isSold)
            dismiss()
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    /**
     * Initializes the type spinner with data from the ViewModel.
     */
    private fun initTypes() {
        viewModel.typesLiveData.observe(viewLifecycleOwner) { typeList ->
            val modifiedTypeList = mutableListOf(TypeEntity(0, "")) // Assuming TypeEntity has a constructor that accepts idType and a name
            modifiedTypeList.addAll(typeList)
            val adapter = TypeAdapter(requireContext(), modifiedTypeList)
            binding.spinnerType.adapter = adapter
        }
        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                typeId = (parent?.getItemAtPosition(position) as TypeEntity).idType
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * Initializes the ChipGroup with data from the ViewModel.
     */
    private fun initChips() {
        viewModel.pOILiveData.observe(viewLifecycleOwner) { poiList ->
            val chipGroup = binding.chipGroupPointOfInterest
            chipGroup.removeAllViews()  // Clear any existing chips
            for (pOI in poiList) {
                val chip = Chip(chipGroup.context)
                chip.text = pOI.namePoi
                chip.isCheckable = true
                chip.tag = pOI.idPoi
                chipGroup.addView(chip)
            }
        }
        binding.chipGroupPointOfInterest.setOnCheckedStateChangeListener { group, _ ->
            val checkedIds = group.checkedChipIds.map { id ->
                group.findViewById<Chip>(id).tag as Int
            }
            pOI.clear()
            pOI.addAll(checkedIds)
        }
    }

    /**
     * Initializes the number pickers for room count, bathroom count, and photo count.
     */
    private fun initNumberPickers() {

        binding.buttonIncrementRoomCount.setOnClickListener {
            roomCount = if (roomCount == null) { 1 } else { roomCount!! + 1 }
            binding.editTextRoomCount.text = roomCount.toString()
        }

        binding.buttonDecrementRoomCount.setOnClickListener {
            roomCount = if (roomCount == null) { 0 } else { roomCount!! }
            if (roomCount!! > 0) {
                roomCount = roomCount!! - 1
            }
            binding.editTextRoomCount.text = roomCount.toString()
        }

        binding.buttonIncrementBathroomCount.setOnClickListener {
            bathroomCount = if (bathroomCount == null) { 1 } else { bathroomCount!! + 1 }
            binding.editTextBathroomCount.text = bathroomCount.toString()
        }

        binding.buttonDecrementBathroomCount.setOnClickListener {
            bathroomCount = if (bathroomCount == null) { 0 } else { bathroomCount!! }
            if (bathroomCount!! > 0) {
                bathroomCount = bathroomCount!! - 1
            }
            binding.editTextBathroomCount.text = bathroomCount.toString()
        }

        binding.buttonIncrementPhotosCount.setOnClickListener {
            minPhotoCount = if (minPhotoCount == null) { 1 } else { minPhotoCount!! + 1 }
            binding.editTextPhotosCount.text = minPhotoCount.toString()
        }

        binding.buttonDecrementPhotosCount.setOnClickListener {
            minPhotoCount = if (minPhotoCount == null) { 0 } else { minPhotoCount!! }
            if (minPhotoCount!! > 0) {
                minPhotoCount = minPhotoCount!! - 1
            }
            binding.editTextPhotosCount.text = minPhotoCount.toString()
        }
    }

    /**
     * Sets up the date picker for the from and to date fields.
     */
    private fun setupDatePicker() {
        binding.editTextFromDate.setOnClickListener {
            showDatePickerDialog(it as EditText)
        }
        binding.editTextEndDate.setOnClickListener {
            showDatePickerDialog(it as EditText)
        }
    }

    /**
     * Shows a date picker dialog for the specified EditText.
     * @param editText The EditText to set the date on.
     */
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                editText.setText(
                    Utils.getEpochToFormattedDate(
                        LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDay, 0, 0, 0)
                            .toEpochSecond(ZoneOffset.UTC)
                    )
                )

                if (editText.id == R.id.editTextFromDate) {
                    fromDate = LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDay, 0, 0, 0)
                        .toEpochSecond(ZoneOffset.UTC)
                } else {
                    toDate = LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDay, 0, 0, 0)
                        .toEpochSecond(ZoneOffset.UTC)
                }
            }, year, month, day)
        datePickerDialog.show()
    }
}
