package com.example.tiratuoferta.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
            val currentTime = System.currentTimeMillis() // Hora actual en milisegundos
            for (auctionSnapshot in snapshot.children) {
                auctionSnapshot.getValue(Auction::class.java)?.let { auction ->
                    // Filtrar subastas activas (tiempo actual menor al tiempo de finalización)
                    if (auction.endTime > currentTime) {
                        auctionList.add(auction)
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Manejar error en la base de datos
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

    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("auctionDetails/${auction.id}") }, // Redirigir al detalle
        shape = MaterialTheme.shapes.medium, // Bordes redondeados
        backgroundColor = Color(0xFFF5F5F5), // Fondo suave gris claro
        elevation = 4.dp // Sombra suave para dar profundidad
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp) // Espaciado dentro de la tarjeta
        ) {
            // Mostrar imagen de la subasta
            AsyncImage(
                model = auction.imageUrl,
                contentDescription = "Imagen de subasta",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 12.dp)
                    .clip(androidx.compose.material3.MaterialTheme.shapes.medium)
            )

            // Título de la subasta
            Text(
                text = auction.title,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF00695C), // Verde petróleo (primario)
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp)) // Espacio entre el título y la descripción

            // Descripción de la subasta
            Text(
                text = auction.description ?: "Descripción no disponible", // Descripción por defecto si no existe
                style = MaterialTheme.typography.body2.copy(color = Color(0xFF37474F)), // Negro carbón
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp)) // Espacio entre la descripción y el precio

            // Precio de la subasta
            Text(
                text = "Precio inicial: \n${auction.startingPrice} €",
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFFF7043) // Naranja coral (secundario)
            )

            Spacer(modifier = Modifier.height(8.dp)) // Espacio entre el precio y la fecha

            // Fecha de finalización
            Text(
                text = "Termina el: $endDate",
                style = MaterialTheme.typography.body2.copy(color = Color(0xFF0288D1)), // Azul oscuro para la fecha
            )
        }
    }
}
