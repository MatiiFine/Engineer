package com.example.engenieer.booking

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class BookingDB (
    val roomName: String,
    val owner: String,
    val equipment: String,
    val date: String,
    val startHour: String,
    val endHour: String
    ): Parcelable