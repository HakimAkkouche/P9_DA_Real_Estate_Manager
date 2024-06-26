package com.haksoftware.p9_da_real_estate_manager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.haksoftware.p9_da_real_estate_manager.data.dao.IsNextToDao
import com.haksoftware.p9_da_real_estate_manager.data.dao.PhotoDao
import com.haksoftware.p9_da_real_estate_manager.data.dao.PointOfInterestDao
import com.haksoftware.p9_da_real_estate_manager.data.dao.RealEstateDao
import com.haksoftware.p9_da_real_estate_manager.data.dao.RealtorDao
import com.haksoftware.p9_da_real_estate_manager.data.dao.TypeDao
import com.haksoftware.p9_da_real_estate_manager.data.entity.IsNextToEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PointOfInterestEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Repository class that manages the data sources for Real Estate.
 * Handles data operations and provides a clean API for data access to the rest of the application.
 */
class RealEstateRepository(
    private val realEstateDao: RealEstateDao,
    private val pointOfInterestDao: PointOfInterestDao,
    private val realtorDao: RealtorDao,
    private val typeDao: TypeDao,
    private val photoDao: PhotoDao,
    private val isNextToDao: IsNextToDao,
    private val ioDispatcher: CoroutineDispatcher
) {

    companion object {
        @Volatile
        private var instance: RealEstateRepository? = null

        /**
         * Returns the single instance of RealEstateRepository, creating it if necessary.
         * This is thread-safe and ensures only one instance of the repository exists.
         *
         * @param realEstateDao DAO for accessing real estate data.
         * @param pointOfInterestDao DAO for accessing point of interest data.
         * @param realtorDao DAO for accessing realtor data.
         * @param typeDao DAO for accessing type data.
         * @param photoDao DAO for accessing photo data.
         * @param isNextToDao DAO for accessing IsNextTo data.
         * @param ioDispatcher Dispatcher for IO operations.
         * @return The singleton instance of RealEstateRepository.
         */
        fun getInstance(
            realEstateDao: RealEstateDao,
            pointOfInterestDao: PointOfInterestDao,
            realtorDao: RealtorDao,
            typeDao: TypeDao,
            photoDao: PhotoDao,
            isNextToDao: IsNextToDao,
            ioDispatcher: CoroutineDispatcher
        ): RealEstateRepository {
            return instance ?: synchronized(this) {
                instance ?: RealEstateRepository(
                    realEstateDao,
                    pointOfInterestDao,
                    realtorDao,
                    typeDao,
                    photoDao,
                    isNextToDao,
                    ioDispatcher
                ).also { instance = it }
            }
        }
    }

    /**
     * Retrieves the details of a specific real estate property by its ID.
     *
     * @param realEstateId The ID of the real estate property.
     * @return The RealEstateWithDetails object containing the details of the property.
     */
    suspend fun getRealEstate(realEstateId: Int): RealEstateWithDetails {
        return withContext(ioDispatcher) { realEstateDao.getRealEstateWithDetails(realEstateId) }
    }

    /**
     * Retrieves a LiveData list of all real estate properties with their details.
     *
     * @return LiveData list of all RealEstateWithDetails objects.
     */
    fun getAllRealEstates(): LiveData<List<RealEstateWithDetails>> {
        return realEstateDao.getAllRealEstatesWithDetails()
    }

    /**
     * Retrieves a LiveData list of all points of interest.
     *
     * @return LiveData list of all PointOfInterestEntity objects.
     */
    fun getAllPointOfInterest(): LiveData<List<PointOfInterestEntity>> {
        return pointOfInterestDao.getPointsOfInterest()
    }

    /**
     * Retrieves a LiveData list of all realtors.
     *
     * @return LiveData list of all RealtorEntity objects.
     */
    fun getAllRealtor(): LiveData<List<RealtorEntity>> {
        return realtorDao.getAllRealtor()
    }

    /**
     * Retrieves a LiveData list of all property types.
     *
     * @return LiveData list of all TypeEntity objects.
     */
    fun getAllTypes(): LiveData<List<TypeEntity>> {
        return typeDao.getAllTypes()
    }

    /**
     * Inserts a new real estate property into the database.
     *
     * @param realEstateEntity The RealEstateEntity object to be inserted.
     * @return The ID of the newly inserted real estate property.
     */
    suspend fun insertRealEstate(realEstateEntity: RealEstateEntity): Long {
        return withContext(ioDispatcher) {
            realEstateDao.insert(realEstateEntity)
        }
    }

    /**
     * Inserts a list of photos into the database.
     *
     * @param photos The list of PhotoEntity objects to be inserted.
     */
    suspend fun insertPhotos(photos: List<PhotoEntity>) {
        withContext(ioDispatcher) {
            photoDao.insertPhotos(photos)
        }
    }

    /**
     * Inserts a list of IsNextTo entities into the database.
     *
     * @param isNextToEntities The list of IsNextToEntity objects to be inserted.
     */
    suspend fun insertIsNextToEntities(isNextToEntities: List<IsNextToEntity>) {
        withContext(ioDispatcher) {
            try {
                isNextToDao.insertIsNextTo(isNextToEntities)
            } catch (e: Exception) {
                Log.println(Log.ERROR, "ERROR Delete", e.message + " " + e.stackTraceToString())
            }
        }
    }

    /**
     * Updates the details of an existing real estate property.
     *
     * @param realEstateWithDetails The RealEstateEntity object with updated details.
     */
    suspend fun updateRealEstate(realEstateWithDetails: RealEstateEntity) {
        withContext(ioDispatcher) {
            realEstateDao.update(realEstateWithDetails)
        }
    }

    /**
     * Deletes a photo from the database.
     *
     * @param photo The PhotoEntity object to be deleted.
     */
    suspend fun deletePhoto(photo: PhotoEntity) {
        withContext(ioDispatcher) {
            photoDao.delete(photo.idPhoto)
        }
    }

    /**
     * Searches for real estate properties based on various criteria.
     *
     * @param typeId The ID of the property type.
     * @param startDate The start date for the search.
     * @param endDate The end date for the search.
     * @param minSurface The minimum surface area for the search.
     * @param maxSurface The maximum surface area for the search.
     * @param minPrice The minimum price for the search.
     * @param maxPrice The maximum price for the search.
     * @param roomCount The number of rooms for the search.
     * @param bathroomCount The number of bathrooms for the search.
     * @param minPhotoCount The minimum number of photos for the search.
     * @param pOI The list of points of interest IDs for the search.
     * @param isSold Whether the property is sold.
     * @return List of RealEstateWithDetails objects matching the search criteria.
     */
    suspend fun searchRealEstates(
        typeId: Int?,
        startDate: Long?, endDate: Long?, minSurface: Int?, maxSurface: Int?,
        minPrice: Int?, maxPrice: Int?, roomCount: Int?, bathroomCount: Int?, minPhotoCount: Int?, pOI: MutableList<Int>, isSold: Boolean
    ): List<RealEstateWithDetails> {
        return withContext(ioDispatcher) {
            val query = createDynamicQuery(typeId, startDate, endDate, minSurface, maxSurface, minPrice, maxPrice, roomCount, bathroomCount, minPhotoCount, pOI, isSold)
            realEstateDao.searchRealEstates(query)
        }
    }

    /**
     * Creates a dynamic SQL query based on the provided search criteria.
     *
     * @param typeId The ID of the property type.
     * @param startDate The start date for the search.
     * @param endDate The end date for the search.
     * @param minSurface The minimum surface area for the search.
     * @param maxSurface The maximum surface area for the search.
     * @param minPrice The minimum price for the search.
     * @param maxPrice The maximum price for the search.
     * @param roomCount The number of rooms for the search.
     * @param bathroomCount The number of bathrooms for the search.
     * @param minPhotoCount The minimum number of photos for the search.
     * @param pOI The list of points of interest IDs for the search.
     * @param isSold Whether the property is sold.
     * @return SupportSQLiteQuery object representing the dynamic query.
     */
    private fun createDynamicQuery(
        typeId: Int?,
        startDate: Long?, endDate: Long?, minSurface: Int?, maxSurface: Int?,
        minPrice: Int?, maxPrice: Int?, roomCount: Int?, bathroomCount: Int?, minPhotoCount: Int?, pOI: MutableList<Int>, isSold: Boolean
    ): SupportSQLiteQuery {
        val selectionArgs = mutableListOf<Any>()
        val selection = StringBuilder("SELECT real_estate.* FROM real_estate " +
                "LEFT JOIN photo ON real_estate.idRealEstate = photo.idRealEstate " +
                "LEFT JOIN Is_next_to ON real_estate.idRealEstate = Is_next_to.idRealEstate")

        val conditions = StringBuilder()
        if (typeId != 0) {
            typeId?.let {
                if (conditions.isNotEmpty()) conditions.append(" AND ")
                conditions.append("idType = ?")
                selectionArgs.add(it)
            }
        }
        startDate?.let {
            if (conditions.isNotEmpty()) conditions.append(" AND ")
            conditions.append("creationDate >= ?")
            selectionArgs.add(it)
        }
        endDate?.let {
            if (conditions.isNotEmpty()) conditions.append(" AND ")
            conditions.append("creationDate <= ?")
            selectionArgs.add(it)
        }
        minSurface?.let {
            if (conditions.isNotEmpty()) conditions.append(" AND ")
            conditions.append("squareFeet >= ?")
            selectionArgs.add(it)
        }
        maxSurface?.let {
            if (conditions.isNotEmpty()) conditions.append(" AND ")
            conditions.append("squareFeet <= ?")
            selectionArgs.add(it)
        }
        minPrice?.let {
            if (conditions.isNotEmpty()) conditions.append(" AND ")
            conditions.append("price >= ?")
            selectionArgs.add(it)
        }
        maxPrice?.let {
            if (conditions.isNotEmpty()) conditions.append(" AND ")
            conditions.append("price <= ?")
            selectionArgs.add(it)
        }
        roomCount?.let {
            if (conditions.isNotEmpty()) conditions.append(" AND ")
            conditions.append("roomCount >= ?")
            selectionArgs.add(it)
        }
        bathroomCount?.let {
            if (conditions.isNotEmpty()) conditions.append(" AND ")
            conditions.append("bathroomCount >= ?")
            selectionArgs.add(it)
        }
        minPhotoCount?.let {
            if (conditions.isNotEmpty()) conditions.append(" AND ")
            conditions.append("(SELECT COUNT(*) FROM photo WHERE photo.idRealEstate = real_estate.idRealEstate) >= ?")
            selectionArgs.add(it)
        }
        if (isSold) {
            if (conditions.isNotEmpty()) conditions.append(" AND ")
            conditions.append("soldDate IS NOT NULL")
        }
        if (pOI.isNotEmpty()) {
            if (conditions.isNotEmpty()) conditions.append(" AND ")
            conditions.append("real_estate.idRealEstate IN (SELECT Is_next_to.idRealEstate FROM Is_next_to WHERE Is_next_to.idPoi IN (")
            for ((index, _) in pOI.withIndex()) {
                if (index > 0) {
                    conditions.append(", ")
                }
                conditions.append("?")
            }
            conditions.append(") GROUP BY Is_next_to.idRealEstate HAVING COUNT(DISTINCT Is_next_to.idPoi) = ?)")
            selectionArgs.addAll(pOI)
            selectionArgs.add(pOI.size)
        }

        if (conditions.isNotEmpty()) {
            selection.append(" WHERE ").append(conditions)
        }

        selection.append(" GROUP BY real_estate.idRealEstate")

        return SimpleSQLiteQuery(selection.toString(), selectionArgs.toTypedArray())
    }

    /**
     * Clears the points of interest associations for a given real estate property before updating.
     *
     * @param idRealEstate The ID of the real estate property.
     */
    suspend fun clearPOIsBeforeUpdate(idRealEstate: Int) {
        withContext(ioDispatcher) {
            isNextToDao.clearPOIsBeforeUpdate(idRealEstate)
        }
    }
}
