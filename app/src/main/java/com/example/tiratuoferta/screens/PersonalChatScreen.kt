package com.example.tiratuoferta.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PersonalChatScreen(navController: NavController, chatId: String) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    var userName by remember { mutableStateOf("") }  // Nombre del usuario
    var messages by remember { mutableStateOf<List<MessagePersonal>>(emptyList()) }
    var newMessage by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    // Obtener el nombre del usuario desde Firebase
    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    userName = document.getString("fullName") ?: "Usuario"
                }
        }
    }

    // Obtener mensajes en tiempo real desde Firestore para chat personal
    LaunchedEffect(chatId) {
        db.collection("chats_personales").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error fetching messages: $error")
                    return@addSnapshotListener
                }

                val newMessages = snapshot?.documents?.map { doc ->
                    doc.toObject(MessagePersonal::class.java)!!
                } ?: emptyList()

                messages = newMessages
            }
    }

    // Enviar mensaje
    fun sendMessage() {
        if (newMessage.isNotEmpty()) {
            val message = MessagePersonal(
                userId = userId ?: "",
                userName = userName,  // Nombre del usuario
                message = newMessage,
                timestamp = System.currentTimeMillis()
            )
            db.collection("chats_personales").document(chatId).collection("messages")
                .add(message)
            newMessage = "" // Limpiar el campo de mensaje
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat Personal") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = android.R.drawable.ic_media_previous), contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Mostrar mensajes
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    reverseLayout = true
                ) {
                    items(messages) { message ->
                        PersonalMessageItem(message = message)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Campo para escribir el mensaje
                TextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    label = { Text("Escribe un mensaje") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Botón de enviar mensaje
                Button(
                    onClick = { sendMessage() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enviar Mensaje")
                }
            }
        }
    )
}


@Composable
fun PersonalMessageItem(message: MessagePersonal) {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())  // Formato para la hora
    val formattedTime = sdf.format(Date(message.timestamp))  // Convertir timestamp a hora

    Column(modifier = Modifier.fillMaxWidth()) {
        // Mostrar el nombre del usuario
        Text(
            text = message.userName,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        // Mostrar el mensaje
        Text(
            text = message.message,
            fontSize = 16.sp
        )
        // Mostrar la hora
        Text(
            text = "A las $formattedTime",
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
data class MessagePersonal(
    val userId: String = "",
    val userName: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)



