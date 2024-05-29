package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "point_of_interest")
data class PointOfInterestEntity(
    @PrimaryKey(autoGenerate = true) var idPoi: Int,
    var namePoi: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idPoi)
        parcel.writeString(namePoi)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PointOfInterestEntity> {
        override fun createFromParcel(parcel: Parcel): PointOfInterestEntity {
            return PointOfInterestEntity(parcel)
        }

        override fun newArray(size: Int): Array<PointOfInterestEntity?> {
            return arrayOfNulls(size)
        }
    }

}