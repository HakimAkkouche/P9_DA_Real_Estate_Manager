package com.haksoftware.p9_da_real_estate_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

interface PointOfInterestDao {
    @Transaction
    @Query("SELECT * FROM point_of_interest")
    fun getPointsOfInterest(): LiveData<List<PointOfInterestDao>>
    @Insert
    fun insert(pointOfInterestDao : PointOfInterestDao)
    @Update
    fun update(pointOfInterestDao : PointOfInterestDao) : Int
    @Query("DELETE FROM point_of_interest where idPoi = :pointOfInterest")
    fun delete(pointOfInterest : Int): Int
}