package com.example.tiratuoferta.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.tiratuoferta.models.Auction
import com.example.tiratuoferta.models.Bid
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@Composable
fun EditAuctionScreen(navController: NavHostController, auctionId: String) {
    // Variables para los campos de la subasta
    var auctionTitle by remember { mutableStateOf(TextFieldValue("")) }
    var auctionDescription by remember { mutableStateOf(TextFieldValue("")) }
    var auctionStartingPrice by remember { mutableStateOf(TextFieldValue("")) }
    var auctionCategory by remember { mutableStateOf(TextFieldValue("")) }
    var auctionEndTime by remember { mutableStateOf(TextFieldValue("")) }

    // Recuperar los datos de la subasta de Firebase
    val database = FirebaseDatabase.getInstance()
    val auctionRef = database.getReference("auctions").child(auctionId)

    // Cargar los datos de la subasta al iniciar la pantalla
    LaunchedEffect(auctionId) {
        auctionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val auction = snapshot.getValue(Auction::class.java)
                if (auction != null) {
                    auctionTitle = TextFieldValue(auction.title)
                    auctionDescription = TextFieldValue(auction.description)
                    auctionStartingPrice = TextFieldValue(auction.startingPrice.toString())
                    auctionCategory = TextFieldValue(auction.category)
                    auctionEndTime = TextFieldValue(auction.endTime.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores si no se puede recuperar la subasta
                Toast.makeText(navController.context, "Error al cargar la subasta", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Interfaz de usuario para editar la subasta
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Editar Subasta", style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para el título de la subasta
        Text("Título de la subasta")
        BasicTextField(
            value = auctionTitle,
            onValueChange = { auctionTitle = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para la descripción de la subasta
        Text("Descripción de la subasta")
        BasicTextField(
            value = auctionDescription,
            onValueChange = { auctionDescription = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para el precio inicial de la subasta
        Text("Precio inicial")
        BasicTextField(
            value = auctionStartingPrice,
            onValueChange = { auctionStartingPrice = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para la categoría de la subasta
        Text("Categoría")
        BasicTextField(
            value = auctionCategory,
            onValueChange = { auctionCategory = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para el tiempo de finalización de la subasta
        Text("Tiempo de finalización (en milisegundos)")
        BasicTextField(
            value = auctionEndTime,
            onValueChange = { auctionEndTime = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para guardar los cambios
        Button(onClick = { saveAuctionChanges(navController, auctionId, auctionTitle.text, auctionDescription.text, auctionStartingPrice.text.toDouble(), auctionCategory.text, auctionEndTime.text.toLong()) }) {
            Text("Guardar Cambios")
        }
    }
}

// Función para guardar los cambios en Firebase
fun saveAuctionChanges(navController: NavHostController, auctionId: String, title: String, description: String, startingPrice: Double, category: String, endTime: Long) {
    val database = FirebaseDatabase.getInstance()
    val auctionRef = database.getReference("auctions").child(auctionId)

    // Recuperar el userId actual para no perder la relación con el usuario
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Crear un objeto de subasta con los nuevos datos
    val updatedAuction = Auction(
        id = auctionId,
        title = title,
        description = description,
        imageUrl = "", // Puedes manejar la actualización de la imagen si es necesario
        category = category,
        startingPrice = startingPrice,
        currentBid = startingPrice, // Se puede establecer al valor inicial
        minimumIncrease = 1.0, // Puedes definir un valor por defecto
        endTime = endTime,
        userId = userId, // Asegúrate de no perder el userId
        participants = mutableMapOf(), // Ajusta según tu implementación
        favorite = false,
        startTime = System.currentTimeMillis(),
        bids = mutableMapOf() // Puedes actualizar las pujas si es necesario
    )

    // Guardar los cambios en Firebase
    auctionRef.setValue(updatedAuction)
        .addOnSuccessListener {
            Toast.makeText(navController.context, "Subasta actualizada exitosamente", Toast.LENGTH_SHORT).show()
            navController.popBackStack() // Volver a la pantalla anterior
        }
        .addOnFailureListener {
            Toast.makeText(navController.context, "Error al actualizar la subasta", Toast.LENGTH_SHORT).show()
        }
}
