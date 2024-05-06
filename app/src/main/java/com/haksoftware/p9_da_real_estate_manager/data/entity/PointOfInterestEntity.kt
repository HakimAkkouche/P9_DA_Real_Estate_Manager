package com.haksoftware.p9_da_real_estate_manager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "point_of_interest")
data class PointOfInterestEntity(
    @PrimaryKey(autoGenerate = true) var idPoi: Int,
    var namePoi: String
) {
}