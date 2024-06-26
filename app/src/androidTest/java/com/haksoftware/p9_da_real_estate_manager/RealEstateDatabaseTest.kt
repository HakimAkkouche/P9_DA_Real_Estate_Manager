package com.haksoftware.p9_da_real_estate_manager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import com.haksoftware.p9_da_real_estate_manager.data.entity.IsNextToEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity

import com.haksoftware.p9_da_real_estate_manager.data.entity.PointOfInterestEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity
import com.haksoftware.p9_da_real_estate_manager.data.manager.RealEstateManagerDatabase
import com.haksoftware.p9_da_real_estate_manager.utils.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations

class RealEstateDatabaseTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RealEstateManagerDatabase

    private lateinit var realEstateEntity: RealEstateEntity
    private lateinit var realtor: RealtorEntity
    private lateinit var photo: PhotoEntity
    private var listPhoto: MutableList<PhotoEntity> = mutableListOf()
    private lateinit var type: TypeEntity
    private var listPoi: MutableList<PointOfInterestEntity> = mutableListOf()
    private var listIsNextTo: MutableList<IsNextToEntity> = mutableListOf()
    private lateinit var realEstateWithDetails: RealEstateWithDetails
    @Before
    fun initDb() {
        MockitoAnnotations.openMocks(this)
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            RealEstateManagerDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        initData()

        database.pointOfInterestDao().insert(listPoi[0])
        database.pointOfInterestDao().insert(listPoi[1])
        database.pointOfInterestDao().insert(listPoi[2])
        database.realtorDao().insert(realtor)
        database.typeDao().insertType(type)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetPointOfInterest() = runBlocking {
        val pointOfInterestEntities = database.pointOfInterestDao().getPointsOfInterest().getOrAwaitValue()
        assertTrue(pointOfInterestEntities.isNotEmpty() && pointOfInterestEntities[0].namePoi == listPoi[0].namePoi)
    }
    @Test
    fun insertAndGetRealtor() = runBlocking {

        val realtorEntities = database.realtorDao().getAllRealtor().getOrAwaitValue()
        assertTrue(realtorEntities.isNotEmpty() && realtorEntities[0].lastname == realtor.lastname)
    }
    @Test
    fun insertAndGetType() = runBlocking {
        val typeEntities = database.typeDao().getAllTypes().getOrAwaitValue()
        assertTrue(typeEntities.isNotEmpty() && typeEntities[0].nameType == type.nameType)
    }
    @Test
    fun insertAndGetRealEstate() = runBlocking {
        database.realEstateDao().insert(realEstateEntity)
        val realEstateEntities = database.realEstateDao().getAllRealEstatesWithDetails().getOrAwaitValue()
        assertTrue(realEstateEntities.isNotEmpty() && realEstateEntities[0].realEstate.descriptionRealEstate == realEstateEntity.descriptionRealEstate)
    }
    @Test
    fun insertAndGetRealEstateAndPhoto() = runBlocking {
        database.realEstateDao().insert(realEstateEntity)
        database.photoDao().insertPhotos(listPhoto)
        val realEstateEntities = database.realEstateDao().getAllRealEstatesWithDetails().getOrAwaitValue()
        assertTrue(realEstateEntities.isNotEmpty() && realEstateEntities[0].photos[0].namePhoto == listPhoto[0].namePhoto)
    }

    @Test
    fun insertPhotoAndRemove() = runBlocking {
        database.realEstateDao().insert(realEstateEntity)
        database.photoDao().insertPhotos(listPhoto)
        val realEstateEntities = database.realEstateDao().getAllRealEstatesWithDetails().getOrAwaitValue()
        assertTrue(realEstateEntities.isNotEmpty() && realEstateEntities[0].photos[0].namePhoto == listPhoto[0].namePhoto)

        database.photoDao().delete(realEstateEntities[0].photos[0].idPhoto)
        val realEstateEntities2 = database.realEstateDao().getAllRealEstatesWithDetails().getOrAwaitValue()
        assertTrue(realEstateEntities2.isNotEmpty() && realEstateEntities2[0].photos.isEmpty())
    }
    @Test
    fun insertAndGetRealEstateAndPoi() = runBlocking {
        database.realEstateDao().insert(realEstateEntity)
        database.isNextToDao().insertIsNextTo(listIsNextTo)
        val realEstateEntities = database.realEstateDao().getAllRealEstatesWithDetails().getOrAwaitValue()
        assertTrue(realEstateEntities.isNotEmpty() && realEstateEntities[0].isNextToEntities[0].idPoi == listIsNextTo[0].idPoi)
    }
    @Test
    fun insertAndUpdateRealEstateAndPoi() = runBlocking {
        database.realEstateDao().insert(realEstateEntity)
        val realEstateEntities = database.realEstateDao().getAllRealEstatesWithDetails().getOrAwaitValue()
        assertTrue(realEstateEntities.isNotEmpty() && realEstateEntities[0].realEstate.descriptionRealEstate == realEstateEntity.descriptionRealEstate)
        realEstateEntity.descriptionRealEstate = "Beautiful spacious house in downtown."
        database.realEstateDao().update(realEstateEntity)
        val realEstateEntities2 = database.realEstateDao().getAllRealEstatesWithDetails().getOrAwaitValue()
        assertTrue(realEstateEntities2.isNotEmpty() && realEstateEntities2[0].realEstate.descriptionRealEstate == realEstateEntity.descriptionRealEstate)
    }
    @Test
    fun insertAndRemoveIsNextTo() = runBlocking {
        database.realEstateDao().insert(realEstateEntity)
        database.isNextToDao().insertIsNextTo(listIsNextTo)
        val realEstateEntities = database.realEstateDao().getAllRealEstatesWithDetails().getOrAwaitValue()
        assertTrue(realEstateEntities.isNotEmpty() && realEstateEntities[0].isNextToEntities[0].idPoi == listIsNextTo[0].idPoi)

        database.isNextToDao().clearPOIsBeforeUpdate(realEstateEntity.idRealEstate)
        val realEstateEntities2 = database.realEstateDao().getAllRealEstatesWithDetails().getOrAwaitValue()
        assertTrue(realEstateEntities2.isNotEmpty() && realEstateEntities2[0].isNextToEntities.isEmpty())

        listIsNextTo.removeAt(1)
        database.isNextToDao().insertIsNextTo(listIsNextTo)

        val realEstateEntities3 = database.realEstateDao().getAllRealEstatesWithDetails().getOrAwaitValue()
        assertTrue(realEstateEntities3.isNotEmpty() && realEstateEntities3[0].isNextToEntities.size == 2
                && realEstateEntities3[0].isNextToEntities[1].idPoi == 3)

    }


    @Test
    fun testSearchRealEstatesByTypeAndSurface() = runBlocking {
        insertListRealEstate()
        val query = createDynamicQuery(
            typeId = 1,
            startDate = null,
            endDate = null,
            minSurface = 1000,
            maxSurface = null,
            minPrice = null,
            maxPrice = null,
            roomCount = null,
            bathroomCount = null,
            minPhotoCount = null,
            pOI = mutableListOf(),
            isSold = false
        )

        val result = database.realEstateDao().searchRealEstates(query)
        assertTrue(result.isNotEmpty())
        assertEquals(realEstateEntity.idType, result[0].realEstate.idType)
        assertTrue(result[0].realEstate.squareFeet >= 100)
    }

    @Test
    fun testSearchRealEstatesByPriceRange() = runBlocking {
        insertListRealEstate()
        val query = createDynamicQuery(
            typeId = null,
            startDate = null,
            endDate = null,
            minSurface = null,
            maxSurface = null,
            minPrice = 200000,
            maxPrice = 300000,
            roomCount = null,
            bathroomCount = null,
            minPhotoCount = null,
            pOI = mutableListOf(),
            isSold = false
        )

        val result = database.realEstateDao().searchRealEstates(query)
        assertTrue(result.isNotEmpty())
        assertTrue(result[0].realEstate.price in 200000..300000)
        assertTrue(result.size == 2)
    }

    @Test
    fun testSearchRealEstatesByPOI() = runBlocking {
        insertListRealEstate()
        val query = createDynamicQuery(
            typeId = null,
            startDate = null,
            endDate = null,
            minSurface = null,
            maxSurface = null,
            minPrice = null,
            maxPrice = null,
            roomCount = null,
            bathroomCount = null,
            minPhotoCount = null,
            pOI = mutableListOf(1, 2),
            isSold = false
        )

        val result = database.realEstateDao().searchRealEstates(query)
        assertTrue(result.isNotEmpty())
        val poiIds = result[0].isNextToEntities.map { it.idPoi }
        assertTrue(poiIds.containsAll(listOf(1, 2)))
        assertTrue(result.size == 2)
    }

    private fun initData() {
        photo = PhotoEntity(
            idPhoto = 1,
            namePhoto = "front_view.jpg",
            descriptionPhoto = "Front view of the real estate",
            idRealEstate = 1
        )
        realEstateEntity = RealEstateEntity(
            idRealEstate = 1,
            price = 250000,
            squareFeet = 1000,
            roomCount = 3,
            bathroomCount = 2,
            descriptionRealEstate = "Beautiful spacious apartment in downtown.",
            address = "123 Main St",
            postalCode = "12345",
            city = "Anytown",
            state = "Anystate",
            creationDate = System.currentTimeMillis(),
            soldDate = null,
            idRealtor = 1,
            idType = 1
        )
        realtor = RealtorEntity(idRealtor = 1, title = "Mr", lastname = "Doe", firstname = "John", email = "john.doe@example.com", phoneNumber = "1234567890")
        type = TypeEntity(idType = 1, nameType = "Apartment")
        listPhoto.add(PhotoEntity(idPhoto = 1, namePhoto = "front_view.jpg", descriptionPhoto = "Front view of the real estate", idRealEstate = 1))
        val poi1 = PointOfInterestEntity(idPoi = 1, namePoi = "Park")
        val poi2 = PointOfInterestEntity(idPoi = 2, namePoi = "School")
        val poi3 = PointOfInterestEntity(idPoi = 3, namePoi = "Restaurant")

        listPoi.add(poi1)
        listPoi.add(poi2)
        listPoi.add(poi3)
        for (poi: PointOfInterestEntity in listPoi) {
            listIsNextTo.add(IsNextToEntity(1, poi.idPoi))
        }
    }
    fun insertListRealEstate() = runBlocking{
        database.realEstateDao().insert(realEstateEntity)
        database.realEstateDao().insert(realEstateEntity.copy(idRealEstate = 2, squareFeet = 800))
        database.realEstateDao().insert(realEstateEntity.copy(idRealEstate = 3,price = 150000))
        database.isNextToDao().insertIsNextTo(mutableListOf(IsNextToEntity(1,1),IsNextToEntity(1,2)))
        database.isNextToDao().insertIsNextTo(mutableListOf(IsNextToEntity(2,1),IsNextToEntity(2,2)))
        database.isNextToDao().insertIsNextTo(mutableListOf(IsNextToEntity(3,2)))
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
}
