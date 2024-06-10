package com.haksoftware.p9_da_real_estate_manager.ui.real_estates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haksoftware.p9_da_real_estate_manager.data.entity.PointOfInterestEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository
import com.haksoftware.p9_da_real_estate_manager.ui.search.SearchCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RealEstatesViewModel(application: Application, private val realEstateRepository: RealEstateRepository) : AndroidViewModel(application)  {

    val realEstates: LiveData<List<RealEstateWithDetails>> = realEstateRepository.getAllRealEstates()

    private val _pOiLiveData: LiveData<List<PointOfInterestEntity>> = realEstateRepository.getAllPointOfInterest()

    val searchResults: MutableLiveData<List<RealEstateWithDetails>> get() = MutableLiveData()

    val pOILiveData: LiveData<List<PointOfInterestEntity>> get() = _pOiLiveData

    fun searchRealEstates(callback: SearchCallback,
        startDate: Long?, endDate: Long?, minSurface: Int?, maxSurface: Int?,
        minPrice: Int?, maxPrice: Int?, roomCount: Int?, bathroomCount: Int?, poi: MutableList<Int>
    ) {
        viewModelScope.launch {
            val results = realEstateRepository.searchRealEstates(startDate, endDate, minSurface, maxSurface, minPrice, maxPrice, roomCount, bathroomCount, poi)
            searchResults.value = results
            callback.onSearchResultCallback(results)
        }
    }
}