package com.haksoftware.p9_da_real_estate_manager.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository
import com.haksoftware.p9_da_real_estate_manager.ui.addrealestate.AddRealEstateViewModel
import com.haksoftware.p9_da_real_estate_manager.ui.detail.DetailViewModel
import com.haksoftware.p9_da_real_estate_manager.ui.edit.EditViewModel
import com.haksoftware.p9_da_real_estate_manager.ui.real_estates.RealEstatesViewModel


class ViewModelFactory private constructor(
    private val application: Application,
    private val realEstateRepository: RealEstateRepository
) : ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    application,
                    RealEstateRepository.getInstance(application)
                ).also { instance = it }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RealEstatesViewModel::class.java) ->
                RealEstatesViewModel(application, realEstateRepository) as T
            modelClass.isAssignableFrom(AddRealEstateViewModel::class.java) ->
                AddRealEstateViewModel(application, realEstateRepository) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) ->
                DetailViewModel(application, realEstateRepository) as T
            modelClass.isAssignableFrom(EditViewModel::class.java) ->
                EditViewModel(application, realEstateRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}