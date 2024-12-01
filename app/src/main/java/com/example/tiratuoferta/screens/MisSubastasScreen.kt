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
    var myAuctions by remember { mutableStateOf<List<Auction>>(emptyList()) }

    LaunchedEffect(Unit) {
        getUserAuctions { auctions ->
            myAuctions = auctions
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Subastas") },
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (myAuctions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes subastas.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(myAuctions) { auction ->
                        AuctionCard(
                            auction = auction,
                            navController = navController,
                            onEdit = { navController.navigate("editAuction/${auction.id}") },
                            onDelete = { context -> deleteAuction(auction.id, navController, context) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AuctionCard(
    auction: Auction,
    navController: NavController,
    onEdit: () -> Unit,
    onDelete: (Context) -> Unit
) {
    val context = LocalContext.current

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Imagen de la subasta
            AsyncImage(
                model = auction.imageUrl,
                contentDescription = "Imagen de subasta",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Título
            Text(
                text = auction.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Descripción
            Text(
                text = auction.description,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Precio actual
            Text(
                text = "Precio actual: ${auction.currentBid}$",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { navController.navigate("auctionDetails/${auction.id}") }) {
                    Text("Ver Detalles")
                }
                Button(onClick = { onEdit() }) {
                    Text("Editar")
                }
                Button(onClick = { onDelete(context) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Eliminar")
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
