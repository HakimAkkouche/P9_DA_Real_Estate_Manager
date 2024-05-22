package com.haksoftware.p9_da_real_estate_manager.ui.edit

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haksoftware.p9_da_real_estate_manager.data.repository.RealEstateRepository

class EditViewModel(application: Application, realEstateRepository: RealEstateRepository) : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
}