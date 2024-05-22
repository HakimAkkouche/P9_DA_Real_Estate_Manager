package com.haksoftware.p9_da_real_estate_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.haksoftware.p9_da_real_estate_manager.data.entity.PhotoEntity

@Dao
interface PhotoDao {
    @Transaction
    @Query("select * from photo where idRealEstate = :idRealEstate")
    fun getRealEstatePhotos(idRealEstate: Int): LiveData<List<PhotoEntity>>
    @Insert
    fun insertPhotos(photos: List<PhotoEntity>)
    @Update
    fun update(photo: PhotoEntity) : Int
    @Query("DELETE FROM photo where idPhoto = :photo")
    fun delete(photo: Int): Int
}