package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation

data class RealEstateWithDetails(
    @Embedded val realEstate: RealEstateEntity,
    @Relation(
        parentColumn = "idRealtor",
        entityColumn = "idRealtor"
    )
    val realtor: RealtorEntity,
    @Relation(
        parentColumn = "idType",
        entityColumn = "idType"
    )
    val type: TypeEntity,
    @Relation(
        parentColumn = "idRealEstate",
        entityColumn = "idRealEstate"
    )
    val photos: List<PhotoEntity>,
    @Relation(
        parentColumn = "idRealEstate",
        entityColumn = "idRealEstate"
    )
    val isNextToEntities: List<IsNextToEntity>
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(RealEstateEntity::class.java.classLoader)!!,
        parcel.readParcelable(RealtorEntity::class.java.classLoader)!!,
        parcel.readParcelable(TypeEntity::class.java.classLoader)!!,
        parcel.createTypedArrayList(PhotoEntity)!!,
        parcel.createTypedArrayList(IsNextToEntity)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(realEstate, flags)
        parcel.writeParcelable(realtor, flags)
        parcel.writeParcelable(type, flags)
        parcel.writeTypedList(photos)
        parcel.writeTypedList(isNextToEntities)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RealEstateWithDetails> {
        override fun createFromParcel(parcel: Parcel): RealEstateWithDetails {
            return RealEstateWithDetails(parcel)
        }

        override fun newArray(size: Int): Array<RealEstateWithDetails?> {
            return arrayOfNulls(size)
        }
    }
}
