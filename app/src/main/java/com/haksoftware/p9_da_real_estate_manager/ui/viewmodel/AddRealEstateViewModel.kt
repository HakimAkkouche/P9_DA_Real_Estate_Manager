package com.haksoftware.p9_da_real_estate_manager.ui.viewmodel

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
/**
 * ViewModel for managing the addition of new real estate entries.
 * @property realEstateRepository Repository for real estate data.
 */
class AddRealEstateViewModel(private val realEstateRepository: RealEstateRepository) : ViewModel() {
    private val _insertSuccess = MutableLiveData<Boolean>()
    val insertSuccess: LiveData<Boolean> get() = _insertSuccess

    private val _pOiLiveData: LiveData<List<PointOfInterestEntity>> = realEstateRepository.getAllPointOfInterest()
    private val _realtorsLiveData: LiveData<List<RealtorEntity>> = realEstateRepository.getAllRealtor()
    private val _typesLiveData: LiveData<List<TypeEntity>> = realEstateRepository.getAllTypes()

    private val _price = MutableStateFlow("")
    private val _surface = MutableStateFlow("")
    private val _description = MutableStateFlow("")
    private val _address = MutableLiveData<String>()
    private val _city = MutableLiveData<String>()
    private val _zipCode = MutableLiveData<String>()
    private val _country = MutableLiveData<String>()
    private val _roomCount = MutableStateFlow(0)
    private val _bathroomCount = MutableStateFlow(0)
    private val _idRealtor = MutableStateFlow(0)
    private val _idType = MutableStateFlow(0)
    private val _pointsOfInterest = MutableStateFlow(emptyList<Int>())

    val pOILiveData: LiveData<List<PointOfInterestEntity>> get() = _pOiLiveData
    val realtorLiveData: LiveData<List<RealtorEntity>> get() = _realtorsLiveData
    val typesLiveData: LiveData<List<TypeEntity>> get() = _typesLiveData

    // Public StateFlows to be observed from the Fragment
    val price: StateFlow<String> = _price
    val address: LiveData<String> get() = _address
    val city: LiveData<String> get() = _city
    val idRealtor: StateFlow<Int> get() = _idRealtor
    val idType: StateFlow<Int> get() = _idType

    private val _photoEntityList = MutableStateFlow<MutableList<PhotoEntity>>(mutableListOf())
    private val photoEntityList: StateFlow<MutableList<PhotoEntity>> get() = _photoEntityList

    // Transform StateFlow to LiveData
    val photoEntityListLiveData: LiveData<List<PhotoEntity>> = _photoEntityList
        .map { it.toList() }
        .asLiveData()

    /**
     * Adds a photo entity to the list of photos.
     * @param photoEntity The photo entity to add.
     */
    fun addPhotoEntity(photoEntity: PhotoEntity) {
        val updatedList = _photoEntityList.value.toMutableList()
        updatedList.add(photoEntity)
        _photoEntityList.value = updatedList
    }

    /**
     * Removes a photo entity from the list of photos.
     * @param photoEntity The photo entity to remove.
     */
    fun removePhotoEntity(photoEntity: PhotoEntity) {
        val updatedList = _photoEntityList.value.toMutableList()
        updatedList.remove(photoEntity)
        _photoEntityList.value = updatedList
    }

    val isFormValid: StateFlow<Boolean> = combine(
        _price, _surface, _description
    ) { values: Array<String> ->
        values[0].isNotBlank() && values[1].isNotBlank() && values[2].isNotBlank()
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    /**
     * Updates the price value.
     * @param price The new price value.
     */
    fun updatePrice(price: String) {
        _price.value = price
    }

    /**
     * Updates the surface value.
     * @param surface The new surface value.
     */
    fun updateSurface(surface: String) {
        _surface.value = surface
    }

    /**
     * Updates the description value.
     * @param description The new description value.
     */
    fun updateDescription(description: String) {
        _description.value = description
    }

    /**
     * Updates the ZIP code value.
     * @param zipCode The new ZIP code value.
     */
    fun updateZipCode(zipCode: String) {
        _zipCode.value = zipCode
    }

    /**
     * Updates the city value.
     * @param city The new city value.
     */
    fun updateCity(city: String) {
        _city.value = city
    }

    /**
     * Updates the country value.
     * @param country The new country value.
     */
    fun updateCountry(country: String) {
        _country.value = country
    }

    /**
     * Updates the address value.
     * @param address The new address value.
     */
    fun updateAddress(address: String) {
        _address.value = address
    }

    /**
     * Updates the room count value.
     * @param roomCount The new room count value.
     */
    fun updateRoomCount(roomCount: Int) {
        _roomCount.value = roomCount
    }

    /**
     * Updates the bathroom count value.
     * @param bathroomCount The new bathroom count value.
     */
    fun updateBathroomCount(bathroomCount: Int) {
        _bathroomCount.value = bathroomCount
    }

    /**
     * Updates the realtor ID value.
     * @param idRealtor The new realtor ID value.
     */
    fun updateIdRealtor(idRealtor: Int) {
        _idRealtor.value = idRealtor
    }

    /**
     * Updates the type ID value.
     * @param idType The new type ID value.
     */
    fun updateIdType(idType: Int) {
        _idType.value = idType
    }

    /**
     * Updates the points of interest.
     * @param pointsOfInterest The new points of interest.
     */
    fun updatePointsOfInterest(pointsOfInterest: List<Int>) {
        _pointsOfInterest.value = pointsOfInterest
    }

    /**
     * Saves the real estate information to the repository.
     * @param context The context to use for saving images.
     */
    fun saveRealEstate(context: Context) {
        viewModelScope.launch {
            try {
                val price = _price.value.toInt()
                val surface = _surface.value.toInt()
                val description = _description.value
                val address = _address.value
                val zipCode = _zipCode.value
                val city = _city.value
                val state = _country.value
                val roomCount = _roomCount.value
                val bathroomCount = _bathroomCount.value
                val idRealtor = _idRealtor.value
                val idType = _idType.value
                val pointsOfInterest = _pointsOfInterest.value
                val photos = photoEntityList.value

                val realEstateEntity = RealEstateEntity(
                    idRealEstate = 0, // Auto-generated by Room
                    price = price,
                    squareFeet = surface,
                    roomCount = roomCount,
                    bathroomCount = bathroomCount,
                    descriptionRealEstate = description,
                    address = address!!,
                    postalCode = zipCode!!,
                    city = city!!,
                    state = state!!,
                    creationDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                    soldDate = null,
                    idRealtor = idRealtor,
                    idType = idType
                )

                // Insert real estate
                val realEstateId = realEstateRepository.insertRealEstate(realEstateEntity).toInt()

                // Insert photos
                val photoEntities = photos.map { photo ->
                    val filename = UUID.randomUUID().toString() + ".jpg"
                    val filePath = Utils.saveImageToInternalStorage(context, photo.namePhoto.toUri(), filename)
                    PhotoEntity(
                        idPhoto = 0, // Auto-generated by Room
                        namePhoto = filePath,
                        descriptionPhoto = photo.descriptionPhoto,
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

                _insertSuccess.postValue(true)
            } catch (e: Exception) {
                _insertSuccess.postValue(false)
            }
        }
    }
}
