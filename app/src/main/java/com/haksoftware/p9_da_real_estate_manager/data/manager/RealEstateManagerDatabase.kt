package com.haksoftware.p9_da_real_estate_manager.data.manager

import android.content.Context
import android.net.Uri
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import com.haksoftware.p9_da_real_estate_manager.utils.BuildConfigResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Abstract class representing the Real Estate Manager Database.
 * This class is responsible for providing access to the various DAOs and
 * configuring the database instance.
 */
@Database(
    entities = [IsNextToEntity::class, PhotoEntity::class, PointOfInterestEntity::class, RealEstateEntity::class, RealtorEntity::class, TypeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RealEstateManagerDatabase : RoomDatabase() {

    // Abstract methods to get DAO instances
    abstract fun photoDao(): PhotoDao
    abstract fun pointOfInterestDao(): PointOfInterestDao
    abstract fun realEstateDao(): RealEstateDao
    abstract fun realtorDao(): RealtorDao
    abstract fun typeDao(): TypeDao
    abstract fun isNextToDao(): IsNextToDao

    companion object {
        @Volatile
        private var INSTANCE: RealEstateManagerDatabase? = null
        private val buildConfigResolver = BuildConfigResolver()

        /**
         * Returns the database instance, creating it if necessary.
         * If running Android tests, an in-memory database is used.
         *
         * @param context The application context.
         * @return The database instance.
         */
        fun getDatabase(context: Context): RealEstateManagerDatabase {
            if(buildConfigResolver.isRunningAndroidTest) {
                return INSTANCE ?: synchronized(this) {
                    // Use the in-memory database configuration for tests
                    val instance = Room.inMemoryDatabaseBuilder(
                        context.applicationContext,
                        RealEstateManagerDatabase::class.java
                    )
                        .build()
                    initForTest(instance, context)
                    INSTANCE = instance
                    instance
                }
            } else {
                return INSTANCE ?: synchronized(this) {
                    // Use the regular database configuration
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        RealEstateManagerDatabase::class.java,
                        "real_estate_manager.db"
                    )
                        .createFromAsset("database/real_estate_manager.db")
                        .build()
                    INSTANCE = instance
                    instance
                }
            }
        }

        /**
         * Initializes the in-memory database with test data.
         *
         * @param database The in-memory database instance.
         * @param context The application context.
         */
        private fun initForTest(database: RealEstateManagerDatabase, context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val realtorDao = database.realtorDao()
                val realEstateDao = database.realEstateDao()
                val typeDao = database.typeDao()
                val pointOfInterestDao = database.pointOfInterestDao()
                val isNextToDao = database.isNextToDao()
                val photoDao = database.photoDao()

                val frontViewUri = Uri.parse("android.resource://${context.applicationContext.packageName}/drawable/test_photo")

                val photo = PhotoEntity(
                    idPhoto = 1,
                    namePhoto = frontViewUri.toString(),
                    descriptionPhoto = "Front view of the real estate",
                    idRealEstate = 1
                )
                val realEstateEntity = RealEstateEntity(
                    idRealEstate = 1,
                    price = 250000,
                    squareFeet = 1200,
                    roomCount = 3,
                    bathroomCount = 2,
                    descriptionRealEstate = "Beautiful spacious apartment in downtown.",
                    address = "15 rue Blomet",
                    postalCode = "75015",
                    city = "PARIS",
                    state = "FRANCE",
                    creationDate = 1716595200L,
                    soldDate = null,
                    idRealtor = 1,
                    idType = 1
                )
                val realtor = RealtorEntity(
                    idRealtor = 1,
                    title = "Mr",
                    lastname = "DOE",
                    firstname = "John",
                    email = "john.doe@example.com",
                    phoneNumber = "1234567890"
                )
                val type = TypeEntity(idType = 1, nameType = "Apartment")
                val poi = PointOfInterestEntity(idPoi = 1, namePoi = "Park")
                val isNextTo = IsNextToEntity(idRealEstate = 1, idPoi = 1)

                // Insert test data into the in-memory database
                realtorDao.insert(realtor)
                typeDao.insertType(type)
                pointOfInterestDao.insert(poi)
                realEstateDao.insert(realEstateEntity)
                isNextToDao.insertIsNextTo(listOf(isNextTo))
                photoDao.insertPhotos(mutableListOf(photo))
            }
        }
    }
}
