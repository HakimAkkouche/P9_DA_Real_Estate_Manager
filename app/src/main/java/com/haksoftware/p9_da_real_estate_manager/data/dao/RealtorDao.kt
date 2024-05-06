package com.haksoftware.p9_da_real_estate_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealtorEntity

@Dao
interface RealtorDao {
    @Transaction
    @Query("select * from realtor")
    fun getPointOfInterest(): LiveData<List<RealtorEntity>>
    @Insert
    fun insert(realtor: RealtorEntity)
    @Update
    fun update(realtor: RealtorEntity) : Int
    @Query("DELETE FROM realtor where idRealtor = :realtor")
    fun delete(realtor: Int): Int
}
