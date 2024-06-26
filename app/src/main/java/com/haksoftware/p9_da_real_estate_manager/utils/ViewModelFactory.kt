package com.haksoftware.p9_da_real_estate_manager.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.haksoftware.p9_da_real_estate_manager.data.manager.RealEstateManagerDatabase
import com.haksoftware.p9_da_real_estate_manager.data.repository.MapRepository
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.AddRealEstateViewModel
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.LoanSimulatorViewModel
import com.haksoftware.p9_da_real_estate_manager.ui.viewmodel.RealEstatesViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


class ViewModelFactory private constructor(
    val application: Application,
    ioDispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {

    private val db = RealEstateManagerDatabase.getDatabase(application.applicationContext)
    private val realEstateRepository: RealEstateRepository = RealEstateRepository.getInstance(
        db.realEstateDao(),
        db.pointOfInterestDao(),
        db.realtorDao(),
        db.typeDao(),
        db.photoDao(),
        db.isNextToDao(),
        ioDispatcher)
    private val mapRepository: MapRepository = MapRepository.getInstance()

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    application,
                    Dispatchers.IO
                ).also { instance = it }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RealEstatesViewModel::class.java) ->
                RealEstatesViewModel(realEstateRepository, mapRepository) as T
            modelClass.isAssignableFrom(AddRealEstateViewModel::class.java) ->
                AddRealEstateViewModel(realEstateRepository) as T
            modelClass.isAssignableFrom(LoanSimulatorViewModel::class.java) ->
                LoanSimulatorViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}