package com.example.tiratuoferta.components

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiratuoferta.activities.BottomNavItem
import com.example.tiratuoferta.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

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

        // Botón de Cerrar Sesión
        DrawerItem("Cerrar sesión") {
            showLogoutDialog = true
        }
    }

    // Cuadro de diálogo de confirmación de cierre de sesión
    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                FirebaseAuth.getInstance().signOut() // Cerrar sesión en Firebase

                // Navegar al LoginActivity y cerrar la actividad actual
                val intent = Intent(navController.context, LoginActivity::class.java)
                navController.context.startActivity(intent)

                // Asegúrate de cerrar la actividad actual
                (navController.context as? Activity)?.finish()
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
                Text(text = "Confirmar", color = MaterialTheme.colors.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )
}
