package com.example.tiratuoferta.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiratuoferta.components.BottomNavBar
import com.example.tiratuoferta.components.DrawerContent
import com.example.tiratuoferta.models.Auction
import com.example.tiratuoferta.screens.*
import com.example.tiratuoferta.ui.theme.TiraTuOfertaTheme
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        setContent {
            TiraTuOfertaTheme {
                MainScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Si el usuario no está autenticado, lo redirigimos al LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("TiraTuOferta") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    backgroundColor = Color(0xFF6200EE),
                    contentColor = Color.White,
                    elevation = 12.dp
                )
            },
            bottomBar = {
                BottomNavBar(navController) // Aquí pasa el navController al BottomNavBar
            },
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
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home.route) { AuctionList(navController) }

                // Categorías
                composable(BottomNavItem.Categories.route) { CategoriesScreen(navController) }

                composable(BottomNavItem.Favorites.route) { FavoritesScreen(navController) }
                composable(BottomNavItem.Profile.route) { ProfileScreen() }

                // Pantallas adicionales
                composable("createAuction") {
                    CreateAuctionScreen(navController = navController, saveAuction = { auction ->
                        saveAuctionToFirebase(auction)
                    })
                }
                composable("auctionDetails/{auctionId}") { backStackEntry ->
                    val auctionId = backStackEntry.arguments?.getString("auctionId") ?: ""
                    AuctionDetailsScreen(navController = navController, auctionId = auctionId)
                }
                composable("placeBid/{auctionId}") { backStackEntry ->
                    val auctionId = backStackEntry.arguments?.getString("auctionId") ?: ""
                    PlaceBidScreen(navController = navController, auctionId = auctionId)
                }

                composable("categoryAuctionList/{category}") { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category") ?: ""
                    CategoryAuctionListScreen(navController = navController, category = category)
                }

                // Otras pantallas
                composable("misSubastas") { MisSubastasScreen() }
                composable("contactar") { ContactarScreen() }
                composable("ayuda") { AyudaScreen() }
                composable("cerrarSesion") { CerrarSesionScreen() }
            }
        }
    }
}



fun saveAuctionToFirebase(auction: Auction) {
    val auctionsRef = FirebaseDatabase.getInstance().getReference("auctions")
    auctionsRef.child(auction.id).setValue(auction)
        .addOnSuccessListener {
            // Mensaje de éxito, puedes añadir lógica adicional si es necesario
        }
        .addOnFailureListener { e ->
            // Manejo de errores, puedes añadir un mensaje de error o lógica adicional
            e.printStackTrace()
        }
}
