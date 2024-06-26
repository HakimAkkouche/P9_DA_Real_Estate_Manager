package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
/**
 * Interface for handling the removal of a photo from a list or collection.
 */
interface RemovePhotoListener {

    /**
     * Called when a photo is removed.
     *
     * @param photoEntity The photo entity that has been removed.
     */
    fun onPhotoRemoved(photoEntity: PhotoEntity)
}
