package com.haksoftware.p9_da_real_estate_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.haksoftware.p9_da_real_estate_manager.data.entity.TypeEntity

@Dao
interface TypeDao {
    @Transaction
    @Query("select * from type")
    fun getAllTypes(): LiveData<List<TypeEntity>>
    @Insert
    fun insertType(type: TypeEntity)
    @Update
    fun update(type: TypeEntity) : Int
    @Query("DELETE FROM type where idType = :type")
    fun delete(type: Int): Int
}