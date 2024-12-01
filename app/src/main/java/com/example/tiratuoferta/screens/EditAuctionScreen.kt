package com.example.tiratuoferta.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAuctionScreen(navController: NavHostController, auctionId: String) {
    // Variables para los campos de la subasta
    var auctionTitle by remember { mutableStateOf(TextFieldValue("")) }
    var auctionDescription by remember { mutableStateOf(TextFieldValue("")) }
    var auctionStartingPrice by remember { mutableStateOf(TextFieldValue("")) }
    var auctionCategory by remember { mutableStateOf(TextFieldValue("")) }
    var auctionEndTime by remember { mutableStateOf(TextFieldValue("")) }
    var auctionImageUrl by remember { mutableStateOf("") } // Variable para almacenar la imagen

    val context = LocalContext.current

    // Recuperar los datos de la subasta de Firebase
    val database = FirebaseDatabase.getInstance()
    val auctionRef = database.getReference("auctions").child(auctionId)

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
                    auctionImageUrl = auction.imageUrl
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al cargar la subasta", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Subasta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Imagen de la subasta (si está disponible)
            if (auctionImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = auctionImageUrl,
                    contentDescription = "Imagen de la subasta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Campos de edición
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    EditableField(
                        label = "Título de la subasta",
                        value = auctionTitle,
                        onValueChange = { auctionTitle = it }
                    )
                }

                item {
                    EditableField(
                        label = "Descripción de la subasta",
                        value = auctionDescription,
                        onValueChange = { auctionDescription = it },
                        maxLines = 4
                    )
                }

                item {
                    EditableField(
                        label = "Precio inicial",
                        value = auctionStartingPrice,
                        onValueChange = { auctionStartingPrice = it },
                        keyboardType = KeyboardType.Number
                    )
                }

                item {
                    EditableField(
                        label = "Categoría",
                        value = auctionCategory,
                        onValueChange = { auctionCategory = it }
                    )
                }

                item {
                    EditableField(
                        label = "Tiempo de finalización (en milisegundos)",
                        value = auctionEndTime,
                        onValueChange = { auctionEndTime = it },
                        keyboardType = KeyboardType.Number
                    )
                }

                item {
                    // Botón para guardar los cambios
                    Button(
                        onClick = {
                            saveAuctionChanges(
                                navController,
                                auctionId,
                                auctionTitle.text,
                                auctionDescription.text,
                                auctionStartingPrice.text.toDoubleOrNull() ?: 0.0,
                                auctionCategory.text,
                                auctionEndTime.text.toLongOrNull() ?: 0L,
                                auctionImageUrl
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}

@Composable
fun EditableField(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.subtitle1 // Cambiado a `subtitle1` para versiones anteriores
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            maxLines = maxLines,
            textStyle = MaterialTheme.typography.body1, // Cambiado a `body1`
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

// Función para guardar los cambios en Firebase
fun saveAuctionChanges(
    navController: NavHostController,
    auctionId: String,
    title: String,
    description: String,
    startingPrice: Double,
    category: String,
    endTime: Long,
    currentImageUrl: String // Parámetro adicional para la imagen
) {
    val database = FirebaseDatabase.getInstance()
    val auctionRef = database.getReference("auctions").child(auctionId)

    // Recuperar el userId actual para no perder la relación con el usuario
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Crear un objeto de subasta con los nuevos datos
    val updatedAuction = Auction(
        id = auctionId,
        title = title,
        description = description,
        imageUrl = currentImageUrl, // Mantener la URL de la imagen actual
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
