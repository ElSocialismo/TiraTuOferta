package com.example.tiratuoferta.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Auction(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "", // Esta será la categoría seleccionada por el usuario
    val startingPrice: Double = 0.0,
    val currentBid: Double = 0.0,
    val minimumIncrease: Double = 0.0,
    var endTime: Long = 0L,
    val userId: String = "",
    val participants: MutableMap<String, Boolean>? = null,
    var favorite: Boolean = false,
    val startTime: Long = 0L
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
        favorite = false,
        startTime = 0L
    )
}

@Parcelize
data class Bid(
    val userId: String = "",
    val amount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable
