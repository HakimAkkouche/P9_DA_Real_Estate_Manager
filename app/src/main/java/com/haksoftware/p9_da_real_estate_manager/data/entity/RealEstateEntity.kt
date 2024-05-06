package com.haksoftware.p9_da_real_estate_manager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "real_estate", foreignKeys = [
    ForeignKey(entity = TypeEstateEntity::class, parentColumns = ["idType"], childColumns = ["idType"]),
    ForeignKey(entity = RealtorEntity::class, parentColumns = ["idRealtor"], childColumns = ["idRealtor"])
])
data class RealEstateEntity (
    @PrimaryKey(autoGenerate = true) var idRealEstate: Int,
    var price: Int,
    var squareFeet: Int,
    var roomCount: Int,
    var descriptionRealEstate: String,
    var address: String,
    var dateCreation: Date,
    var isSold: Boolean,
    var soldDate: Date,
    var idType: Int,
    var idRealtor: Int
) {
}