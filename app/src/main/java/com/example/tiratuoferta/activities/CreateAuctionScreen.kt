package com.example.tiratuoferta.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.tiratuoferta.models.Auction // Asegúrate de que esta línea apunta al paquete correcto
import java.util.UUID
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.Calendar
import androidx.compose.ui.platform.LocalContext

@Composable
fun CreateAuctionScreen(navController: NavController, saveAuction: (Auction) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startingPrice by remember { mutableStateOf("") }
    var minimumIncrease by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current // Obtener el contexto aquí

    // Launcher para seleccionar una imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            uploadImageToFirebase(it) { downloadUrl ->
                imageUrl = downloadUrl
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título de la subasta") }
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") }
        )

        TextField(
            value = startingPrice,
            onValueChange = { startingPrice = it },
            label = { Text("Precio inicial") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = minimumIncrease,
            onValueChange = { minimumIncrease = it },
            label = { Text("Incremento mínimo") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Subir Imagen")
        }

        selectedImageUri?.let {
            Text(text = "Imagen seleccionada")
        }

        Button(onClick = { showDateTimePicker(context) { dateInMillis -> selectedDate = dateInMillis } }) {
            Text("Seleccionar fecha de finalización")
        }

        selectedDate?.let {
            Text(text = "Fecha seleccionada: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(it)}")
        }

        Button(
            onClick = {
                val auction = Auction(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    imageUrl = imageUrl ?: "",
                    startingPrice = startingPrice.toDoubleOrNull() ?: 0.0,
                    minimumIncrease = minimumIncrease.toDoubleOrNull() ?: 0.0,
                    endTime = selectedDate ?: 0L,
                    userId = "ID_DEL_USUARIO",
                    startTime = System.currentTimeMillis() // Cambia esto por el ID del usuario actual
                )
                saveAuction(auction)
                navController.popBackStack()
            }
        ) {
            Text("Crear Subasta")
        }
    }
}

// Función para subir la imagen a Firebase Storage y obtener la URL de descarga
fun uploadImageToFirebase(uri: Uri, onSuccess: (String) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                onSuccess(downloadUrl.toString())
            }
        }
        .addOnFailureListener {
            // Maneja el error
        }
}

fun showDateTimePicker(context: Context, onDateTimeSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(context, { _, year, month, day ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        TimePickerDialog(context, { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            // Retornar el tiempo en milisegundos
            onDateTimeSelected(calendar.timeInMillis)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
}
