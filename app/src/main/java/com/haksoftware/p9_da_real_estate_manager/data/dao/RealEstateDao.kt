package com.haksoftware.p9_da_real_estate_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithPoiEntity

@Dao
interface RealEstateDao {
    @Transaction
    @Query("select * from real_estate")
    fun getRealEstateWithPoi(): LiveData<List<RealEstateWithPoiEntity>>
    @Insert
    fun insert(realEstate: RealEstateEntity)
    @Update
    fun update(realEstate: RealEstateEntity) : Int
    @Query("DELETE FROM real_estate where idRealtor = :realEstate")
    fun delete(realEstate: Int): Int
}