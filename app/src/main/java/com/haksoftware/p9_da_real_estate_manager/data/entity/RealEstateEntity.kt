package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Data class representing a real estate entity.
 * This class is used to store information about real estates in the database.
 *
 * @property idRealEstate The unique ID of the real estate. This is auto-generated.
 * @property price The price of the real estate.
 * @property squareFeet The size of the real estate in square feet.
 * @property roomCount The number of rooms in the real estate.
 * @property bathroomCount The number of bathrooms in the real estate.
 * @property descriptionRealEstate The description of the real estate.
 * @property address The address of the real estate.
 * @property postalCode The postal code of the real estate.
 * @property city The city where the real estate is located.
 * @property state The state where the real estate is located.
 * @property creationDate The creation date of the real estate listing (in epoch time).
 * @property soldDate The date when the real estate was sold (in epoch time), if applicable.
 * @property idRealtor The ID of the realtor associated with the real estate.
 * @property idType The ID of the type of real estate.
 */
@Entity(
    tableName = "real_estate",
    foreignKeys = [
        ForeignKey(entity = TypeEntity::class, parentColumns = ["idType"], childColumns = ["idType"]),
        ForeignKey(entity = RealtorEntity::class, parentColumns = ["idRealtor"], childColumns = ["idRealtor"])
    ],
    indices = [Index(value = ["idType"]), Index(value = ["idRealtor"])]
)
data class RealEstateEntity (
    @PrimaryKey(autoGenerate = true)
    var idRealEstate: Int,
    var price: Int,
    var squareFeet: Int,
    var roomCount: Int,
    var bathroomCount: Int,
    var descriptionRealEstate: String,
    var address: String,
    var postalCode: String,
    var city: String,
    var state: String,
    var creationDate: Long,
    var soldDate: Long?,
    var idRealtor: Int,
    var idType: Int
) : Parcelable {

    // Parcelable constructor
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt()
    )

    // Writes the object to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idRealEstate)
        parcel.writeInt(price)
        parcel.writeInt(squareFeet)
        parcel.writeInt(roomCount)
        parcel.writeInt(bathroomCount)
        parcel.writeString(descriptionRealEstate)
        parcel.writeString(address)
        parcel.writeString(postalCode)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeLong(creationDate)
        parcel.writeLong(soldDate ?: 0L)
        parcel.writeInt(idRealtor)
        parcel.writeInt(idType)
    }

    // Describes the contents for the Parcelable interface
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RealEstateEntity> {
        // Creates an instance of RealEstateEntity from a Parcel
        override fun createFromParcel(parcel: Parcel): RealEstateEntity {
            return RealEstateEntity(parcel)
        }

        // Creates a new array of RealEstateEntity
        override fun newArray(size: Int): Array<RealEstateEntity?> {
            return arrayOfNulls(size)
        }
    }
}
