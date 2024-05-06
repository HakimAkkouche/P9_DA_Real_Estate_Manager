package com.haksoftware.p9_da_real_estate_manager.ui.gallery

import android.provider.ContactsContract.Contacts.Photo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel(val photos: List<Photo>) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text
}