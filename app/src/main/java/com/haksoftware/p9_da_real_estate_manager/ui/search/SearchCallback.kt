package com.haksoftware.p9_da_real_estate_manager.ui.search

import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails

interface SearchCallback {
    fun onSearchResultCallback(result: List<RealEstateWithDetails>)
}