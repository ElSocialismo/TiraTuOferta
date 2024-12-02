package com.example.tiratuoferta.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val userId: String = "",
    val userName: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@SuppressLint("SimpleDateFormat")
@Composable
fun PersonalChatScreen(navController: NavController, chatId: String) {
    val db = FirebaseDatabase.getInstance().reference
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val currentUserName = FirebaseAuth.getInstance().currentUser?.displayName.orEmpty()  // Obtener nombre del usuario actual
    var newMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }  // Estado de carga

    // Cargar mensajes del chat de Firebase usando ChildEventListener para evitar duplicados
    LaunchedEffect(chatId) {
        db.child("chats").child(chatId).child("messages")
            .orderByChild("timestamp") // Ordenar mensajes por timestamp
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(ChatMessage::class.java)
                    message?.let {
                        messages.add(it)
                        messages.sortBy { message -> message.timestamp }
                    }
                    isLoading = false  // Dejar de cargar una vez que los datos estén disponibles
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // Puedes manejar cambios en los mensajes si es necesario
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Puedes manejar la eliminación de mensajes si es necesario
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Chat", "Error cargando los mensajes: ${error.message}")
                }
            })
    }

    // Mostrar la interfaz de usuario
    Column(modifier = Modifier.fillMaxSize()) {
        // Mostrar la lista de mensajes
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            if (isLoading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            } else {
                items(messages) { message ->
                    MessageItem(message)
                }
            }
        }

        // Campo de texto y botón para enviar mensaje
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Gray.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium)
                    .padding(12.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
            )
            IconButton(
                onClick = {
                    if (newMessage.isNotBlank()) {
                        sendMessage(currentUserId, currentUserName, newMessage, chatId)
                        newMessage = "" // Limpiar el campo después de enviar
                    }
                }
            ) {
                Icon(painter = painterResource(id = android.R.drawable.ic_menu_send), contentDescription = "Send")
            }
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage) {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = dateFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = message.userName,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = message.content,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = formattedTime,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

fun sendMessage(userId: String, userName: String, content: String, chatId: String) {
    val db = FirebaseDatabase.getInstance().reference
    val message = ChatMessage(userId = userId, userName = userName, content = content, timestamp = System.currentTimeMillis())

    // Agregar el mensaje a Firebase usando push() para generar un ID único para cada mensaje
    db.child("chats").child(chatId).child("messages")
        .push() // Usar push() para generar un ID único para cada mensaje
        .setValue(message)
        .addOnSuccessListener {
            Log.d("Chat", "Mensaje enviado correctamente")
        }
        .addOnFailureListener { e ->
            Log.e("Chat", "Error enviando el mensaje: ${e.message}")
        }
}
