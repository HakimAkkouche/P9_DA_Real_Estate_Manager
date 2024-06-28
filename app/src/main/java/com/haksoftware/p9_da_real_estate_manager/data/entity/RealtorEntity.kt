package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Parcelable data class representing a realtor entity.
 * Stores information about a realtor including their title, last name, first name, email, and phone number.
 *
 * @property idRealtor The unique identifier of the realtor.
 * @property title The title of the realtor (e.g., Mr, Ms, Dr).
 * @property lastname The last name of the realtor.
 * @property firstname The first name of the realtor.
 * @property email The email address of the realtor.
 * @property phoneNumber The phone number of the realtor.
 */
@Entity(tableName = "realtor")
data class RealtorEntity(
    @PrimaryKey(autoGenerate = true) var idRealtor: Int,
    var title: String,
    var lastname: String,
    var firstname: String,
    var email: String,
    var phoneNumber: String
): Parcelable {
    // Parcelable constructor
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    // Writes the object to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idRealtor)
        parcel.writeString(title)
        parcel.writeString(lastname)
        parcel.writeString(firstname)
        parcel.writeString(email)
        parcel.writeString(phoneNumber)
    }

    // Describes the contents for the Parcelable interface
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RealtorEntity> {
        // Creates an instance of RealtorEntity from a Parcel
        override fun createFromParcel(parcel: Parcel): RealtorEntity {
            return RealtorEntity(parcel)
        }

        // Creates a new array of RealtorEntity
        override fun newArray(size: Int): Array<RealtorEntity?> {
            return arrayOfNulls(size)
        }
    }
}
