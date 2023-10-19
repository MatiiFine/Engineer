package com.example.engenieer.buildings

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.engenieer.helper.FirebaseHandler
import java.util.ArrayList

object Building {

    val ITEMS: MutableList<BuildingItem> = ArrayList()
    var iterator: Int = 0

    fun addItem(item: BuildingItem) {
        ITEMS.add(item)
        iterator = 0
    }

    fun clearItems(){
        ITEMS.clear()
    }

    fun isAbleToDownload(): Boolean{
        return iterator < ITEMS.size
    }

    fun getPhotoID(): String{
        iterator += 1
        return ITEMS[iterator-1].photo
    }

    fun getIter(): Int{
        return iterator-1
    }

    fun deleteBuilding(position: Int){
        deleteBuildingPhoto(ITEMS[position].photo)
        deleteBuildingFromDatabase(ITEMS[position].buildingID)
        ITEMS.removeAt(position)
    }

    private fun deleteBuildingPhoto(photoID: String){
        FirebaseHandler.RealtimeDatabase.getBuildingStorageRef(photoID).delete().addOnSuccessListener {
            Log.i("buildingDeletion","photoDeleted")
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