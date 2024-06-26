package com.haksoftware.p9_da_real_estate_manager.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Parcelable data class representing a detailed view of a real estate entity.
 * Combines data from multiple entities related to a real estate into a single object.
 *
 * @property realEstate The RealEstateEntity representing the basic details of the real estate.
 * @property realtor The RealtorEntity associated with the real estate.
 * @property type The TypeEntity defining the type of real estate.
 * @property photos A list of PhotoEntity objects associated with the real estate.
 * @property isNextToEntities A list of IsNextToEntity objects linking the real estate to points of interest.
 * @property pointsOfInterest A list of PointOfInterestEntity objects near the real estate.
 */
@Suppress("DEPRECATION")
data class RealEstateWithDetails(
    @Embedded val realEstate: RealEstateEntity,
    @Relation(
        parentColumn = "idRealtor",
        entityColumn = "idRealtor"
    )
    val realtor: RealtorEntity,
    @Relation(
        parentColumn = "idType",
        entityColumn = "idType"
    )
    val type: TypeEntity,
    @Relation(
        parentColumn = "idRealEstate",
        entityColumn = "idRealEstate"
    )
    val photos: MutableList<PhotoEntity>,
    @Relation(
        parentColumn = "idRealEstate",
        entityColumn = "idRealEstate"
    )
    val isNextToEntities: MutableList<IsNextToEntity>,
    @Relation(
        entity = PointOfInterestEntity::class,
        parentColumn = "idRealEstate",
        entityColumn = "idPoi",
        associateBy = Junction(IsNextToEntity::class)
    )
    val pointsOfInterest: MutableList<PointOfInterestEntity>
) : Parcelable {

    // Parcelable constructor
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(RealEstateEntity::class.java.classLoader)!!,
        parcel.readParcelable(RealtorEntity::class.java.classLoader)!!,
        parcel.readParcelable(TypeEntity::class.java.classLoader)!!,
        parcel.createTypedArrayList(PhotoEntity.CREATOR)!!,
        parcel.createTypedArrayList(IsNextToEntity.CREATOR)!!,
        parcel.createTypedArrayList(PointOfInterestEntity.CREATOR)!!
    )

    // Writes the object to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(realEstate, flags)
        parcel.writeParcelable(realtor, flags)
        parcel.writeParcelable(type, flags)
        parcel.writeTypedList(photos)
        parcel.writeTypedList(isNextToEntities)
        parcel.writeTypedList(pointsOfInterest)
    }

    // Describes the contents for the Parcelable interface
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RealEstateWithDetails> {
        // Creates an instance of RealEstateWithDetails from a Parcel
        override fun createFromParcel(parcel: Parcel): RealEstateWithDetails {
            return RealEstateWithDetails(parcel)
        }

        // Creates a new array of RealEstateWithDetails
        override fun newArray(size: Int): Array<RealEstateWithDetails?> {
            return arrayOfNulls(size)
        }
    }
}
