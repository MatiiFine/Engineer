package com.example.engenieer.rooms

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoomDB(
    val name: String,
    val description: String,
    val shortDescription: String,
    val photo: String
): Parcelable