package com.example.tiratuoferta.activities

import android.net.Uri
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.navigation.NavController
import com.example.tiratuoferta.models.Auction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateAuctionScreen(navController: NavController, saveAuction: (Auction) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startingPrice by remember { mutableStateOf("") }
    var minimumIncrease by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Lista de categorías predefinidas
    val categories = listOf("Electronica", "Ropa", "Juguetes", "Automoviles", "Hogar")

    // Launcher para seleccionar una imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            uploadImageToFirebase(it) { downloadUrl -> imageUrl = downloadUrl }
        }
    }

    // Estado para abrir o cerrar el DropdownMenu
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),  // Agregar scroll vertical
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Título y Descripción: Grupo de Información General
        Section(title = "Información General") {
            // Título
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título de la subasta") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
        }

        // Precio y Incremento: Grupo de Precios
        Section(title = "Precios") {
            // Precio inicial
            OutlinedTextField(
                value = startingPrice,
                onValueChange = { startingPrice = it },
                label = { Text("Precio inicial") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                shape = MaterialTheme.shapes.medium
            )
            // Incremento mínimo
            OutlinedTextField(
                value = minimumIncrease,
                onValueChange = { minimumIncrease = it },
                label = { Text("Incremento mínimo") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                shape = MaterialTheme.shapes.medium
            )
        }

        // Categoría
        Section(title = "Categoría") {
            OutlinedTextField(
                value = selectedCategory ?: "Seleccionar Categoría",
                onValueChange = {},
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Expandir categorías")
                    }
                },
                shape = MaterialTheme.shapes.medium
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(onClick = {
                        selectedCategory = category
                        expanded = false
                    }) {
                        Text(text = category)
                    }
                }
            }
        }

        // Imagen
        Section(title = "Imagen") {
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            ) {
                Text("Subir Imagen", color = Color.White)
            }
            selectedImageUri?.let {
                Text(text = "Imagen seleccionada", color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
            }
        }

        // Fecha y Hora
        Section(title = "Fecha de finalización") {
            Button(
                onClick = { showDateTimePicker(context) { dateInMillis -> selectedDate = dateInMillis } },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            ) {
                Text("Seleccionar fecha", color = Color.White)
            }
            selectedDate?.let {
                Text(text = "Fecha seleccionada: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)}")
            }
        }

        // Botón de creación de subasta
        Section(title = "") {
            Button(
                onClick = {
                    // Validaciones
                    if (title.isBlank()) {
                        errorMessage = "El título no puede estar vacío"
                        showError = true
                    } else if (description.isBlank()) {
                        errorMessage = "La descripción no puede estar vacía"
                        showError = true
                    } else if (startingPrice.isBlank() || startingPrice.toDoubleOrNull() == null) {
                        errorMessage = "El precio inicial debe ser un número válido"
                        showError = true
                    } else if (minimumIncrease.isBlank() || minimumIncrease.toDoubleOrNull() == null) {
                        errorMessage = "El incremento mínimo debe ser un número válido"
                        showError = true
                    } else if (selectedCategory.isNullOrEmpty()) {
                        errorMessage = "Debe seleccionar una categoría"
                        showError = true
                    } else if (imageUrl == null) {
                        errorMessage = "Debe seleccionar una imagen"
                        showError = true
                    } else if (selectedDate == null || selectedDate!! < System.currentTimeMillis()) {
                        errorMessage = "La fecha de finalización debe ser posterior a la fecha actual"
                        showError = true
                    } else {
                        showError = false
                        // Crear la subasta si todo es válido
                        if (currentUser != null) {
                            val auction = Auction(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                description = description,
                                imageUrl = imageUrl ?: "",
                                startingPrice = startingPrice.toDoubleOrNull() ?: 0.0,
                                minimumIncrease = minimumIncrease.toDoubleOrNull() ?: 0.0,
                                endTime = selectedDate ?: 0L,
                                userId = currentUser.uid,
                                startTime = System.currentTimeMillis(),
                                category = selectedCategory ?: ""
                            )
                            saveAuction(auction)
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
            ) {
                Text("Crear Subasta", color = Color.White)
            }

            // Mostrar mensaje de error si alguna validación falla
            if (showError) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        content()
    }
}

fun uploadImageToFirebase(uri: Uri, onSuccess: (String) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                // Llamar onSuccess con la URL de la imagen
                onSuccess(downloadUrl.toString())
            }
        }
        .addOnFailureListener {
            Log.d("UploadImage", "Error al subir la imagen: ${it.message}")
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
            onDateTimeSelected(calendar.timeInMillis)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
}