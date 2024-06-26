package com.haksoftware.p9_da_real_estate_manager.ui.addrealestate

import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity

/**
 * Interface for handling the addition of a photo from a dialog.
 */
interface AddPhotoDialogListener {

    /**
     * Called when a photo is added through the dialog.
     *
     * @param photoEntity The photo entity that has been added.
     */
    fun onPhotoDialogAdded(photoEntity: PhotoEntity)
}
