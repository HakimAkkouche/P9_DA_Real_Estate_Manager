package com.haksoftware.p9_da_real_estate_manager.ui.detail

import androidx.lifecycle.ViewModel
import com.haksoftware.p9_da_real_estate_manager.BuildConfig
import com.haksoftware.p9_da_real_estate_manager.data.repository.MapRepository

class DetailViewModel(private val repository: MapRepository) : ViewModel() {

    fun getMapUrl(address: String): String {

        return repository.getMapUrl(address)
    }
}