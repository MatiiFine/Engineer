package com.example.engenieer.rooms

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.engenieer.buildings.Building
import com.example.engenieer.helper.FirebaseHandler
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList

object Room {
    val ITEMS: MutableList<RoomItem> = ArrayList()
    val PHOTOS: ArrayList<Bitmap> = ArrayList()
    val DOWNLOAD: MutableList<Pair<String, Boolean>> = ArrayList()
    val ROOMS_OF_BUILDING: MutableList<Pair<String,String>> = ArrayList()
    val ROOMS_PHOTOS_OF_BUILDING: MutableList<Pair<String,String>> = ArrayList()
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
        DOWNLOAD.removeAt(position)
        PHOTOS.removeAt(position)
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

    fun deleteRoomsOfBuilding(buildingID: String, ref: DatabaseReference, listener: ValueEventListener) {
        val defaultID = "default"
        var toDelete = ROOMS_OF_BUILDING.size
        var rooms = 0
        var photos = 0
        FirebaseHandler.RealtimeDatabase.getRoomsRef().child(buildingID).removeValue()
        for(room in ROOMS_PHOTOS_OF_BUILDING){
            if(room.second != defaultID){
                FirebaseHandler.RealtimeDatabase.getRoomStorageRef(buildingID).child(room.second).delete()
                    .addOnSuccessListener {

                    }
            }
        }
        ref.removeEventListener(listener)
    }

    fun getCurrentBuildingID(): String{
        return currentBuildingID
    }

    fun clear(){
        ITEMS.clear()
        DOWNLOAD.clear()
        PHOTOS.clear()
        ROOMS_OF_BUILDING.clear()
        ROOMS_PHOTOS_OF_BUILDING.clear()
    }

    fun addRoomToDeletionList(buildingID: String, roomID: String, roomPhoto: String) {
        ROOMS_OF_BUILDING.add(Pair(buildingID,roomID))
        ROOMS_PHOTOS_OF_BUILDING.add(Pair(buildingID,roomPhoto))
    }

    fun editLocalData(roomItem: RoomItem): String {
        val element = findElementByID(roomItem.id)
        val index = ITEMS.indexOf(element)
        ITEMS[index] = roomItem
        DOWNLOAD[index] = Pair(roomItem.id,true)

        return element.photo
    }

    private fun findElementByID(id: String): RoomItem {
        for (item in ITEMS){
            if (item.id == id)
                return item
        }
        return null!!
    }

    fun deleteOldRoomPhoto(oldPhotoID: String, buildingID: String) {
        val defaultID = "default"
        if (oldPhotoID!=defaultID){
            FirebaseHandler.RealtimeDatabase.getRoomStorageRef(buildingID).child(oldPhotoID).delete()
                .addOnSuccessListener {
                Log.i("roomDeletion", "photoDeleted")
            }
        }
    }

    fun editPhoto(resource: Bitmap, roomItem: RoomItem) {
        val index = ITEMS.indexOf(roomItem)
        PHOTOS[index] = resource
    }

    fun getItem(position: Int): RoomItem {
        return ITEMS[position]
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