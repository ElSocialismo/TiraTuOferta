package com.example.tiratuoferta.activities

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : BottomNavItem("home", Icons.Filled.Home, "Home")
    object Categories : BottomNavItem("categories", Icons.Filled.List, "Categories")
    object Favorites : BottomNavItem("favorites", Icons.Filled.Favorite, "Favorites")
    object Profile : BottomNavItem("profile", Icons.Filled.Person, "Profile")
}
