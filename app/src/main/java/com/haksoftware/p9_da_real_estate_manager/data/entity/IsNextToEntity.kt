package com.haksoftware.p9_da_real_estate_manager.data.entity

import androidx.room.Entity

@Entity(tableName = "is_next_to", primaryKeys = ["idRealEstate", "idPoi"])
data class IsNextToEntity(
    val idType:Int,
    val idPoi: Int
) {

}