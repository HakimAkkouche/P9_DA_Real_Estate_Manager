package com.haksoftware.p9_da_real_estate_manager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.haksoftware.p9_da_real_estate_manager.data.dao.RealtorDao
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PointOfInterestEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEstateEntity

@Database(
    entities = [PhotoEntity::class,
        PointOfInterestEntity::class,
        RealEstateEntity::class,
        RealtorEntity::class,
        TypeEstateEntity::class],
    version = 1,
    exportSchema = false)
abstract class RealEstateManagerDatabase : RoomDatabase() {
    abstract fun realEstateDao() : RealtorDao
    companion object
    {
        @Volatile
        private var INSTANCE: RealEstateManagerDatabase? = null

        fun getDatabase(context: Context): RealEstateManagerDatabase
        {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RealEstateManagerDatabase::class.java,
                    "real_estate_manager"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}