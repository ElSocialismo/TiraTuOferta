package com.example.tiratuoferta.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiratuoferta.components.BottomNavBar
import com.example.tiratuoferta.models.Auction
import com.example.tiratuoferta.screens.AyudaScreen
import com.example.tiratuoferta.screens.CerrarSesionScreen
import com.example.tiratuoferta.screens.ContactarScreen
import com.example.tiratuoferta.screens.IdiomaScreen
import com.example.tiratuoferta.screens.MisSubastasScreen
import com.example.tiratuoferta.ui.theme.TiraTuOfertaTheme
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

import com.google.firebase.auth.FirebaseAuth

class HomeActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
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
            // Redirige a la pantalla de inicio de sesión
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
                // Nuevas rutas para las opciones del menú lateral
                composable("misSubastas") { MisSubastasScreen() }
                composable("contactar") { ContactarScreen() }
                composable("idioma") { IdiomaScreen() }
                composable("ayuda") { AyudaScreen() }
                composable("cerrarSesion") { CerrarSesionScreen() }
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

@Composable
fun DrawerContent(navController: NavController) {
    Column {
        Text(
            text = "Menú de Navegación",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.h6
        )
        Divider()
        DrawerItem("Mis Subastas") { navController.navigate("misSubastas") }
        DrawerItem("Añadir Subasta") { navController.navigate("createAuction") }
        DrawerItem("Contactar") { navController.navigate("contactar") }
        DrawerItem("Idioma") { navController.navigate("idioma") }
        DrawerItem("Ayuda") { navController.navigate("ayuda") }
        DrawerItem("Cerrar sesión") { navController.navigate("cerrarSesion") }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        text = { Text(text) }
    )
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