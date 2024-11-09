// AuctionDetailsScreen.kt
package com.example.tiratuoferta.screens

import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiratuoferta.models.Auction
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@Composable
fun AuctionDetailsScreen(auctionId: String) {
    var auction by remember { mutableStateOf<Auction?>(null) }

    // Consultar la subasta en Firebase usando el auctionId
    LaunchedEffect(auctionId) {
        val database = FirebaseDatabase.getInstance().getReference("auctions").child(auctionId)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                auction = snapshot.getValue(Auction::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })
    }

    if (auction != null) {
        // Mostrar los detalles de la subasta
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Title: ${auction?.title}")
            Text(text = "Description: ${auction?.description}")
            Text(text = "Starting Price: ${auction?.startingPrice}")
            Text(text = "Current Bid: ${auction?.currentBid}")
            // Mostrar otros detalles
        }
    } else {
        // Mostrar un indicador de carga mientras se obtienen los datos
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
