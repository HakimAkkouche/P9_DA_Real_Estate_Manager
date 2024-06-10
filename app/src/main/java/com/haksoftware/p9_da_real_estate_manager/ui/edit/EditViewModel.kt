package com.haksoftware.p9_da_real_estate_manager.ui.edit

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.haksoftware.p9_da_real_estate_manager.data.entity.IsNextToEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PointOfInterestEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository
import com.haksoftware.realestatemanager.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class EditViewModel(application: Application,
                    private val realEstateRepository: RealEstateRepository
) : AndroidViewModel(application) {
    private val _updateSuccess = MutableLiveData<Boolean>()

    private val _realEstateWithDetailsOriginal = MutableLiveData<RealEstateWithDetails>()
    private val _realEstateWithDetailsModified = MutableLiveData<RealEstateWithDetails>()
    private val _pOiLiveData: LiveData<List<PointOfInterestEntity>> = realEstateRepository.getAllPointOfInterest()
    private val _realtorsLiveData: LiveData<List<RealtorEntity>> = realEstateRepository.getAllRealtor()
    private val _typesLiveData: LiveData<List<TypeEntity>> = realEstateRepository.getAllTypes()

    // Observables for UI
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    fun setRealEstateWithDetails(realEstateId: Int) {
        viewModelScope.launch {
            _realEstateWithDetailsOriginal.value = realEstateRepository.getRealEstate(realEstateId)
        }
    }

    fun updateRealEstateWithDetails(realEstateWithDetails: RealEstateWithDetails) {
        _realEstateWithDetailsModified.value = realEstateWithDetails
    }
    private val _pointsOfInterest = MutableStateFlow(emptyList<Int>())


    private val _photosEntityList = MutableStateFlow<MutableList<PhotoEntity>>(mutableListOf())
    private val _photosToAddEntityList = MutableStateFlow<MutableList<PhotoEntity>>(mutableListOf())
    private val _photosToRemoveEntityList = MutableStateFlow<MutableList<PhotoEntity>>(mutableListOf())
    val pOILiveData: LiveData<List<PointOfInterestEntity>> get() = _pOiLiveData
    val realtorLiveData: LiveData<List<RealtorEntity>> get() = _realtorsLiveData
    val typesLiveData: LiveData<List<TypeEntity>> get() = _typesLiveData

    // Transform StateFlow to LiveData
    val photosEntityListLiveData: LiveData<List<PhotoEntity>> = _photosEntityList
        .map { it.toList() }
        .asLiveData()
    fun addPhotoEntity(photoViewState: PhotoEntity) {
        _photosToAddEntityList.update { list ->
            list.apply { add(photoViewState) }
        }
    }
    fun removePhotoEntity(photoEntity: PhotoEntity) {
        _photosToRemoveEntityList.update { list ->
            list.apply { add(photoEntity) }
        }
    }

    fun updatePointsOfInterest(pointsOfInterest: List<Int>) {
        _pointsOfInterest.value = pointsOfInterest
    }
    fun updateRealEstate() {
        val modifiedDetails = _realEstateWithDetailsModified.value ?: return
        viewModelScope.launch {
            try {
                Log.d("updateRealEstate", "Coroutine started")
                // Perform the real estate update if details have changed
                if (_realEstateWithDetailsOriginal.value != _realEstateWithDetailsModified.value) {
                    withContext(Dispatchers.IO) {
                        Log.d("updateRealEstate", "Updating real estate")
                        realEstateRepository.updateRealEstate(modifiedDetails.realEstate)
                    }
                }

                Log.d("updateRealEstate", "Update successful")
                _updateSuccess.postValue(true)
            }
            catch(e: IOException) {
                Log.d("update realEstate", "update realestate entity ${e.message}")
            }
        }
    }
    fun addPhotos() {
        viewModelScope.launch {
            // Handle photo updates
            try {
                if(_photosToAddEntityList.value.isNotEmpty()) {
                    val photoEntities = _photosToAddEntityList.value.map { photo ->
                        val filename = UUID.randomUUID().toString() + ".jpg"
                        val filePath = Utils.saveImageToInternalStorage(
                            getApplication(),
                            photo.namePhoto.toUri(),
                            filename
                        )
                        PhotoEntity(
                            idPhoto = 0, // Auto-generated by Room
                            namePhoto = filePath,
                            descriptionPhoto = photo.descriptionPhoto,
                            idRealEstate = _realEstateWithDetailsOriginal.value!!.realEstate.idRealEstate
                        )
                    }
                    Log.d("updateRealEstate", "Inserting photos")
                    realEstateRepository.insertPhotos(photoEntities)
                }

            }
            catch(e: IOException) {
                Log.d("Error", "add photo entity ${e.message}")
            }

        }
    }
    fun removePhotos() {
        viewModelScope.launch {
            // Handle photo updates
            try {
                if (_photosToRemoveEntityList.value.isNotEmpty()) {
                    Log.d("updateRealEstate", "Deleting photos")
                    _photosToRemoveEntityList.value.forEach {
                        realEstateRepository.deletePhoto(
                            it
                        )
                    }
                }


            }
            catch(e: IOException) {
                Log.d("Error", "remove photo entity ${e.message}")
            }

        }
    }
    fun updateISNextTo() {

        viewModelScope.launch {
            try {
                // Handle points of interest updates
                val isNextToEntities = _pointsOfInterest.value.map { poiId ->
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