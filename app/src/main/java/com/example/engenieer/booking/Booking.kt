package com.example.engenieer.booking

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.util.ArrayList

object Booking {

    val ITEMS: MutableList<BookingItem> = ArrayList()
    var currentRoomID: String = ""

    fun addItem(item: BookingItem){
        if(!checkIfExist(item))
            ITEMS.add(item)
    }

    private fun checkIfExist(item: BookingItem): Boolean {
        for (element in ITEMS){
            if(element.bookingID == item.bookingID) return true
        }
        return false
    }

    fun checkRoom(roomID: String){
        if (currentRoomID != roomID){
            currentRoomID = roomID
            ITEMS.clear()
        }
    }

    fun delete(position: Int) {
        ITEMS.removeAt(position)
        Log.i("bookingDeletion", "bookingDeleted")
    }

    fun getItem(position: Int): BookingItem{
        return ITEMS[position]
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