package com.haksoftware.p9_da_real_estate_manager.ui.real_estates

import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails

interface OnItemClickListener {
    fun onItemClick(realEstate: RealEstateWithDetails)
}