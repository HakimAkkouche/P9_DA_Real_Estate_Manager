package com.haksoftware.p9_da_real_estate_manager.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.haksoftware.p9_da_real_estate_manager.data.RealEstateManagerDatabase
import com.haksoftware.p9_da_real_estate_manager.data.entity.IsNextToEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PointOfInterestEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RealEstateRepository(context: Context, private val ioDispatcher: CoroutineDispatcher) {

    private val database = RealEstateManagerDatabase.getDatabase(context)
    companion object {
        @Volatile
        private var instance: RealEstateRepository? = null

        fun getInstance(context: Context, ioDispatcher: CoroutineDispatcher): RealEstateRepository {
            return instance ?: synchronized(this) {
                instance ?: RealEstateRepository(context, ioDispatcher).also { instance = it }
            }
        }
    }
    suspend fun getRealEstate(realEstateId: Int): RealEstateWithDetails {
        return withContext(ioDispatcher) { database.realEstateDao().getRealEstateWithDetails(realEstateId)}

    }
    fun getAllRealEstates(): LiveData<List<RealEstateWithDetails>> {
           return database.realEstateDao().getAllRealEstatesWithDetails()
    }
    fun getAllPointOfInterest(): LiveData<List<PointOfInterestEntity>> {
        return database.pointOfInterestDao().getPointsOfInterest()
    }
    fun getAllRealtor(): LiveData<List<RealtorEntity>> {
        return database.realtorDao().getAllRealtor()
    }
    fun getAllTypes(): LiveData<List<TypeEntity>> {
        return database.typeDao().getAllTypes()
    }
    suspend fun insertRealEstate(realEstateEntity: RealEstateEntity): Long {
        return withContext(ioDispatcher) {
            database.realEstateDao().insert(realEstateEntity)

        }
    }
    suspend fun insertPhotos(photos: List<PhotoEntity>) {
        withContext(ioDispatcher) {
            database.photoDao().insertPhotos(photos)
        }
    }

    suspend fun insertIsNextToEntities(isNextToEntities: List<IsNextToEntity>) {
        withContext(ioDispatcher) {
            try {
                database.isNextToDao().insertIsNextTo(isNextToEntities)
            } catch (e: Exception) {
                Log.println(Log.ERROR, "ERROR Delete", e.message + " " + e.stackTraceToString())
            }
        }
    }

    suspend fun updateRealEstate(realEstateWithDetails: RealEstateEntity) {
        withContext(ioDispatcher) {
            database.realEstateDao().update(realEstateWithDetails)
        }
    }

    suspend fun deletePhoto(photo: PhotoEntity) {
        withContext(ioDispatcher) {
            database.photoDao().delete(photo.idPhoto)
        }
    }

    suspend fun searchRealEstates(typeId: Int?,
        startDate: Long?, endDate: Long?, minSurface: Int?, maxSurface: Int?,
        minPrice: Int?, maxPrice: Int?, roomCount: Int?, bathroomCount: Int?, minPhotoCount: Int?, pOI: MutableList<Int>, isSold: Boolean
    ): List<RealEstateWithDetails> {
        return withContext(ioDispatcher) {
            val query = createDynamicQuery(typeId, startDate, endDate, minSurface, maxSurface, minPrice, maxPrice, roomCount, bathroomCount, minPhotoCount, pOI, isSold)

            database.realEstateDao().searchRealEstates(query)
        }
    }
    fun createDynamicQuery(typeId: Int?,
                           startDate: Long?, endDate: Long?, minSurface: Int?, maxSurface: Int?,
                           minPrice: Int?, maxPrice: Int?, roomCount: Int?, bathroomCount: Int?, minPhotoCount: Int?, pOI: MutableList<Int>, isSold: Boolean
    ): SupportSQLiteQuery {
        val selectionArgs = mutableListOf<Any>()
        val selection = StringBuilder("SELECT real_estate.* FROM real_estate " +
                "LEFT JOIN photo ON real_estate.idRealEstate = photo.idRealEstate " +
                "LEFT JOIN Is_next_to ON real_estate.idRealEstate = Is_next_to.idRealEstate")

        val conditions = StringBuilder()
        if(typeId != 0) {
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


    suspend fun clearPOIsBeforeUpdate(idRealEstate: Int) {
        withContext(ioDispatcher) {
            database.isNextToDao().clearPOIsBeforeUpdate(idRealEstate)
        }
    }


}