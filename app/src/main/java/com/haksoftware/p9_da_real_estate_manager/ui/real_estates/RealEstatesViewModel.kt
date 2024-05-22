package com.haksoftware.p9_da_real_estate_manager.ui.real_estates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository

class RealEstatesViewModel(application: Application, realEstateRepository: RealEstateRepository) : AndroidViewModel(application) {

    val realEstates: LiveData<List<RealEstateWithDetails>>
    init {
        realEstates = realEstateRepository.getAllRealEstates()
    }
}