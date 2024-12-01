package com.example.tiratuoferta.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiratuoferta.activities.BottomNavItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope() // Define la corutina aquí
    var showLogoutDialog by remember { mutableStateOf(false) } // Estado para el cuadro de diálogo

    Column {
        Text(
            text = "Menú de Navegación",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.h6
        )
        Divider()

        // Ir al Home
        DrawerItem("Ir al Home") {
            scope.launch { drawerState.close() }
            if (navController.currentDestination?.route != BottomNavItem.Home.route) {
                navController.navigate(BottomNavItem.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        DrawerItem("Mis Subastas") {
            scope.launch { drawerState.close() }
            navController.navigate("misSubastas") {
                popUpTo(BottomNavItem.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }

        DrawerItem("Contactar") {
            scope.launch { drawerState.close() }
            navController.navigate("contactar") {
                popUpTo(BottomNavItem.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }

        DrawerItem("Ayuda") {
            scope.launch { drawerState.close() }
            navController.navigate("ayuda") {
                popUpTo(BottomNavItem.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }

        DrawerItem("Cerrar sesión") {
            scope.launch { drawerState.close() }
            showLogoutDialog = true // Activar el cuadro de diálogo
        }
    }

    // Cuadro de diálogo de confirmación de cierre de sesión
    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                FirebaseAuth.getInstance().signOut() // Cerrar sesión en Firebase
                navController.navigate("login") { // Navegar al LoginActivity
                    popUpTo(0) { inclusive = true }
                }
            },
            onDismiss = { showLogoutDialog = false }
        )
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

@Composable
fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Cerrar sesión") },
        text = { Text(text = "¿Estás seguro de que deseas cerrar sesión?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )
}
