package com.haksoftware.p9_da_real_estate_manager.ui.real_estates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haksoftware.p9_da_real_estate_manager.data.entity.PointOfInterestEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository
import com.haksoftware.p9_da_real_estate_manager.ui.search.SearchCallback
import kotlinx.coroutines.launch

class RealEstatesViewModel(application: Application, private val realEstateRepository: RealEstateRepository) : AndroidViewModel(application)  {

    val realEstates: LiveData<List<RealEstateWithDetails>> = realEstateRepository.getAllRealEstates()
    private val _typesLiveData: LiveData<List<TypeEntity>> = realEstateRepository.getAllTypes()
    private val _pOiLiveData: LiveData<List<PointOfInterestEntity>> = realEstateRepository.getAllPointOfInterest()

    val searchResults: MutableLiveData<List<RealEstateWithDetails>> get() = MutableLiveData()
    val typesLiveData: LiveData<List<TypeEntity>> get() = _typesLiveData

    val pOILiveData: LiveData<List<PointOfInterestEntity>> get() = _pOiLiveData

    fun searchRealEstates(callback: SearchCallback, typeId: Int?,
        startDate: Long?, endDate: Long?, minSurface: Int?, maxSurface: Int?,
        minPrice: Int?, maxPrice: Int?, roomCount: Int?, bathroomCount: Int?, minPhotoCount: Int?, poi: MutableList<Int>, isSold: Boolean
    ) {
        viewModelScope.launch {
            val results = realEstateRepository.searchRealEstates(typeId, startDate, endDate, minSurface, maxSurface, minPrice, maxPrice, roomCount, bathroomCount, minPhotoCount, poi, isSold)
            searchResults.value = results
            callback.onSearchResultCallback(results)
        }
    }
}