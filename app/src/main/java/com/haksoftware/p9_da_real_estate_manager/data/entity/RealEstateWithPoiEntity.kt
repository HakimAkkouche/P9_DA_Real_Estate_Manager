package com.haksoftware.p9_da_real_estate_manager.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RealEstateWithPoiEntity(
    @Embedded
    var realEstateEntity: RealEstateEntity,

    @Relation(
        parentColumn = "idRealEstate",
        entity = PointOfInterestEntity::class,
        entityColumn = "idPoi",
        associateBy = Junction(IsNextToEntity::class)
    )
    var poi: List<PointOfInterestEntity>
)
