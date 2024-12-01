// PlaceBidScreen.kt
package com.example.tiratuoferta.screens

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import com.example.tiratuoferta.models.Bid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PlaceBidScreen(navController: NavController, auctionId: String) {
    val context = LocalContext.current
    var bidAmount by remember { mutableStateOf("") }
    val userId = FirebaseAuth.getInstance().currentUser?.email ?: "Desconocido"
    val auctionRef = FirebaseDatabase.getInstance().getReference("auctions").child(auctionId)
    val bidsDatabase = auctionRef.child("bids")

    // Estado para almacenar el historial de pujas
    val bidHistory = remember { mutableStateListOf<Bid>() }
    var currentBid by remember { mutableStateOf(0.0) }
    var startingPrice by remember { mutableStateOf(0.0) }
    var minimumIncrease by remember { mutableStateOf(0.0) }
    var errorMessage by remember { mutableStateOf("") }
    var auctionEndTime by remember { mutableStateOf(0L) }

    // Listener para obtener el `currentBid`, `startingPrice`, `minimumIncrease` y el historial de pujas en tiempo real
    LaunchedEffect(Unit) {
        // Obtener el `currentBid`, `startingPrice`, `minimumIncrease` y el tiempo de finalización
        auctionRef.child("currentBid").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentBid = snapshot.getValue(Double::class.java) ?: 0.0
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })

        auctionRef.child("startingPrice").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                startingPrice = snapshot.getValue(Double::class.java) ?: 0.0
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })

        auctionRef.child("minimumIncrease").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                minimumIncrease = snapshot.getValue(Double::class.java) ?: 0.0
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })

        auctionRef.child("endTime").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                auctionEndTime = snapshot.getValue(Long::class.java) ?: 0L
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })

        // Obtener el historial de pujas
        bidsDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bidHistory.clear()
                for (bidSnapshot in snapshot.children) {
                    val bid = bidSnapshot.getValue(Bid::class.java)
                    bid?.let { bidHistory.add(it) }
                }

                // Detectar si la subasta ha terminado y quién es el ganador
                if (System.currentTimeMillis() > auctionEndTime && bidHistory.isNotEmpty()) {
                    val lastBid = bidHistory.last()
                    if (lastBid.userId == userId) {
                        // Si el usuario actual hizo la última puja, entonces es el ganador
                        sendNotification(context, "¡Felicidades!", "¡Ganaste la subasta procede con el pago!")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Realizar Puja") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Ingrese su puja para la subasta", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar la puja actual
            Text(text = "Puja actual: ${currentBid}$", style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar el precio inicial y el incremento mínimo
            Text(text = "Precio inicial: ${startingPrice}$", style = MaterialTheme.typography.body2)
            Text(text = "Incremento mínimo: ${minimumIncrease}$", style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(8.dp))

            // Campo de puja
            TextField(
                value = bidAmount,
                onValueChange = {
                    if (it.length <= 7) { // Limitar a 7 caracteres
                        bidAmount = it
                    }
                },
                label = { Text("Ingrese su puja") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar mensaje de error si el monto no es válido
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val amount = bidAmount.toDoubleOrNull()

                    // Validar que la puja sea mayor que el precio inicial y que cumpla con el incremento mínimo
                    if (amount == null) {
                        errorMessage = "Por favor ingrese un valor válido"
                    } else if (amount <= startingPrice) {
                        errorMessage = "La puja debe ser mayor al precio inicial de ${startingPrice}$"
                    } else if (amount <= currentBid) {
                        errorMessage = "La puja debe ser mayor a la puja actual de ${currentBid}$"
                    } else if (amount < currentBid + minimumIncrease) {
                        errorMessage = "La puja debe ser al menos ${minimumIncrease}$ mayor que la puja actual"
                    } else {
                        val newBid = Bid(userId = userId, amount = amount)

                        // Guardar la nueva puja en Firebase bajo la subasta correspondiente
                        bidsDatabase.push().setValue(newBid)

                        // Actualizar el currentBid en el nodo principal de la subasta
                        auctionRef.child("currentBid").setValue(amount)

                        // Limpiar el campo después de la puja
                        bidAmount = ""
                        errorMessage = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Puja")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección del historial de pujas
            Text(text = "Historial de Pujas", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                bidHistory.forEach { bid ->
                    BidItem(bid = bid)
                }
            }
        }
    }
}


@Composable
fun BidItem(bid: Bid) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colors.surface, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        Text(text = bid.userId, fontWeight = FontWeight.Bold)
        Text(text = "${bid.amount}$", fontSize = 16.sp)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateString = dateFormat.format(Date(bid.timestamp))
        Text(text = dateString, fontSize = 14.sp, color = Color.Gray)
    }
}

// Función para enviar la notificación local
fun sendNotification(context: Context, title: String, message: String) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Crear un canal de notificación (requerido para Android 8.0 y superior)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "auction_channel",
            "Subasta Notificaciones",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    // Crear la notificación
    val notification: Notification = NotificationCompat.Builder(context, "auction_channel")
        .setSmallIcon(android.R.drawable.ic_notification_overlay)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    // Mostrar la notificación
    notificationManager.notify(1, notification)
}