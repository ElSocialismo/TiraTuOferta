package com.example.tiratuoferta.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Auction(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val startingPrice: Double = 0.0,
    val currentBid: Double = 0.0,
    val minimumIncrease: Double = 0.0,
    val endTime: Long = 0L,
    val userId: String = "",
    val participants: MutableMap<String, Boolean>? = null,
    var Favorite: Boolean = false
) : Parcelable
