package com.haksoftware.p9_da_real_estate_manager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.haksoftware.p9_da_real_estate_manager.data.RealEstateManagerDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock


@RunWith(JUnit4::class)
class PointOfInterestDaoTestClass {

    private lateinit var database: RealEstateManagerDatabase

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {


        database = Room.inMemoryDatabaseBuilder(
            mock(Context::class.java),
            RealEstateManagerDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun getPointOfInterest() {
        val pointOfInterestEntity = database.pointOfInterestDao().getPointsOfInterest()
        val listPoi = pointOfInterestEntity.value
        assert(!listPoi.isNullOrEmpty() && listPoi[0].namePoi == "Kindergarten")
    }
}