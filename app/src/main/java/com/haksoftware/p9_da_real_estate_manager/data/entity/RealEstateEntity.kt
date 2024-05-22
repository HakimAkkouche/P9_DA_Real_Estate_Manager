package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    var creationDate: String,
    var soldDate: String?,
    var idRealtor: Int,
    var idType: Int
): Parcelable {
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
        parcel.readString()!!,
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

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
        parcel.writeString(creationDate)
        parcel.writeString(soldDate)
        parcel.writeInt(idRealtor)
        parcel.writeInt(idType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RealEstateEntity> {
        override fun createFromParcel(parcel: Parcel): RealEstateEntity {
            return RealEstateEntity(parcel)
        }

        override fun newArray(size: Int): Array<RealEstateEntity?> {
            return arrayOfNulls(size)
        }
    }
}