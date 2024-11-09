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

@Composable
fun DrawerContent(navController: NavController) {
    Column {
        Text(
            text = "Menú de Navegación",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.h6
        )
        Divider()

        // Navegar a la pantalla de Mis Subastas
        DrawerItem("Mis Subastas") {
            navController.navigate("misSubastas") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }

        // Navegar a la pantalla de Contactar
        DrawerItem("Contactar") {
            navController.navigate("contactar") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }

        // Navegar a la pantalla de Idioma
        DrawerItem("Idioma") {
            navController.navigate("idioma") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }

        // Navegar a la pantalla de Ayuda
        DrawerItem("Ayuda") {
            navController.navigate("ayuda") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }

        // Navegar a la pantalla de Cerrar Sesión
        DrawerItem("Cerrar sesión") {
            navController.navigate("cerrarSesion") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
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


