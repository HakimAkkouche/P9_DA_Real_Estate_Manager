package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "photo",
    foreignKeys = [
        ForeignKey(
            entity = RealEstateEntity::class,
            parentColumns = ["idRealEstate"],
            childColumns = ["idRealEstate"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index(value = ["idRealEstate"])]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    var idPhoto: Int,
    var namePhoto: String,
    var descriptionPhoto: String?,
    var idRealEstate: Int

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idPhoto)
        parcel.writeString(namePhoto)
        parcel.writeString(descriptionPhoto)
        parcel.writeInt(idRealEstate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhotoEntity> {
        override fun createFromParcel(parcel: Parcel): PhotoEntity {
            return PhotoEntity(parcel)
        }

        override fun newArray(size: Int): Array<PhotoEntity?> {
            return arrayOfNulls(size)
        }
    }

}