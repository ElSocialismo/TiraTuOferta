package com.example.tiratuoferta.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tiratuoferta.models.Auction
import com.example.tiratuoferta.models.Bid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var fullName by remember { mutableStateOf(user?.displayName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var isEditing by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }

    // Variables para manejar el estado de los Toast
    var showPasswordUpdatedToast by remember { mutableStateOf(false) }
    var showNameUpdatedToast by remember { mutableStateOf(false) }
    var showErrorToast by remember { mutableStateOf(false) }

    var showToast by remember { mutableStateOf<Pair<Boolean, String>>(false to "") }

    // Control del contexto
    val context = LocalContext.current

    // Efecto para mostrar los Toasts
    LaunchedEffect(showPasswordUpdatedToast) {
        if (showPasswordUpdatedToast) {
            Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(showNameUpdatedToast) {
        if (showNameUpdatedToast) {
            Toast.makeText(context, "Nombre actualizado", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(showErrorToast) {
        if (showErrorToast) {
            Toast.makeText(context, "Error al actualizar el nombre o la contraseña", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Mi Perfil", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            if (isEditing) {
                FloatingActionButton(
                    onClick = {
                        // Guardar cambios
                        if (newPassword.isNotEmpty()) {
                            user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                                showToast = if (task.isSuccessful) {
                                    true to "Contraseña actualizada"
                                } else {
                                    true to "Error al actualizar la contraseña"
                                }
                            }
                        }
                        if (fullName != user?.displayName) {
                            user?.updateProfile(
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build()
                            )?.addOnCompleteListener { task ->
                                showToast = if (task.isSuccessful) {
                                    true to "Nombre actualizado"
                                } else {
                                    true to "Error al actualizar el nombre"
                                }
                            }
                        }
                        isEditing = false
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Guardar cambios")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de perfil
            Card(
                modifier = Modifier.size(120.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información del usuario
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Nombre", style = MaterialTheme.typography.labelMedium)
                    if (isEditing) {
                        TextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(text = fullName, style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Correo", style = MaterialTheme.typography.labelMedium)
                    Text(text = email, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subastas creadas
            ProfileSection(title = "Mis Subastas Creadas") {
                SubastasCreadas(userId = user?.uid ?: "")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subastas en las que participo
            ProfileSection(title = "Subastas en las que participo") {
                SubastasParticipadas(userId = user?.uid ?: "")
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SubastasCreadas(userId: String) {
    val subastasCreadas = remember { mutableStateListOf<Auction>() }

    // Obtén las subastas creadas por el usuario
    LaunchedEffect(userId) {
        FirebaseDatabase.getInstance().reference
            .child("auctions")
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                subastasCreadas.clear()
                for (auctionSnapshot in snapshot.children) {
                    auctionSnapshot.getValue(Auction::class.java)?.let {
                        subastasCreadas.add(it)
                    }
                }
            }
    }

    // Mostrar las subastas creadas
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(subastasCreadas.size) { index ->
            AuctionItem(auction = subastasCreadas[index])
        }
    }
}

@Composable
fun SubastasParticipadas(userId: String) {
    val subastasParticipadas = remember { mutableStateListOf<Auction>() }

    // Obtener las subastas en las que el usuario ha participado
    LaunchedEffect(userId) {
        FirebaseDatabase.getInstance().reference
            .child("auctions")
            .get() // Obtener todas las subastas
            .addOnSuccessListener { snapshot ->
                subastasParticipadas.clear()
                for (auctionSnapshot in snapshot.children) {
                    val auction = auctionSnapshot.getValue(Auction::class.java)

                    auction?.let { auctionItem ->
                        val bidsDatabase = auctionSnapshot.child("bids")

                        // Buscar en las pujas de esta subasta
                        for (bidSnapshot in bidsDatabase.children) {
                            val bid = bidSnapshot.getValue(Bid::class.java)
                            if (bid?.userId == userId) {
                                // Si el userId de la puja coincide con el usuario, agregar la subasta a la lista
                                subastasParticipadas.add(auctionItem)
                                break // Ya encontramos una puja del usuario, no necesitamos seguir buscando
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
                Log.d("SubastasParticipadas", "Error al obtener subastas: ${exception.message}")
            }
    }

    // Mostrar las subastas en las que el usuario ha participado
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(subastasParticipadas.size) { index ->
            AuctionItem(auction = subastasParticipadas[index])
        }
    }
}



@Composable
fun AuctionItem(auction: Auction) {
    // Mostrar la información de cada subasta
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text = auction.title, style = MaterialTheme.typography.bodyLarge)
        Text(text = "Precio inicial: ${auction.startingPrice} €", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Aquí agregarías la navegación a la pantalla de detalles de la subasta
                // navController.navigate("auctionDetails/${auction.id}")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ver detalles")
        }
    }
}
