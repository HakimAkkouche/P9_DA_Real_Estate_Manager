package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity

interface RemovePhotoListener {
    fun onPhotoRemoved(photoEntity: PhotoEntity)
}