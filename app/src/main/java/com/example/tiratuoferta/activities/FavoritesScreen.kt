package com.example.tiratuoferta.activities

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.navigation.NavController
import com.example.tiratuoferta.models.Auction
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

// Pantalla de Favoritos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    // Estado mutable para almacenar las subastas favoritas
    var favoriteAuctions by remember { mutableStateOf<List<Auction>>(emptyList()) }

    // Obtener las subastas favoritas desde Firebase
    LaunchedEffect(Unit) {
        getFavoriteAuctions { auctions ->
            favoriteAuctions = auctions
        }
    }

    // UI de la pantalla de Favoritos
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (favoriteAuctions.isEmpty()) {
                // Si no hay subastas favoritas, mostramos un mensaje
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No tienes subastas favoritas.")
                }
            } else {
                // Si hay subastas favoritas, las mostramos en una lista
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(favoriteAuctions) { auction ->
                        AuctionItem(auction = auction, navController = navController)
                    }
                }
            }
        }
    }
}

// Función para obtener las subastas favoritas desde Firebase
suspend fun getFavoriteAuctions(onComplete: (List<Auction>) -> Unit) {
    val userId = "user123" // Aquí deberías usar el ID del usuario actual
    val auctionsRef = FirebaseDatabase.getInstance().getReference("users/$userId/favorites")

    try {
        // Obtener las IDs de las subastas favoritas
        val snapshot = auctionsRef.get().await()
        val favoriteAuctionIds = snapshot.children.mapNotNull { it.key }

        // Ahora obtenemos las subastas completas
        val allAuctionsRef = FirebaseDatabase.getInstance().getReference("auctions")
        val auctionsSnapshot = allAuctionsRef.get().await()

        // Filtrar solo las subastas favoritas
        val auctions = auctionsSnapshot.children.mapNotNull {
            val auction = it.getValue(Auction::class.java)
            if (auction != null && favoriteAuctionIds.contains(auction.id)) auction else null
        }

        onComplete(auctions) // Devolvemos las subastas favoritas
    } catch (e: Exception) {
        onComplete(emptyList()) // Si ocurre un error, devolvemos una lista vacía
    }
}

// Componente para mostrar cada subasta en la lista de favoritos
@Composable
fun AuctionItem(auction: Auction, navController: NavController) {
    // Formato de fecha para mostrar la fecha de finalización
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val endDate = dateFormatter.format(Date(auction.endTime))

    Column(modifier = Modifier.padding(16.dp)) {
        // Mostrar imagen de la subasta
        AsyncImage(
            model = auction.imageUrl,
            contentDescription = "Imagen de subasta",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 8.dp)
        )

        // Mostrar título de la subasta
        Text(text = auction.title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar descripción de la subasta
        Text(text = auction.description, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar fecha de finalización de la subasta
        Text(text = "Termina el: $endDate", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar el precio actual de la subasta
        Text(text = "Precio actual: ${auction.currentBid}$", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // Botón para navegar a los detalles de la subasta
        Button(
            onClick = {
                navController.navigate("auctionDetails/${auction.id}")
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Ver Detalles")
        }
    }
}
