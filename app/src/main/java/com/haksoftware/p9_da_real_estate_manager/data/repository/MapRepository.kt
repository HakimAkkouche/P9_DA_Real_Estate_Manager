package com.haksoftware.p9_da_real_estate_manager.data.repository

import com.haksoftware.p9_da_real_estate_manager.BuildConfig

/**
 * Repository class that manages operations related to map services.
 * Provides methods to generate map URLs.
 */
class MapRepository {

    companion object {
        @Volatile
        private var instance: MapRepository? = null

        /**
         * Returns the single instance of MapRepository, creating it if necessary.
         * This is thread-safe and ensures only one instance of the repository exists.
         *
         * @return The singleton instance of MapRepository.
         */
        fun getInstance(): MapRepository {
            return instance ?: synchronized(this) {
                instance ?: MapRepository().also { instance = it }
            }
        }
    }

    /**
     * Generates a URL for a static map image centered on the given address.
     *
     * @param address The address to center the map on.
     * @return The URL of the static map image.
     */
    fun getMapUrl(address: String): String {
        return "https://maps.googleapis.com/maps/api/staticmap?center=$address&zoom=17&size=1200x800&key=${BuildConfig.GOOGLE_MAPS_API_KEY}"
    }
}
