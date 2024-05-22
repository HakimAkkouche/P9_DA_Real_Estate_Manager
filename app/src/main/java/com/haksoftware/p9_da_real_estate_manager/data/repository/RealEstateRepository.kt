package com.haksoftware.p9_da_real_estate_manager.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.haksoftware.p9_da_real_estate_manager.data.RealEstateManagerDatabase
import com.haksoftware.p9_da_real_estate_manager.data.entity.IsNextToEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PointOfInterestEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealEstateRepository(context: Context) {

    private val database = RealEstateManagerDatabase.getDatabase(context)
    companion object {
        @Volatile
        private var instance: RealEstateRepository? = null

        fun getInstance(context: Context): RealEstateRepository {
            return instance ?: synchronized(this) {
                instance ?: RealEstateRepository(context).also { instance = it }
            }
        }
    }

    fun getAllRealEstates(): LiveData<List<RealEstateWithDetails>> {
           return database.realEstateDao().getRealEstatesWithDetails()
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
        return withContext(Dispatchers.IO) {
            database.realEstateDao().insert(realEstateEntity)

        }
    }
    suspend fun insertPhotos(photos: List<PhotoEntity>) {
        withContext(Dispatchers.IO) {
            database.photoDao().insertPhotos(photos)
        }
    }

    suspend fun insertIsNextToEntities(isNextToEntities: List<IsNextToEntity>) {
        withContext(Dispatchers.IO) {
            database.isNextToDao().insertIsNextTo(isNextToEntities)
        }
    }
}