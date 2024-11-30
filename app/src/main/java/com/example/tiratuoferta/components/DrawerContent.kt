package com.example.tiratuoferta.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiratuoferta.activities.BottomNavItem

@Composable
fun DrawerContent(navController: NavController) {
    Column {
        Text(
            text = "Menú de Navegación",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.h6
        )
        Divider()

        // Navegar a la pantalla de Home
        DrawerItem("Ir al Home") {
            // Limpiar la pila y navegar a Home
            navController.navigate(BottomNavItem.Home.route) {
                popUpTo(BottomNavItem.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }

        // Otros elementos de navegación
        DrawerItem("Mis Subastas") {
            navController.navigate("misSubastas") {
                popUpTo(BottomNavItem.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }

        DrawerItem("Contactar") {
            navController.navigate("contactar") {
                popUpTo(BottomNavItem.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }

        DrawerItem("Ayuda") {
            navController.navigate("ayuda") {
                popUpTo(BottomNavItem.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }

        DrawerItem("Cerrar sesión") {
            navController.navigate("cerrarSesion") {
                popUpTo(BottomNavItem.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }
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
