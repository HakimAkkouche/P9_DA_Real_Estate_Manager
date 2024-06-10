package com.haksoftware.p9_da_real_estate_manager.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haksoftware.p9_da_real_estate_manager.data.repository.MapRepository
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository
import com.haksoftware.p9_da_real_estate_manager.ui.edit.GetRealEstateCallBack
import kotlinx.coroutines.launch

class DetailViewModel(private val realEstateRepository: RealEstateRepository, private val mapRepository: MapRepository) : ViewModel() {

    fun getMapUrl(address: String): String {

        return mapRepository.getMapUrl(address)
    }

    fun getRealEstateWithDetails(realEstateId: Int, callback: GetRealEstateCallBack) {
        viewModelScope.launch {
            val realEstateWithDetails = realEstateRepository.getRealEstate(realEstateId)
            callback.onGetRealEstateReponse(realEstateWithDetails)
        }
    }
}