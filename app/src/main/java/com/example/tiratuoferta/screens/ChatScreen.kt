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
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChatScreen(navController: NavController, auctionId: String) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    var userName by remember { mutableStateOf("") }  // Nombre del usuario
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var newMessage by remember { mutableStateOf("") }
    val messagesRef = FirebaseDatabase.getInstance().getReference("auctions").child(auctionId).child("chat")
    val userRef = FirebaseDatabase.getInstance().getReference("users")  // Referencia a los usuarios

    // Obtener el nombre del usuario desde Firebase
    LaunchedEffect(userId) {
        if (userId != null) {
            userRef.child(userId).child("fullName").get().addOnSuccessListener {
                userName = it.value.toString()  // Recuperar el nombre del usuario
            }
        }
    }

    // Obtener mensajes en tiempo real
    LaunchedEffect(auctionId) {
        messagesRef.orderByChild("timestamp").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    messages = messages + it
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Enviar mensaje
    fun sendMessage() {
        if (newMessage.isNotEmpty()) {
            val messageId = messagesRef.push().key ?: return
            val message = Message(
                userId = userId ?: "",
                userName = userName,  // Nombre del usuario
                message = newMessage,
                timestamp = System.currentTimeMillis()
            )
            messagesRef.child(messageId).setValue(message)
            newMessage = "" // Limpiar el campo de mensaje
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pantalla de Chat") },
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
                        MessageItem(message = message)
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
fun MessageItem(message: Message) {
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

data class Message(
    val userId: String = "",
    val userName: String = "",  // Almacenar el nombre del usuario
    val message: String = "",
    val timestamp: Long = 0L
)


