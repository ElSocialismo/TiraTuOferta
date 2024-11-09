package com.example.tiratuoferta.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tiratuoferta.models.Auction
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AuctionList(navController: NavController) {
    val auctionList = remember { mutableStateListOf<Auction>() }

    // Firebase listener to fetch auctions
    val database = FirebaseDatabase.getInstance().getReference("auctions")
    val auctionListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            auctionList.clear()
            for (auctionSnapshot in snapshot.children) {
                auctionSnapshot.getValue(Auction::class.java)?.let {
                    auctionList.add(it)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle database error
        }
    }
    database.addValueEventListener(auctionListener)

    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
        items(auctionList.size) { index ->
            AuctionItem(auction = auctionList[index], navController = navController)
        }
    }
}

@Composable
fun AuctionItem(auction: Auction, navController: NavController) {
    // Formato de fecha para mostrar la fecha de finalización
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val endDate = dateFormatter.format(auction.endTime)

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { navController.navigate("auctionDetails/${auction.id}") },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Carga y muestra la imagen
        AsyncImage(
            model = auction.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .padding(8.dp)
        )
        Text(text = auction.title, maxLines = 1)
        Text(text = "Ends on: $endDate")
        Button(onClick = { navController.navigate("auctionDetails/${auction.id}") }) {
            Text("VER SUBASTA")
        }
    }
}
