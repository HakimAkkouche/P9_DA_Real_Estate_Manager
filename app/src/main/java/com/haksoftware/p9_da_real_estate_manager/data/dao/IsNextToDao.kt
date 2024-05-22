package com.haksoftware.p9_da_real_estate_manager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.haksoftware.p9_da_real_estate_manager.data.entity.IsNextToEntity
@Dao
interface IsNextToDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIsNextTo(isNextToEntity: List<IsNextToEntity>)

    @Query("SELECT * FROM Is_next_to")
    suspend fun getAllIsNextToRelations(): List<IsNextToEntity>

    @Query("SELECT * FROM Is_next_to WHERE idRealEstate = :realEstateId")
    suspend fun getIsNextToRelationsByRealEstateId(realEstateId: String): List<IsNextToEntity>

    @Query("SELECT * FROM Is_next_to WHERE IdPOI = :poiId")
    suspend fun getIsNextToRelationsByPoiId(poiId: String): List<IsNextToEntity>
}