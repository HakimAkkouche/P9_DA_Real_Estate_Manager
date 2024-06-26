package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Parcelable data class representing a real estate type entity.
 * Stores information about the type of real estate, identified by its unique ID and name.
 *
 * @property idType The unique identifier of the real estate type.
 * @property nameType The name of the real estate type (e.g., Apartment, House, Condo).
 */
@Entity(tableName = "type")
data class TypeEntity(
    @PrimaryKey(autoGenerate = true) var idType: Int,
    var nameType: String
): Parcelable {
    // Parcelable constructor
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    )

    // Writes the object to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idType)
        parcel.writeString(nameType)
    }

    // Describes the contents for the Parcelable interface
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TypeEntity> {
        // Creates an instance of TypeEntity from a Parcel
        override fun createFromParcel(parcel: Parcel): TypeEntity {
            return TypeEntity(parcel)
        }

        // Creates a new array of TypeEntity
        override fun newArray(size: Int): Array<TypeEntity?> {
            return arrayOfNulls(size)
        }
    }
}
