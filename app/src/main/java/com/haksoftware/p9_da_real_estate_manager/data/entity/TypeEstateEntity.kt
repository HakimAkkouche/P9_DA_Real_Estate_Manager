package com.haksoftware.p9_da_real_estate_manager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "type_estate")
data class TypeEstateEntity (
    @PrimaryKey(autoGenerate = true) var idType: Int,
    var nameType: String
){
}