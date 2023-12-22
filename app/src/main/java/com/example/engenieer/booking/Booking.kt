package com.example.engenieer.booking

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

object Booking {

    val ITEMS: MutableList<BookingItem> = ArrayList()

    fun addItem(item: BookingItem) {
        ITEMS.add(item)
    }

    fun clear(){
        ITEMS.clear()
    }
}

data class BookingItem(val bookingID: String, val ownerID: String, val roomID: String, val roomName: String,
                   val equipment: String, val date: String, val startHour: String, val endHour: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun toString(): String = bookingID
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookingID)
        parcel.writeString(ownerID)
        parcel.writeString(roomID)
        parcel.writeString(roomName)
        parcel.writeString(equipment)
        parcel.writeString(date)
        parcel.writeString(startHour)
        parcel.writeString(endHour)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookingItem> {
        override fun createFromParcel(parcel: Parcel): BookingItem {
            return BookingItem(parcel)
        }

        override fun newArray(size: Int): Array<BookingItem?> {
            return arrayOfNulls(size)
        }
    }
}