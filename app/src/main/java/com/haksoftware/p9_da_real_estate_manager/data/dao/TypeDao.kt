package com.haksoftware.p9_da_real_estate_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEstateEntity

@Dao
interface TypeDao {
    @Transaction
    @Query("select * from type_estate")
    fun getPointOfInterest(): LiveData<List<TypeEstateEntity>>
    @Insert
    fun insertType(type: TypeEstateEntity)
    @Update
    fun update(type: TypeEstateEntity) : Int
    @Query("DELETE FROM type_estate where idType = :type")
    fun delete(type: Int): Int
}