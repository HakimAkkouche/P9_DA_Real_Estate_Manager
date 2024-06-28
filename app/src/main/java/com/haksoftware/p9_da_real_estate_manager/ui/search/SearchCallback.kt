package com.haksoftware.p9_da_real_estate_manager.ui.search

import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails

/**
 * Interface for handling search results in the real estate application.
 */
interface SearchCallback {

    /**
     * Called when the search results are available.
     *
     * @param result A list of real estate details that match the search criteria.
     */
    fun onSearchResultCallback(result: List<RealEstateWithDetails>)
}
