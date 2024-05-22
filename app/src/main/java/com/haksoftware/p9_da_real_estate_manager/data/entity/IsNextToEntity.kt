package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "Is_next_to",
    primaryKeys = ["idRealEstate", "idPoi"],
    foreignKeys = [

        ForeignKey(
            entity = PointOfInterestEntity::class,
            parentColumns = ["idPoi"],
            childColumns = ["idPoi"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RealEstateEntity::class,
            parentColumns = ["idRealEstate"],
            childColumns = ["idRealEstate"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idPoi"])]
)
data class IsNextToEntity(
    val idRealEstate:Int,
    val idPoi: Int
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idRealEstate)
        parcel.writeInt(idPoi)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IsNextToEntity> {
        override fun createFromParcel(parcel: Parcel): IsNextToEntity {
            return IsNextToEntity(parcel)
        }

        override fun newArray(size: Int): Array<IsNextToEntity?> {
            return arrayOfNulls(size)
        }
    }

}