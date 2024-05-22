package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haksoftware.p9_da_real_estate_manager.data.entity.IsNextToEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PointOfInterestEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository
import com.haksoftware.realestatemanager.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class AddRealEstateViewModel(application: Application, private val realEstateRepository: RealEstateRepository) : AndroidViewModel(application) {

    private val _pOiLiveData: LiveData<List<PointOfInterestEntity>> = realEstateRepository.getAllPointOfInterest()
    private val _realtorsLiveData: LiveData<List<RealtorEntity>> = realEstateRepository.getAllRealtor()
    private val _typesLiveData: LiveData<List<TypeEntity>> = realEstateRepository.getAllTypes()

    private val _price = MutableStateFlow("")
    private val _surface = MutableStateFlow("")
    private val _description = MutableStateFlow("")
    private val _address = MutableStateFlow("")
    private val _postalCode = MutableStateFlow("")
    private val _city = MutableStateFlow("")
    private val _state = MutableStateFlow("")
    private val _soldDate = MutableStateFlow("")
    private val _roomCount = MutableStateFlow(0)
    private val _bathroomCount = MutableStateFlow(0)
    private val _idRealtor = MutableStateFlow(0)
    private val _idType = MutableStateFlow(0)
    private val _pointsOfInterest = MutableStateFlow(emptyList<Int>())

    val pOILiveData: LiveData<List<PointOfInterestEntity>> get() = _pOiLiveData
    val realtorLiveData: LiveData<List<RealtorEntity>> get() = _realtorsLiveData
    val typesLiveData: LiveData<List<TypeEntity>> get() = _typesLiveData

    private val _photoViewStateList = MutableLiveData<MutableList<PhotoViewState>>()
    val photoViewStateList: LiveData<MutableList<PhotoViewState>> get() = _photoViewStateList

    // Public StateFlows to be observed from the Fragment
    val price: StateFlow<String> = _price
    val surface: StateFlow<String> = _surface
    val description: StateFlow<String> = _description
    val address: StateFlow<String> = _address
    val postalCode: StateFlow<String> = _postalCode
    val city: StateFlow<String> = _city
    val state: StateFlow<String> = _state
    val soldDate: StateFlow<String> = _soldDate
    val roomCount: StateFlow<Int> = _roomCount
    val bathroomCount: StateFlow<Int> = _bathroomCount
    val idRealtor: StateFlow<Int> = _idRealtor
    val idType: StateFlow<Int> = _idType
    val pointsOfInterest: StateFlow<List<Int>> = _pointsOfInterest
    fun addPhotoViewState(photoViewState: PhotoViewState) {
        _photoViewStateList.value?.let { list ->
            list.add(photoViewState)
            _photoViewStateList.value = list
        }
    }

    // Combined StateFlow to check if the form is valid
    val isFormValid: StateFlow<Boolean> = combine(
        _price, _surface, _description, _address, _postalCode, _city, _state
    ) { values: Array<String> ->
        values[0].isNotBlank() && values[1].isNotBlank() && values[2].isNotBlank() &&
                values[3].isNotBlank() && values[4].isNotBlank() && values[5].isNotBlank() && values[6].isNotBlank()
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    fun updatePrice(price: String) {
        _price.value = price
    }

    fun updateSurface(surface: String) {
        _surface.value = surface
    }

    fun updateDescription(description: String) {
        _description.value = description
    }

    fun updateAddress(address: String) {
        _address.value = address
    }

    fun updatePostalCode(postalCode: String) {
        _postalCode.value = postalCode
    }

    fun updateCity(city: String) {
        _city.value = city
    }

    fun updateState(state: String) {
        _state.value = state
    }

    fun updateSoldDate(soldDate: String) {
        _soldDate.value = soldDate
    }

    fun updateRoomCount(roomCount: Int) {
        _roomCount.value = roomCount
    }

    fun updateBathroomCount(bathroomCount: Int) {
        _bathroomCount.value = bathroomCount
    }

    fun updateIdRealtor(idRealtor: Int) {
        _idRealtor.value = idRealtor
    }

    fun updateIdType(idType: Int) {
        _idType.value = idType
    }

    fun updatePointsOfInterest(pointsOfInterest: List<Int>) {
        _pointsOfInterest.value = pointsOfInterest
    }
    fun saveRealEstate() {
        viewModelScope.launch {
            val price = _price.value.toInt()
            val surface = _surface.value.toInt()
            val description = _description.value
            val address = _address.value
            val postalCode = _postalCode.value
            val city = _city.value
            val state = _state.value
            val roomCount = _roomCount.value
            val bathroomCount = _bathroomCount.value
            val idRealtor = _idRealtor.value
            val idType = _idType.value
            val pointsOfInterest = _pointsOfInterest.value
            val photos = photoViewStateList.value.orEmpty()

            val realEstateEntity = RealEstateEntity(
                idRealEstate = 0, // Auto-generated by Room
                price = price,
                squareFeet = surface,
                roomCount = roomCount,
                bathroomCount = bathroomCount,
                descriptionRealEstate = description,
                address = address,
                postalCode = postalCode,
                city = city,
                state = state,
                creationDate = Utils.getTodayDate(),
                soldDate = "",
                idRealtor = idRealtor,
                idType = idType
            )

            // Insert real estate
            val realEstateId = realEstateRepository.insertRealEstate(realEstateEntity).toInt()

            // Insert photos
            val photoEntities = photos.map { photo ->
                val filename = UUID.randomUUID().toString() + ".jpg"
                val filePath = Utils.saveImageToInternalStorage(getApplication(), photo.imageUri!!, filename)
                PhotoEntity(
                    idPhoto = 0, // Auto-generated by Room
                    namePhoto = filePath,
                    descriptionPhoto = photo.description,
                    idRealEstate = realEstateId
                )
            }
            realEstateRepository.insertPhotos(photoEntities)

            // Insert associations with points of interest
            val isNextToEntities = pointsOfInterest.map { poiId ->
                IsNextToEntity(
                    idRealEstate = realEstateId,
                    idPoi = poiId
                )
            }
            realEstateRepository.insertIsNextToEntities(isNextToEntities)
        }
    }

}