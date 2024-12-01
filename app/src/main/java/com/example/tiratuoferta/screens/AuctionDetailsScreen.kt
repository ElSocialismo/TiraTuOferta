package com.example.tiratuoferta.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.tiratuoferta.models.Auction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun AuctionDetailsScreen(navController: NavController, auctionId: String) {
    var auction by remember { mutableStateOf<Auction?>(null) }
    var timeRemaining by remember { mutableStateOf("00 días 00 horas 00 minutos 00 segundos") }

    // Referencia a la subasta en Firebase
    val auctionRef = FirebaseDatabase.getInstance().getReference("auctions").child(auctionId)

    // Listener para obtener los detalles de la subasta y actualizarlos en tiempo real
    LaunchedEffect(auctionId) {
        auctionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                auction = snapshot.getValue(Auction::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })
    }

    // Contador de tiempo real
    LaunchedEffect(auction?.endTime) {
        while (auction?.endTime != null) {
            val timeLeft = auction!!.endTime - System.currentTimeMillis()
            if (timeLeft > 0) {
                val days = TimeUnit.MILLISECONDS.toDays(timeLeft)
                val hours = TimeUnit.MILLISECONDS.toHours(timeLeft) % 24
                val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % 60
                timeRemaining = "%02d días %02d horas %02d minutos %02d segundos".format(days, hours, minutes, seconds)
                delay(1000L)
            } else {
                timeRemaining = "00 días 00 horas 00 minutos 00 segundos"
                break
            }
        }
    }

    auction?.let { auction ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Contenido principal de la pantalla (AuctionDetailsScreen) aquí
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Título con botones de retroceso y favorito
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = android.R.drawable.ic_media_previous), contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Detalles", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        // Marcar o desmarcar como favorito
                        val updatedAuction = auction.copy(favorite = !auction.favorite)
                        auctionRef.child("favorite").setValue(updatedAuction.favorite)

                        // Agregar o quitar de la lista de favoritos del usuario
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@IconButton
                        val favoritesRef = FirebaseDatabase.getInstance().getReference("users/$userId/favorites")
                        if (updatedAuction.favorite) {
                            favoritesRef.child(auctionId).setValue(true)  // Agregar a favoritos
                        } else {
                            favoritesRef.child(auctionId).removeValue()  // Eliminar de favoritos
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = if (auction.favorite) android.R.drawable.star_on else android.R.drawable.star_off),
                            contentDescription = "Favorite"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Imagen de la subasta con borde redondeado y sombra
                Image(
                    painter = rememberImagePainter(data = auction.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(12.dp))
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Título
                Text(
                    text = auction.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Contador de tiempo restante
                Text(
                    text = timeRemaining,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Precios
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Precio inicial: ${auction.startingPrice}$",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Puja actual: ${auction.currentBid}$",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = auction.description,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de realizar puja con un diseño más llamativo
                Button(
                    onClick = { navController.navigate("placeBid/${auctionId}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE))
                ) {
                    Text(text = "Realizar Puja", fontSize = 18.sp, color = Color.White)
                }
            }

            // Floating Action Button para Chat
            FloatingActionButton(
                onClick = {
                    // Acción para abrir el chat
                    navController.navigate("chat/${auctionId}")
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart), // Coloca el botón en la parte inferior izquierda
                backgroundColor = Color(0xFF6200EE) // Color del botón flotante
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_send),
                    contentDescription = "Chat",
                    tint = Color.White
                )
            }
        }
    } ?: run {
        // Loading indicator
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
