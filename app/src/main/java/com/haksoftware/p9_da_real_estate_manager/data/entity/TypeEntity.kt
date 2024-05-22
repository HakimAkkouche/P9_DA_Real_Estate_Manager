package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "type")
data class TypeEntity (
    @PrimaryKey(autoGenerate = true) var idType: Int,
    var nameType: String
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idType)
        parcel.writeString(nameType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TypeEntity> {
        override fun createFromParcel(parcel: Parcel): TypeEntity {
            return TypeEntity(parcel)
        }

        override fun newArray(size: Int): Array<TypeEntity?> {
            return arrayOfNulls(size)
        }
    }
}