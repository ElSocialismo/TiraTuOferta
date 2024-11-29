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
    var endTime: Long = 0L,
    val userId: String = "",
    val participants: MutableMap<String, Boolean>? = null,
    var Favorite: Boolean = false,
    val startTime: Long = 0L  // Agregar valor predeterminado para el campo 'startTime'
) : Parcelable {
    // Constructor vacío para Firebase
    constructor() : this(
        id = "",
        title = "",
        description = "",
        imageUrl = "",
        category = "",
        startingPrice = 0.0,
        currentBid = 0.0,
        minimumIncrease = 0.0,
        endTime = 0L,
        userId = "",
        participants = null,
        Favorite = false,
        startTime = 0L
    )
}

@Parcelize
data class Bid(
    val userId: String = "",
    val amount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable
