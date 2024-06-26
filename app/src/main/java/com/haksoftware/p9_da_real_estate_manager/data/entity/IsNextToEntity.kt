package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
/**
 * Data class representing the relationship between Real Estate and Points of Interest (POI).
 * This class is used to map the many-to-many relationship between real estates and their nearby points of interest.
 *
 * @property idRealEstate The ID of the real estate.
 * @property idPoi The ID of the point of interest.
 */
@Entity(
    tableName = "Is_next_to",
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
    val idRealEstate: Int,
    val idPoi: Int
) : Parcelable {

    // Parcelable constructor
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    )

    // Writes the object to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idRealEstate)
        parcel.writeInt(idPoi)
    }

    // Describes the contents for the Parcelable interface
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IsNextToEntity> {
        // Creates an instance of IsNextToEntity from a Parcel
        override fun createFromParcel(parcel: Parcel): IsNextToEntity {
            return IsNextToEntity(parcel)
        }

        // Creates a new array of IsNextToEntity
        override fun newArray(size: Int): Array<IsNextToEntity?> {
            return arrayOfNulls(size)
        }
    }
}
