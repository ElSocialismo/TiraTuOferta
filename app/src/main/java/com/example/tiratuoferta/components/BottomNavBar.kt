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
                    // Evitamos que se navegue de nuevo a la misma ruta
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Evita duplicar el mismo destino en el back stack
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Restaura el estado anterior si ya estaba en el back stack
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
