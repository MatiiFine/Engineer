package com.example.engenieer.buildings

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

object Building {

    val ITEMS: MutableList<BuildingItem> = ArrayList()

    fun addItem(item: BuildingItem) {
        ITEMS.add(item)
    }

    fun clearItems(){
        ITEMS.clear()
    }
}
data class BuildingItem(val buildingID: String,
                        val name: String,
                        val description: String,
                        val shortDescription: String,
                        val photo: String ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun toString(): String = buildingID
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(buildingID)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(shortDescription)
        parcel.writeString(photo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BuildingItem> {
        override fun createFromParcel(parcel: Parcel): BuildingItem {
            return BuildingItem(parcel)
        }

        override fun newArray(size: Int): Array<BuildingItem?> {
            return arrayOfNulls(size)
        }
    }
}