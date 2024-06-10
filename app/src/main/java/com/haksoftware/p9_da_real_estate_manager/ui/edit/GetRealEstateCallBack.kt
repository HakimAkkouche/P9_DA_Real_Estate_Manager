package com.haksoftware.p9_da_real_estate_manager.ui.edit

import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails

interface GetRealEstateCallBack {
    fun onGetRealEstateReponse(realEstateWithDetails: RealEstateWithDetails)
}