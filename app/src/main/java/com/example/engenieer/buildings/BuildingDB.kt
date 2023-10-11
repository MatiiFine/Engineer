package com.example.engenieer.buildings

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BuildingDB(
    val name: String,
    val description: String,
    val shortDescription: String,
    val photo: String
): Parcelable