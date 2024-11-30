package com.example.tiratuoferta.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tiratuoferta.activities.BottomNavItem

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Categories,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )

    BottomNavigation {
        // Obtenemos el estado actual del BackStackEntry
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route


        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    // Si estamos en la misma pantalla, no hacemos nada
                    if (currentRoute != item.route) {
                        // Si no estamos en la ruta, navegar y limpiar la pila de pantallas anteriores
                        navController.navigate(item.route) {
                            // Limpiar la pila de navegación hacia la pantalla de inicio
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            // Evitar duplicación de la ruta
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
