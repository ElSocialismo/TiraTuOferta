package com.example.tiratuoferta.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.navigation.NavController
import com.example.tiratuoferta.models.Auction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSubastasScreen(navController: NavController) {
    // Estado mutable para almacenar las subastas del usuario
    var myAuctions by remember { mutableStateOf<List<Auction>>(emptyList()) }

    // Obtener las subastas del usuario desde Firebase
    LaunchedEffect(Unit) {
        getUserAuctions { auctions ->
            myAuctions = auctions
        }
    }

    // UI de la pantalla "Mis Subastas"
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Subastas") }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (myAuctions.isEmpty()) {
                // Si el usuario no tiene subastas, mostramos un mensaje
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No tienes subastas.")
                }
            } else {
                // Si el usuario tiene subastas, las mostramos en una lista
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(myAuctions) { auction ->
                        val context = LocalContext.current // Aquí obtenemos el contexto

                        AuctionItem(
                            auction = auction,
                            navController = navController,
                            onEdit = { navController.navigate("editAuction/${auction.id}") },  // Navegar a la edición
                            onDelete = { deleteAuction(auction.id, navController, context) }  // Pasamos el contexto
                        )
                    }
                }
            }
        }
    }
}

// Función para obtener las subastas del usuario desde Firebase
suspend fun getUserAuctions(onComplete: (List<Auction>) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val auctionsRef = FirebaseDatabase.getInstance().getReference("auctions")

    try {
        // Obtener todas las subastas
        val snapshot = auctionsRef.orderByChild("userId").equalTo(userId).get().await()

        // Convertir los datos en una lista de objetos Auction
        val auctions = snapshot.children.mapNotNull { it.getValue(Auction::class.java) }

        onComplete(auctions) // Devolver las subastas
    } catch (e: Exception) {
        onComplete(emptyList()) // Si ocurre un error, devolvemos una lista vacía
    }
}

@Composable
fun AuctionItem(
    auction: Auction,
    navController: NavController,
    onEdit: () -> Unit,
    onDelete: (Context) -> Unit // Cambiado para aceptar el contexto
) {
    val context = LocalContext.current

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

        // Mostrar el precio actual de la subasta
        Text(text = "Precio actual: ${auction.currentBid}$", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // Botón para ver detalles de la subasta
        Button(
            onClick = {
                navController.navigate("auctionDetails/${auction.id}")
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Ver Detalles")
        }

        // Botón para editar la subasta
        Button(
            onClick = {
                onEdit() // Llamar al callback de edición
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Editar")
        }

        // Botón para eliminar la subasta
        Button(
            onClick = {
                onDelete(context) // Pasamos el contexto para invocar Toast
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Eliminar")
        }
    }
}

fun deleteAuction(auctionId: String, navController: NavController, context: Context) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val auctionRef = FirebaseDatabase.getInstance().getReference("auctions").child(auctionId)

    // Eliminar la subasta de Firebase
    auctionRef.removeValue()
        .addOnSuccessListener {
            Toast.makeText(context, "Subasta eliminada exitosamente", Toast.LENGTH_SHORT).show()
            navController.popBackStack()  // Regresar a la pantalla anterior después de eliminar
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al eliminar la subasta", Toast.LENGTH_SHORT).show()
        }
}
