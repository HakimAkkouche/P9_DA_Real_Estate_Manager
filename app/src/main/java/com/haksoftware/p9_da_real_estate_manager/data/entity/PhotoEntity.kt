package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Data class representing a photo entity associated with a real estate.
 * This class is used to store information about photos related to real estates in the database.
 *
 * @property idPhoto The unique ID of the photo. This is auto-generated.
 * @property namePhoto The name or path of the photo.
 * @property descriptionPhoto The description of the photo.
 * @property idRealEstate The ID of the real estate to which this photo belongs.
 */
@Entity(
    tableName = "photo",
    foreignKeys = [
        ForeignKey(
            entity = RealEstateEntity::class,
            parentColumns = ["idRealEstate"],
            childColumns = ["idRealEstate"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idRealEstate"])]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    var idPhoto: Int,
    var namePhoto: String,
    var descriptionPhoto: String?,
    var idRealEstate: Int
) : Parcelable {

    // Parcelable constructor
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readInt()
    )

    // Writes the object to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idPhoto)
        parcel.writeString(namePhoto)
        parcel.writeString(descriptionPhoto)
        parcel.writeInt(idRealEstate)
    }

    // Describes the contents for the Parcelable interface
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhotoEntity> {
        // Creates an instance of PhotoEntity from a Parcel
        override fun createFromParcel(parcel: Parcel): PhotoEntity {
            return PhotoEntity(parcel)
        }

        // Creates a new array of PhotoEntity
        override fun newArray(size: Int): Array<PhotoEntity?> {
            return arrayOfNulls(size)
        }
    }
}
