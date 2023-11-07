package com.example.engenieer.rooms

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

object Room {
    val ITEMS: MutableList<RoomItem> = ArrayList()

    private fun addItem(item: RoomItem) {
        ITEMS.add(item)
    }
}

data class RoomItem(val id: String, val name: String, val description: String, val shortDescription: String, val photo: String, val buildingID: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun toString(): String = name
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(photo)
        parcel.writeString(buildingID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomItem> {
        override fun createFromParcel(parcel: Parcel): RoomItem {
            return RoomItem(parcel)
        }

        override fun newArray(size: Int): Array<RoomItem?> {
            return arrayOfNulls(size)
        }
    }
}