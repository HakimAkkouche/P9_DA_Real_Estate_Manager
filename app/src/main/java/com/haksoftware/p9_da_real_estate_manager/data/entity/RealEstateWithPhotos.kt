package com.haksoftware.p9_da_real_estate_manager.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class RealEstateWithPhotos(
    @Embedded
    val RealEstate: RealEstateEntity,
    @Relation(
        parentColumn = "idRealEstate",
        entityColumn = "idRealEstate"
    )
    val realEstates: List<RealEstateEntity>
)
