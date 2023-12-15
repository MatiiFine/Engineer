package com.example.engenieer.buildings

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.engenieer.helper.FirebaseHandler
import kotlin.collections.ArrayList

object Building {

    val ITEMS: MutableList<BuildingItem> = ArrayList()
    val DOWNLOAD: MutableList<Pair<String, Boolean>> = ArrayList()
    val PHOTOS: ArrayList<Bitmap> = ArrayList()
    var iterator: Int = 0
    var adminAccess: Boolean = false

    fun setAccess(access: Boolean){
        adminAccess = access
    }

    fun getAccess(): Boolean{
        return adminAccess
    }

    fun addItem(item: BuildingItem): Boolean {
        var found: Boolean = false
        for (building in ITEMS)
            if(building.buildingID == item.buildingID) found = true
        if (!found){
            ITEMS.add(item)
            DOWNLOAD.add(Pair(item.buildingID,false))
        }
        return !found
    }

    fun addItemFromLocalData(item: BuildingItem){
        ITEMS.add(item)
        DOWNLOAD.add(Pair(item.buildingID,true))
    }

    fun addPhoto(photo: Bitmap){
        PHOTOS.add(photo)
    }

    fun clearItems(){
        ITEMS.clear()
    }

    fun deleteBuilding(position: Int){
        deleteBuildingPhoto(ITEMS[position].photo)
        deleteBuildingFromDatabase(ITEMS[position].buildingID)
        ITEMS.removeAt(position)
    }

    private fun deleteBuildingPhoto(photoID: String){
        val defaultID = "default"
        if(photoID!=defaultID) {
            FirebaseHandler.RealtimeDatabase.getBuildingStorageRef(photoID).delete()
                .addOnSuccessListener {
                    Log.i("buildingDeletion", "photoDeleted")
                }
        }
    }

    private fun deleteBuildingFromDatabase(buildingID: String){
        FirebaseHandler.RealtimeDatabase.getBuildingRef(buildingID).removeValue().addOnSuccessListener {
            Log.i("buildingDeletion","buildingInfoDeleted")
        }
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