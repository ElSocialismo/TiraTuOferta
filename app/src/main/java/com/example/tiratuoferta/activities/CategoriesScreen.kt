package com.example.tiratuoferta.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tiratuoferta.R
import com.example.tiratuoferta.models.Auction
import com.example.tiratuoferta.screens.AuctionItem
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

// Data class para representar una categoría con imágenes desde recursos (painter)
data class Category(val name: String, val icon: Painter, val route: String)

@Composable
fun CategoriesScreen(navController: NavController) {
    // Lista de categorías con imágenes
    val categories = listOf(
        Category("Electrónica", painterResource(id = R.drawable.electronic_icon), "Electronica"),
        Category("Ropa", painterResource(id = R.drawable.clothing_icon), "Ropa"),
        Category("Juguetes", painterResource(id = R.drawable.toys_icon), "Juguetes"),
        Category("Automóviles", painterResource(id = R.drawable.automobile_icon), "Automoviles"),
        Category("Hogar", painterResource(id = R.drawable.home_icon), "Casa")
    )

    // UI de la pantalla de Categorías
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Selecciona una categoría para ver las subastas", style = MaterialTheme.typography.titleMedium)

        categories.forEach { category ->
            CategoryCard(category, navController)
        }
    }
}

@Composable
fun CategoryCard(category: Category, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navegar a la pantalla de subastas de la categoría
                navController.navigate("categoryAuctionList/${category.route}")
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Usamos el Image composable para mostrar la imagen
            Image(painter = category.icon, contentDescription = category.name, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = category.name, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Función para cargar las subastas por categoría
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAuctionListScreen(navController: NavController, category: String) {
    // Obtener las subastas filtradas por la categoría seleccionada
    val auctions = remember { mutableStateListOf<Auction>() }

    // Simulamos la carga de subastas
    LaunchedEffect(category) {
        getAuctionsByCategory(category) { filteredAuctions ->
            auctions.clear()
            auctions.addAll(filteredAuctions)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Subastas de $category") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (auctions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay subastas en esta categoría.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(auctions) { auction ->
                        AuctionItem(auction = auction, navController = navController)
                    }
                }
            }
        }
    }
}

// Función para obtener subastas filtradas por categoría desde Firebase
suspend fun getAuctionsByCategory(category: String, onComplete: (List<Auction>) -> Unit) {
    val database = FirebaseDatabase.getInstance().getReference("auctions")
    val snapshot = database.orderByChild("category").equalTo(category).get().await()
    val auctions = snapshot.children.mapNotNull {
        it.getValue(Auction::class.java)
    }
    onComplete(auctions)
}
