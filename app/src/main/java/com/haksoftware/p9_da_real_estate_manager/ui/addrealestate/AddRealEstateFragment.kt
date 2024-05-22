package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import com.haksoftware.p9_da_real_estate_manager.databinding.FragmentAddRealEstateBinding
import com.haksoftware.p9_da_real_estate_manager.utils.ViewModelFactory
import kotlinx.coroutines.launch

class AddRealEstateFragment : Fragment(), AddPhotoListener{

    private lateinit var viewModel: AddRealEstateViewModel
    private var _binding: FragmentAddRealEstateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRealEstateBinding.inflate(inflater, container, false)
        binding.pickerRoomCount.minValue = 1
        binding.pickerRoomCount.maxValue = 10
        binding.pickerBathroomCount.minValue = 0
        binding.pickerBathroomCount.maxValue = 4
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory)[AddRealEstateViewModel::class.java]
        initRealtor()
        initTypes()
        initChips()
        setupRecyclerView()
        setupTextWatchers()
        binding.buttonSubmit.setOnClickListener {
            val price = binding.editPrice.text.toString().toInt()
            val surface = binding.editSurface.text.toString().toInt()
            val description = binding.editDescription.text.toString()
            val address = binding.editAddress.text.toString()
            val postalCode = binding.editPostalCode.text.toString()
            val city = binding.editCity.text.toString()
            val state = binding.editState.text.toString()
            val roomCount = binding.pickerRoomCount.value
            val bathroomCount = binding.pickerBathroomCount.value
            val idRealtor = (binding.spinnerRealtor.selectedItem as RealtorEntity).idRealtor// Get selected realtor ID from spinner
            val idType = (binding.spinnerType.selectedItem as TypeEntity).idType // Get selected type ID from spinner
            val pointsOfInterest = binding.chipGroupPointOfInterest.checkedChipIds
            viewModel.saveRealEstate()
        }

        lifecycleScope.launch {
            viewModel.isFormValid.collect { isValid ->
                binding.buttonSubmit.isEnabled = isValid
            }
        }
    }
    private fun initRealtor(){
        viewModel.realtorLiveData.observe(viewLifecycleOwner) { realtorList ->
            val adapter = RealtorAdapter(requireContext(), realtorList)
            binding.spinnerRealtor.adapter = adapter
        }
    }

    private fun initTypes(){
        viewModel.typesLiveData.observe(viewLifecycleOwner) { typeList ->
            val adapter = TypeAdapter(requireContext(), typeList)
            binding.spinnerType.adapter = adapter
        }
    }

    private fun initChips(){
        viewModel.pOILiveData.observe(viewLifecycleOwner) { poiList ->
            val chipGroup = binding.chipGroupPointOfInterest
            chipGroup.removeAllViews()  // Clear any existing chips
            for (pOI in poiList){
                val chip = Chip(chipGroup.context)
                chip.text = pOI.namePoi
                chip.isCheckable = true
                chipGroup.addView(chip)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        val recyclerViewPhotos: RecyclerView = binding.photoGallery
        val layoutManager = GridLayoutManager(requireContext(), 2) // GridLayoutManager with 2 columns
        recyclerViewPhotos.layoutManager = layoutManager
        val adapterPhotos = AddPhotoAdapter(requireContext(), mutableListOf(), this)
        recyclerViewPhotos.adapter = adapterPhotos

        viewModel.photoViewStateList.observe(viewLifecycleOwner) {
            adapterPhotos.notifyDataSetChanged()
        }
    }
    private fun setupTextWatchers() {
        binding.editPrice.addTextChangedListener(createTextWatcher { viewModel.updatePrice(it) })
        binding.editSurface.addTextChangedListener(createTextWatcher { viewModel.updateSurface(it) })
        binding.editDescription.addTextChangedListener(createTextWatcher { viewModel.updateDescription(it) })
        binding.editAddress.addTextChangedListener(createTextWatcher { viewModel.updateAddress(it) })
        binding.editPostalCode.addTextChangedListener(createTextWatcher { viewModel.updatePostalCode(it) })
        binding.editCity.addTextChangedListener(createTextWatcher { viewModel.updateCity(it) })
        binding.editState.addTextChangedListener(createTextWatcher { viewModel.updateState(it) })

        binding.pickerRoomCount.setOnValueChangedListener { _, _, newVal ->
            viewModel.updateRoomCount(newVal)
        }
        binding.pickerBathroomCount.setOnValueChangedListener { _, _, newVal ->
            viewModel.updateBathroomCount(newVal)
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

        binding.chipGroupPointOfInterest.setOnCheckedChangeListener { group, _ ->
            val checkedIds = group.checkedChipIds
            viewModel.updatePointsOfInterest(checkedIds)
        }
    }

    private fun createTextWatcher(update: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                update(s.toString())
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPhotoAdded(photoViewState: PhotoViewState) {
        viewModel.addPhotoViewState(photoViewState)
    }
}