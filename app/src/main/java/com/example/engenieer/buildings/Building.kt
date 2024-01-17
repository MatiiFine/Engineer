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
        PHOTOS.clear()
        DOWNLOAD.clear()
    }

    fun deleteBuilding(position: Int): String{
        val buildingID = ITEMS[position].buildingID
        deleteBuildingPhoto(ITEMS[position].photo)
        deleteBuildingFromDatabase(ITEMS[position].buildingID)
        ITEMS.removeAt(position)
        DOWNLOAD.removeAt(position)
        PHOTOS.removeAt(position)
        return buildingID
    }

    fun deleteBuildingPhoto(photoID: String){
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

    fun editLocalData(buildingItem: BuildingItem): String {
        val element = findElementByID(buildingItem.buildingID)
        val index = ITEMS.indexOf(element)
        ITEMS[index] = buildingItem
        DOWNLOAD[index] = Pair(buildingItem.buildingID,true)

        return element.photo
    }

    private fun findElementByID(buildingID: String): BuildingItem {
        for (item in ITEMS){
            if (item.buildingID == buildingID)
                return item
        }
        return null!!
    }

    fun editPhoto(resource: Bitmap, buildingItem: BuildingItem) {
        val index = ITEMS.indexOf(buildingItem)
        PHOTOS[index] = resource
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