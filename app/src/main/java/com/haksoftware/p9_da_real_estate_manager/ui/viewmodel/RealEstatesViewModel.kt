package com.haksoftware.p9_da_real_estate_manager.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.haksoftware.p9_da_real_estate_manager.data.entity.IsNextToEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PointOfInterestEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import com.haksoftware.p9_da_real_estate_manager.data.repository.MapRepository
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository
import com.haksoftware.p9_da_real_estate_manager.ui.edit.GetRealEstateCallBack
import com.haksoftware.p9_da_real_estate_manager.ui.search.SearchCallback
import com.haksoftware.realestatemanager.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

/**
 * ViewModel for managing real estate data, including fetching, updating, and maintaining state.
 * @property realEstateRepository Repository for real estate data.
 * @property mapRepository Repository for map data.
 */
class RealEstatesViewModel(
    private val realEstateRepository: RealEstateRepository,
    private val mapRepository: MapRepository
) : ViewModel() {

    private var _currentRealEstate: MutableLiveData<RealEstateWithDetails> = MutableLiveData()
    private val _updateSuccess = MutableLiveData<Boolean>()

    private val _realEstateWithDetailsOriginal = MutableLiveData<RealEstateWithDetails>()
    private val _realEstateWithDetailsModified = MutableLiveData<RealEstateWithDetails>()

    private val _realtorsLiveData: LiveData<List<RealtorEntity>> = realEstateRepository.getAllRealtor()
    private val _typesLiveData: LiveData<List<TypeEntity>> = realEstateRepository.getAllTypes()
    private val _pOiLiveData: LiveData<List<PointOfInterestEntity>> = realEstateRepository.getAllPointOfInterest()

    private val _isNextTo = MutableStateFlow(emptyList<Int>())

    private val _photosEntityList = MutableStateFlow<MutableList<PhotoEntity>>(mutableListOf())
    private val _photosToAddEntityList = MutableStateFlow<MutableList<PhotoEntity>>(mutableListOf())
    private val _photosToRemoveEntityList = MutableStateFlow<MutableList<PhotoEntity>>(mutableListOf())

    val realEstates: LiveData<List<RealEstateWithDetails>> = realEstateRepository.getAllRealEstates()

    val currentRealEstate: LiveData<RealEstateWithDetails> get() = _currentRealEstate
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess
    val realEstateIdOriginal get() = _realEstateWithDetailsOriginal
    val realEstateWithDetailsModified get() = _realEstateWithDetailsModified
    val realtorLiveData: LiveData<List<RealtorEntity>> get() = _realtorsLiveData
    val typesLiveData: LiveData<List<TypeEntity>> get() = _typesLiveData
    val pOILiveData: LiveData<List<PointOfInterestEntity>> get() = _pOiLiveData
    val isNextTo: StateFlow<List<Int>> get() = _isNextTo

    val photosEntityListLiveData: LiveData<List<PhotoEntity>> = _photosEntityList
        .map { it.toList() }
        .asLiveData()
    val photosToAddEntityList: StateFlow<MutableList<PhotoEntity>> get() = _photosToAddEntityList
    val photosToRemoveEntityList: StateFlow<MutableList<PhotoEntity>> get() = _photosToRemoveEntityList

    val searchResults: MutableLiveData<List<RealEstateWithDetails>> get() = MutableLiveData()

    /**
     * Searches for real estates based on various criteria.
     * @param callback The callback to handle the search results.
     * @param typeId The type ID of the real estate.
     * @param startDate The start date for filtering.
     * @param endDate The end date for filtering.
     * @param minSurface The minimum surface area.
     * @param maxSurface The maximum surface area.
     * @param minPrice The minimum price.
     * @param maxPrice The maximum price.
     * @param roomCount The number of rooms.
     * @param bathroomCount The number of bathrooms.
     * @param minPhotoCount The minimum number of photos.
     * @param poi List of points of interest.
     * @param isSold Whether the real estate is sold.
     */
    fun searchRealEstates(
        callback: SearchCallback, typeId: Int?,
        startDate: Long?, endDate: Long?, minSurface: Int?, maxSurface: Int?,
        minPrice: Int?, maxPrice: Int?, roomCount: Int?, bathroomCount: Int?,
        minPhotoCount: Int?, poi: MutableList<Int>, isSold: Boolean
    ) {
        viewModelScope.launch {
            val results = realEstateRepository.searchRealEstates(
                typeId, startDate, endDate, minSurface, maxSurface, minPrice, maxPrice, roomCount,
                bathroomCount, minPhotoCount, poi, isSold
            )
            searchResults.value = results
            callback.onSearchResultCallback(results)
        }
    }

    /**
     * Updates the current real estate details.
     * @param it The new real estate details.
     */
    fun updateCurrentRealEstate(it: RealEstateWithDetails) {
        _currentRealEstate.value = it
    }

    /**
     * Gets the map URL for a given address.
     * @param address The address to get the map URL for.
     * @return The map URL.
     */
    fun getMapUrl(address: String): String {
        return mapRepository.getMapUrl(address)
    }

    /**
     * Gets the real estate details for a given ID and triggers a callback.
     * @param realEstateId The ID of the real estate.
     * @param callback The callback to handle the response.
     */
    fun getRealEstateWithDetails(realEstateId: Int, callback: GetRealEstateCallBack) {
        viewModelScope.launch {
            val realEstateWithDetails = realEstateRepository.getRealEstate(realEstateId)
            callback.onGetRealEstateResponse(realEstateWithDetails)
        }
    }

    /**
     * Sets the original real estate ID.
     * @param realEstateId The ID of the real estate.
     */
    fun setOriginalRealEstateId(realEstateId: Int) {
        viewModelScope.launch {
            _realEstateWithDetailsOriginal.value = realEstateRepository.getRealEstate(realEstateId)
        }
    }

    /**
     * Updates the modified real estate details.
     * @param realEstateWithDetails The modified real estate details.
     */
    fun updateRealEstateWithDetails(realEstateWithDetails: RealEstateWithDetails) {
        _realEstateWithDetailsModified.value = realEstateWithDetails
    }

    /**
     * Adds a photo entity to the list of photos to add.
     * @param photoViewEntity The photo entity to add.
     */
    fun photoToAddEntity(photoViewEntity: PhotoEntity) {
        _photosToAddEntityList.update { list ->
            list.apply { add(photoViewEntity) }
        }
    }

    /**
     * Adds a photo entity to the list of photos to remove.
     * @param photoEntity The photo entity to remove.
     */
    fun photoToRemove(photoEntity: PhotoEntity) {
        _photosToRemoveEntityList.update { list ->
            list.apply { add(photoEntity) }
        }
    }

    /**
     * Updates the list of points of interest for the real estate.
     * @param pointsOfInterest The list of points of interest.
     */
    fun updatePointsOfInterest(pointsOfInterest: List<Int>) {
        _isNextTo.value = pointsOfInterest
    }

    /**
     * Updates the real estate details.
     */
    fun updateRealEstate() {
        val modifiedDetails = _realEstateWithDetailsModified.value ?: return
        viewModelScope.launch {
            try {
                Log.d("updateRealEstate", "Coroutine started")
                if (_realEstateWithDetailsOriginal.value != _realEstateWithDetailsModified.value) {
                    withContext(Dispatchers.IO) {
                        Log.d("updateRealEstate", "Updating real estate")
                        realEstateRepository.updateRealEstate(modifiedDetails.realEstate)
                    }
                }
                Log.d("updateRealEstate", "Update successful")
                _updateSuccess.postValue(true)
            } catch (e: IOException) {
                _updateSuccess.postValue(false)
                Log.d("updateRealEstate", "Update real estate entity ${e.message}")
            }
        }
    }

    /**
     * Adds photos to the real estate.
     * @param context The context to use for saving images.
     */
    fun addPhotos(context: Context) {
        viewModelScope.launch {
            try {
                if (_photosToAddEntityList.value.isNotEmpty()) {
                    val photoEntities = _photosToAddEntityList.value.map { photo ->
                        val filename = UUID.randomUUID().toString() + ".jpg"
                        val filePath = Utils.saveImageToInternalStorage(
                            context,
                            photo.namePhoto.toUri(),
                            filename
                        )
                        PhotoEntity(
                            idPhoto = 0,
                            namePhoto = filePath,
                            descriptionPhoto = photo.descriptionPhoto,
                            idRealEstate = _realEstateWithDetailsOriginal.value!!.realEstate.idRealEstate
                        )
                    }
                    Log.d("updateRealEstate", "Inserting photos")
                    realEstateRepository.insertPhotos(photoEntities)
                }
            } catch (e: IOException) {
                Log.d("Error", "Add photo entity ${e.message}")
            }
        }
    }

    /**
     * Removes photos from the real estate.
     */
    fun removePhotos() {
        viewModelScope.launch {
            try {
                if (_photosToRemoveEntityList.value.isNotEmpty()) {
                    Log.d("updateRealEstate", "Deleting photos")
                    _photosToRemoveEntityList.value.forEach {
                        realEstateRepository.deletePhoto(it)
                    }
                }
            } catch (e: IOException) {
                Log.d("Error", "Remove photo entity ${e.message}")
            }
        }
    }

    /**
     * Updates the points of interest associated with the real estate.
     */
    fun updateISNextTo() {
        viewModelScope.launch {
            try {
                val isNextToEntities = _isNextTo.value.map { poiId ->
                    IsNextToEntity(
                        idRealEstate = _realEstateWithDetailsOriginal.value!!.realEstate.idRealEstate,
                        idPoi = poiId
                    )
                }
                if (isNextToEntities.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        if (_realEstateWithDetailsOriginal.value!!.pointsOfInterest.isNotEmpty()) {
                            Log.d("updateRealEstate", "Clearing POIs before update")
                            realEstateRepository.clearPOIsBeforeUpdate(
                                _realEstateWithDetailsOriginal.value!!.realEstate.idRealEstate
                            )
                        }
                        Log.d("updateRealEstate", "Inserting new POIs")
                        realEstateRepository.insertIsNextToEntities(isNextToEntities)
                        Log.d("updateRealEstate", "Inserted new POIs")
                    }
                }
                _updateSuccess.postValue(true)
            } catch (e: IOException) {
                _updateSuccess.postValue(false)
            }
        }
    }
}
