package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a point of interest entity.
 * This class is used to store information about points of interest in the database.
 *
 * @property idPoi The unique ID of the point of interest. This is auto-generated.
 * @property namePoi The name of the point of interest.
 */
@Entity(tableName = "point_of_interest")
data class PointOfInterestEntity(
    @PrimaryKey(autoGenerate = true) var idPoi: Int,
    var namePoi: String
) : Parcelable {

    // Parcelable constructor
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    // Writes the object to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idPoi)
        parcel.writeString(namePoi)
    }

    // Describes the contents for the Parcelable interface
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PointOfInterestEntity> {
        // Creates an instance of PointOfInterestEntity from a Parcel
        override fun createFromParcel(parcel: Parcel): PointOfInterestEntity {
            return PointOfInterestEntity(parcel)
        }

        // Creates a new array of PointOfInterestEntity
        override fun newArray(size: Int): Array<PointOfInterestEntity?> {
            return arrayOfNulls(size)
        }
    }
}
