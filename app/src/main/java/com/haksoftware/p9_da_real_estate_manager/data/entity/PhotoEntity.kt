package com.haksoftware.p9_da_real_estate_manager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "photo", foreignKeys = [
    ForeignKey(entity = RealEstateEntity::class, parentColumns = ["idRealEstate"], childColumns = ["idRealEstate"])
])
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) var idPhoto: Int,
    var nameType: String,
    var idRealEstate: Int

) {

}