package com.example.engenieer.rooms

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.engenieer.buildings.Building
import com.example.engenieer.helper.FirebaseHandler
import java.util.ArrayList

object Room {
    val ITEMS: MutableList<RoomItem> = ArrayList()
    val PHOTOS: ArrayList<Bitmap> = ArrayList()
    val DOWNLOAD: MutableList<Pair<String, Boolean>> = ArrayList()
    private var currentBuildingID: String = ""

    fun addItem(item: RoomItem): Boolean {
        var found: Boolean = false
        for(room in ITEMS)
            if(room.id == item.id)found = true
        if (!found) {
            ITEMS.add(item)
            DOWNLOAD.add(Pair(item.id,false))
        }
        return !found
    }

    fun checkBuilding(buildingID: String){
        if (currentBuildingID.isEmpty()) currentBuildingID = buildingID
        else if(buildingID!= currentBuildingID){
            currentBuildingID = buildingID
            ITEMS.clear()
            PHOTOS.clear()
            DOWNLOAD.clear()
        }
    }

    fun addFromLocalData(item: RoomItem) {
        ITEMS.add(item)
        DOWNLOAD.add(Pair(item.id,true))
    }

    fun addPhoto(resource: Bitmap) {
        PHOTOS.add(resource)
    }

    fun deleteRoom(position: Int) {
        deleteRoomPhoto(ITEMS[position])
        deleteRoomFromDatabase(ITEMS[position])
        ITEMS.removeAt(position)
    }

    private fun deleteRoomFromDatabase(roomItem: RoomItem) {
        FirebaseHandler.RealtimeDatabase.getRoomsRef()
            .child(roomItem.buildingID)
            .child(roomItem.id)
            .removeValue()
            .addOnSuccessListener {
                Log.i("roomDeletion","roomInfoDeleted")
            }
    }

    private fun deleteRoomPhoto(roomItem: RoomItem) {
        val defaultID = "default"
        if(roomItem.photo!=defaultID){
            FirebaseHandler.RealtimeDatabase.getBuildingStorageRef(roomItem.buildingID).child(roomItem.photo).delete()
                .addOnSuccessListener {
                    Log.i("roomDeletion", "photoDeleted")
                }
        }
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