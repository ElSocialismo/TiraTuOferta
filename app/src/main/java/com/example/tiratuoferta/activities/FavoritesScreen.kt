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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    // Estado mutable para almacenar las subastas favoritas
    var favoriteAuctions by remember { mutableStateOf<List<Auction>>(emptyList()) }
    var favoriteAuctionIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Obtener las subastas favoritas desde Firebase
    LaunchedEffect(Unit) {
        getFavoriteAuctions { auctions, favorites ->
            favoriteAuctions = auctions
            favoriteAuctionIds = favorites
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
                        AuctionItem(auction = auction, navController = navController, isFavorite = favoriteAuctionIds.contains(auction.id))
                    }
                }
            }
        }
    }
}

// Función para obtener las subastas favoritas desde Firebase
suspend fun getFavoriteAuctions(onComplete: (List<Auction>, Set<String>) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val auctionsRef = FirebaseDatabase.getInstance().getReference("users/$userId/favorites")

    try {
        // Obtener las IDs de las subastas favoritas
        val snapshot = auctionsRef.get().await()
        val favoriteAuctionIds = snapshot.children.mapNotNull { it.key }.toSet()

        // Ahora obtenemos las subastas completas
        val allAuctionsRef = FirebaseDatabase.getInstance().getReference("auctions")
        val auctionsSnapshot = allAuctionsRef.get().await()

        // Filtrar solo las subastas favoritas
        val auctions = auctionsSnapshot.children.mapNotNull {
            val auction = it.getValue(Auction::class.java)
            if (auction != null && favoriteAuctionIds.contains(auction.id)) auction else null
        }

        onComplete(auctions, favoriteAuctionIds) // Devolvemos las subastas favoritas y sus IDs
    } catch (e: Exception) {
        onComplete(emptyList(), emptySet()) // Si ocurre un error, devolvemos una lista vacía
    }
}

// Componente para mostrar cada subasta en la lista de favoritos
@Composable
fun AuctionItem(auction: Auction, navController: NavController, isFavorite: Boolean) {
    // Formato de fecha para mostrar la fecha de finalización
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val endDate = dateFormatter.format(Date(auction.endTime))

    val context = LocalContext.current  // Obtener el contexto dentro del composable

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

        // Mostrar botón de "Agregar a Favoritos" solo si no está en favoritos
        if (!isFavorite) {
            Button(
                onClick = {
                    addToFavorites(auction.id, context)  // Llamar a la función para agregar a favoritos
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Agregar a Favoritos")
            }
        }
    }
}

// Función para agregar una subasta a los favoritos del usuario
fun addToFavorites(auctionId: String, context: Context) {
    // Obtener el ID del usuario actual (usamos FirebaseAuth para obtenerlo)
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // Referencia de la base de datos donde se almacenan los favoritos
    val favoritesRef = FirebaseDatabase.getInstance().getReference("users/$userId/favorites")

    // Agregar el ID de la subasta a la lista de favoritos
    favoritesRef.child(auctionId).setValue(true)
        .addOnSuccessListener {
            Toast.makeText(context, "Subasta agregada a favoritos", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al agregar a favoritos", Toast.LENGTH_SHORT).show()
        }
}
