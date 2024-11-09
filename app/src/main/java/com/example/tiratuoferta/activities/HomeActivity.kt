package com.example.tiratuoferta.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.tiratuoferta.components.BottomNavBar
import com.example.tiratuoferta.ui.theme.TiraTuOfertaTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tiratuoferta.models.Auction
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TiraTuOfertaTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("createAuction") },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreenContent() }
            composable(BottomNavItem.Categories.route) { CategoriesScreen() }
            composable(BottomNavItem.Favorites.route) { FavoritesScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
            composable("createAuction") {
                CreateAuctionScreen(navController = navController, saveAuction = { auction ->
                    saveAuctionToFirebase(auction)
                })
            }
        }
    }
}

@Composable
fun HomeScreenContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido a tu app de subastas")
    }
}

// Función para guardar la subasta en Firebase Realtime Database
fun saveAuctionToFirebase(auction: Auction) {
    val database = FirebaseDatabase.getInstance().getReference("auctions")
    database.child(auction.id).setValue(auction)
        .addOnSuccessListener {
            // Aquí puedes mostrar un mensaje de éxito o realizar alguna acción adicional
        }
        .addOnFailureListener { e ->
            // Aquí puedes manejar errores en caso de que la subasta no se guarde correctamente
        }
}
