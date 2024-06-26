package com.haksoftware.p9_da_real_estate_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity

/**
 * Data Access Object (DAO) interface for managing PhotoEntity interactions with the database.
 * Provides methods to perform CRUD operations and queries related to the PhotoEntity table.
 */
@Dao
interface PhotoDao {
    /**
     * Retrieves a list of photos associated with a specific real estate ID from the database.
     *
     * @param idRealEstate The ID of the real estate to retrieve photos for.
     * @return A LiveData object containing a list of PhotoEntity instances associated with the specified real estate ID.
     */
    @Transaction
    @Query("select * from photo where idRealEstate = :idRealEstate")
    fun getRealEstatePhotos(idRealEstate: Int): LiveData<List<PhotoEntity>>

    /**
     * Inserts a list of PhotoEntity instances into the database.
     *
     * @param photos The list of PhotoEntity instances to insert.
     */
    @Insert
    fun insertPhotos(photos: List<PhotoEntity>)

    /**
     * Updates a PhotoEntity instance in the database.
     *
     * @param photo The PhotoEntity instance to update.
     * @return The number of rows updated in the database (should be 1).
     */
    @Update
    fun update(photo: PhotoEntity): Int

    /**
     * Deletes a PhotoEntity instance from the database.
     *
     * @param photo The ID of the photo to delete.
     * @return The number of rows deleted in the database (should be 1).
     */
    @Query("DELETE FROM photo where idPhoto = :photo")
    fun delete(photo: Int): Int
}
