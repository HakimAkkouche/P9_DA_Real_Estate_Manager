package com.haksoftware.p9_da_real_estate_manager.data.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateEntity
import com.haksoftware.p9_da_real_estate_manager.data.entity.RealEstateWithDetails

@Dao
interface RealEstateDao {
    @Transaction
    @Query("select * from real_estate")
    fun getAllRealEstatesWithDetails(): LiveData<List<RealEstateWithDetails>>
    @Transaction
    @Query("select * from real_estate where idRealEstate = :realEstateId")
    fun getRealEstateWithDetails(realEstateId: Int): RealEstateWithDetails
    @Insert
    fun insert(realEstate: RealEstateEntity): Long
    @Update
    fun update(realEstate: RealEstateEntity) : Int
    @Query("DELETE FROM real_estate where idRealtor = :realEstate")
    fun delete(realEstate: Int): Int
    @Query("select * from real_estate")
    fun getAllRealEstateCursor(): Cursor
    @Transaction
    @RawQuery
    fun searchRealEstates(query: SupportSQLiteQuery): List<RealEstateWithDetails>

}