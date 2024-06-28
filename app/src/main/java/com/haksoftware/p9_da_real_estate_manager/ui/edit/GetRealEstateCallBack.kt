package com.haksoftware.p9_da_real_estate_manager.ui.edit

import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
/**
 * Interface for receiving a response when a real estate item is fetched.
 */
interface GetRealEstateCallBack {

    /**
     * Called when the details of a real estate item are retrieved.
     *
     * @param realEstateWithDetails The detailed information of the fetched real estate item.
     */
    fun onGetRealEstateResponse(realEstateWithDetails: RealEstateWithDetails)
}
