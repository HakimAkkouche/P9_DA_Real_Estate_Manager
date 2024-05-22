package com.haksoftware.p9_da_real_estate_manager.data.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails

@Dao
interface RealEstateDao {
    @Transaction
    @Query("select * from real_estate")
    fun getRealEstatesWithDetails(): LiveData<List<RealEstateWithDetails>>
    @Insert
    fun insert(realEstate: RealEstateEntity): Long
    @Update
    fun update(realEstate: RealEstateEntity) : Int
    @Query("DELETE FROM real_estate where idRealtor = :realEstate")
    fun delete(realEstate: Int): Int
    @Query("select * from real_estate")
    fun getAllRealEstateCursor(): Cursor
}