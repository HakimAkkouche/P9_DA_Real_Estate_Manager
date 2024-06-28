package com.haksoftware.p9_da_real_estate_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.haksoftware.p9_da_real_estate_manager.data.entity.PointOfInterestEntity

@Dao
interface PointOfInterestDao {
    @Transaction
    @Query("SELECT * FROM point_of_interest")
    fun getPointsOfInterest(): LiveData<List<PointOfInterestEntity>>
    @Insert
    fun insert(pointOfInterest : PointOfInterestEntity)
    @Update
    fun update(pointOfInterest : PointOfInterestEntity) : Int
    @Query("DELETE FROM point_of_interest where idPoi = :pointOfInterest")
    fun delete(pointOfInterest : Int): Int
}