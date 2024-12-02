package com.example.tiratuoferta.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

@Composable
fun UserListScreen(navController: NavController) {
    // Estado para almacenar la lista de usuarios
    val users = remember { mutableStateListOf<User>() }
    val isLoading = remember { mutableStateOf(true) }  // Estado de carga
    val dbRef = FirebaseDatabase.getInstance().getReference("users")

    // Obtener los usuarios en tiempo real
    LaunchedEffect(Unit) {
        dbRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                user?.let { users.add(it) }
                isLoading.value = false  // Detener la carga cuando los datos estén disponibles
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    // Aquí puedes manejar actualizaciones de usuarios si es necesario
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    // Aquí puedes manejar eliminación de usuarios si es necesario
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("UserList", "Error al obtener usuarios: ${error.message}")
            }
        })
    }

    // Mostrar la lista de usuarios
    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading.value) {
            // Mostrar un indicador de carga mientras se obtienen los usuarios
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(users) { user ->
                    UserListItem(user = user, navController = navController)
                }
            }
        }
    }
}

@Composable
fun UserListItem(user: User, navController: NavController) {
    Card(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = user.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = user.email, fontSize = 14.sp)
                Text(text = "Phone: ${user.phoneNumber}", fontSize = 12.sp)
            }

            // Botón para abrir el chat con el usuario
            Button(
                onClick = {
                    // Navegar a la pantalla de chat, pasando el ID del chat
                    val chatId = generateChatId(user.email) // O cualquier otro identificador único
                    navController.navigate("personalChat/$chatId")
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Chat")
            }
        }
    }
}

// Función para generar un ID de chat único
fun generateChatId(userEmail: String): String {
    // Aquí puedes usar un hash, una combinación de IDs, o cualquier otra lógica
    return userEmail.hashCode().toString() // Esto es solo un ejemplo
}

data class User(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = ""
)
