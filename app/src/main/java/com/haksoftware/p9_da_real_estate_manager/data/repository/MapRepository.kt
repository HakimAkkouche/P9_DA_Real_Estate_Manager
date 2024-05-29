package com.haksoftware.p9_da_real_estate_manager.data.repository

import com.haksoftware.p9_da_real_estate_manager.BuildConfig

class MapRepository {
    companion object {
        @Volatile
        private var instance: MapRepository? = null

        fun getInstance(): MapRepository {
            return instance ?: synchronized(this) {
                instance ?: MapRepository().also { instance = it }
            }
        }
    }
    fun getMapUrl(address: String): String {
        return "https://maps.googleapis.com/maps/api/staticmap?center=$address&zoom=17&size=600x400&key=${BuildConfig.GOOGLE_MAPS_API_KEY}"
    }
}