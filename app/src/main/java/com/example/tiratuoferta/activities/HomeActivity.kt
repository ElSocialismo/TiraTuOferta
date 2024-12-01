package com.example.tiratuoferta.activities

import FavoritesScreen
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
            DrawerContent(navController, drawerState)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "TiraTuOferta",
                            style = MaterialTheme.typography.h5,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Abrir menú",
                                tint = Color.White
                            )
                        }
                    },
                    backgroundColor = Color(0xFF00695C), // Verde petróleo
                    elevation = 10.dp
                )
            },
            bottomBar = {
                BottomNavBar(navController)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("createAuction") },
                    backgroundColor = Color(0xFFFF7043), // Naranja coral
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Crear Subasta"
                    )
                }
            },
            backgroundColor = Color(0xFFECEFF1) // Gris humo
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home.route) { AuctionList(navController) }
                composable(BottomNavItem.Categories.route) { CategoriesScreen(navController) }
                composable(BottomNavItem.Favorites.route) { FavoritesScreen(navController) }
                composable(BottomNavItem.Profile.route) { ProfileScreen() }
                composable("createAuction") {
                    CreateAuctionScreen(navController = navController, saveAuction = { auction ->
                        saveAuctionToFirebase(auction)
                    })
                }
                composable("auctionDetails/{auctionId}") { backStackEntry ->
                    val auctionId = backStackEntry.arguments?.getString("auctionId") ?: ""
                    if (auctionId.isNotEmpty()) {
                        AuctionDetailsScreen(navController = navController, auctionId = auctionId)
                    } else {
                        Log.e("Navigation", "Auction ID vacío")
                    }
                }
                composable("placeBid/{auctionId}") { backStackEntry ->
                    val auctionId = backStackEntry.arguments?.getString("auctionId") ?: ""
                    if (auctionId.isNotEmpty()) {
                        PlaceBidScreen(navController = navController, auctionId = auctionId)
                    } else {
                        Log.e("Navigation", "Auction ID vacío")
                    }
                }
                composable("categoryAuctionList/{category}") { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category") ?: ""
                    CategoryAuctionListScreen(navController = navController, category = category)
                }
                composable("editAuction/{auctionId}") { backStackEntry ->
                    val auctionId = backStackEntry.arguments?.getString("auctionId") ?: ""
                    if (auctionId.isNotEmpty()) {
                        EditAuctionScreen(navController = navController, auctionId = auctionId)
                    } else {
                        Log.e("Navigation", "Auction ID vacío")
                    }
                }
                composable("misSubastas") { MisSubastasScreen(navController = navController) }
                composable("contactar") { ContactarScreen() }
                composable("ayuda") { AyudaScreen() }
            }
        }
    }
}

fun saveAuctionToFirebase(auction: Auction) {
    // Verificar que auction.id no sea nulo o vacío
    val auctionId = auction.id ?: generateAuctionId() // Si id es nulo, generamos uno nuevo

    val auctionsRef = FirebaseDatabase.getInstance().getReference("auctions")
    auctionsRef.child(auctionId).setValue(auction)
        .addOnSuccessListener {
            // Mensaje de éxito
            Log.d("Firebase", "Subasta guardada correctamente.")
        }
        .addOnFailureListener { e ->
            // Manejo de errores
            Log.e("Firebase", "Error al guardar subasta: ", e)
        }
}

// Función para generar un ID único para la subasta
fun generateAuctionId(): String {
    return "auction_${System.currentTimeMillis()}"
}
