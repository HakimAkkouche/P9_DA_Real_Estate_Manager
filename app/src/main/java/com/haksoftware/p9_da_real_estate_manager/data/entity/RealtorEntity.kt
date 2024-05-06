package com.haksoftware.p9_da_real_estate_manager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "realtor")
data class RealtorEntity(
    @PrimaryKey(autoGenerate = true) var idRealtor: Int,
    var title: String,
    var lastnameRealtor: String,
    var firstnameRealtor: String,
    var email: String,
    var phoneNumber: String
) {
}