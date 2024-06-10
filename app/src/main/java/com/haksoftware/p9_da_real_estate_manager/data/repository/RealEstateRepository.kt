package com.haksoftware.p9_da_real_estate_manager.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
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

    suspend fun searchRealEstates(
        startDate: Long?, endDate: Long?, minSurface: Int?, maxSurface: Int?,
        minPrice: Int?, maxPrice: Int?, roomCount: Int?, bathroomCount: Int?, pOI: MutableList<Int>
    ): List<RealEstateWithDetails> {
        return withContext(ioDispatcher) {
            val query = createDynamicQuery(startDate, endDate, minSurface, maxSurface, minPrice, maxPrice, roomCount, bathroomCount)

            database.realEstateDao().searchRealEstates(query)
        }
    }
    fun createDynamicQuery(
        startDate: Long?, endDate: Long?, minSurface: Int?, maxSurface: Int?,
        minPrice: Int?, maxPrice: Int?, roomCount: Int?, bathroomCount: Int?
    ): SupportSQLiteQuery {
        val queryBuilder = SupportSQLiteQueryBuilder.builder("real_estate")

        val selectionArgs = mutableListOf<Any>()
        val selection = StringBuilder()

        startDate?.let {
            selection.append("creationDate >= ?")
            selectionArgs.add(it)
        }
        endDate?.let {
            if (selection.isNotEmpty()) selection.append(" AND ")
            selection.append("creationDate <= ?")
            selectionArgs.add(it)
        }
        minSurface?.let {
            if (selection.isNotEmpty()) selection.append(" AND ")
            selection.append("squareFeet >= ?")
            selectionArgs.add(it)
        }
        maxSurface?.let {
            if (selection.isNotEmpty()) selection.append(" AND ")
            selection.append("squareFeet <= ?")
            selectionArgs.add(it)
        }
        minPrice?.let {
            if (selection.isNotEmpty()) selection.append(" AND ")
            selection.append("price >= ?")
            selectionArgs.add(it)
        }
        maxPrice?.let {
            if (selection.isNotEmpty()) selection.append(" AND ")
            selection.append("price <= ?")
            selectionArgs.add(it)
        }
        roomCount?.let {
            if (selection.isNotEmpty()) selection.append(" AND ")
            selection.append("roomCount >= ?")
            selectionArgs.add(it)
        }
        bathroomCount?.let {
            if (selection.isNotEmpty()) selection.append(" AND ")
            selection.append("bathroomCount >= ?")
            selectionArgs.add(it)
        }

        queryBuilder.selection(selection.toString(), selectionArgs.toTypedArray())
        return queryBuilder.create()
    }

    suspend fun clearPOIsBeforeUpdate(idRealEstate: Int) {
        withContext(ioDispatcher) {
            database.isNextToDao().clearPOIsBeforeUpdate(idRealEstate)
        }
    }


}