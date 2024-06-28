package com.haksoftware.p9_da_real_estate_manager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.haksoftware.p9_da_real_estate_manager.data.entity.IsNextToEntity

/**
 * Data Access Object (DAO) interface for managing IsNextToEntity interactions with the database.
 * Provides methods to perform CRUD operations and queries related to the IsNextToEntity table.
 */
@Dao
interface IsNextToDao {
    /**
     * Inserts or replaces a list of IsNextToEntity instances into the database.
     *
     * @param isNextToEntity The list of IsNextToEntity instances to insert or replace.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIsNextTo(isNextToEntity: List<IsNextToEntity>)

    /**
     * Retrieves all IsNextToEntity relations from the database.
     *
     * @return A list of all IsNextToEntity instances stored in the database.
     */
    @Query("SELECT * FROM Is_next_to")
    suspend fun getAllIsNextToRelations(): List<IsNextToEntity>

    /**
     * Retrieves IsNextToEntity relations associated with a specific real estate ID from the database.
     *
     * @param realEstateId The ID of the real estate to retrieve IsNextToEntity relations for.
     * @return A list of IsNextToEntity instances associated with the specified real estate ID.
     */
    @Query("SELECT * FROM Is_next_to WHERE idRealEstate = :realEstateId")
    suspend fun getIsNextToRelationsByRealEstateId(realEstateId: String): List<IsNextToEntity>

    /**
     * Retrieves IsNextToEntity relations associated with a specific point of interest (POI) ID from the database.
     *
     * @param poiId The ID of the point of interest (POI) to retrieve IsNextToEntity relations for.
     * @return A list of IsNextToEntity instances associated with the specified POI ID.
     */
    @Query("SELECT * FROM Is_next_to WHERE IdPOI = :poiId")
    suspend fun getIsNextToRelationsByPoiId(poiId: String): List<IsNextToEntity>

    /**
     * Deletes IsNextToEntity relations associated with a specific real estate ID from the database.
     *
     * @param idRealEstate The ID of the real estate to delete IsNextToEntity relations for.
     * @return The number of IsNextToEntity relations deleted.
     */
    @Query("DELETE FROM Is_next_to where idRealEstate = :idRealEstate")
    fun clearPOIsBeforeUpdate(idRealEstate: Int): Int
}
