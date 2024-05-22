package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "realtor")
data class RealtorEntity(
    @PrimaryKey(autoGenerate = true) var idRealtor: Int,
    var title: String,
    var lastname: String,
    var firstname: String,
    var email: String,
    var phoneNumber: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idRealtor)
        parcel.writeString(title)
        parcel.writeString(lastname)
        parcel.writeString(firstname)
        parcel.writeString(email)
        parcel.writeString(phoneNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RealtorEntity> {
        override fun createFromParcel(parcel: Parcel): RealtorEntity {
            return RealtorEntity(parcel)
        }

        override fun newArray(size: Int): Array<RealtorEntity?> {
            return arrayOfNulls(size)
        }
    }
}