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

    val context = LocalContext.current // Obtener el contexto aquí

    // Lista de categorías predefinidas
    val categories = listOf("Electrónica", "Ropa", "Juguetes", "Automóviles", "Hogar")

    // Launcher para seleccionar una imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            uploadImageToFirebase(it) { downloadUrl ->
                imageUrl = downloadUrl
            }
        }
    }

    // Estado para abrir o cerrar el DropdownMenu
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Campo Título
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título de la subasta") }
        )

        // Campo Descripción
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") }
        )

        // Campo Precio inicial
        TextField(
            value = startingPrice,
            onValueChange = { startingPrice = it },
            label = { Text("Precio inicial") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        // Campo Incremento mínimo
        TextField(
            value = minimumIncrease,
            onValueChange = { minimumIncrease = it },
            label = { Text("Incremento mínimo") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        // Dropdown para seleccionar la categoría
        TextField(
            value = selectedCategory ?: "Seleccionar Categoría",
            onValueChange = {},
            label = { Text("Categoría") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Expandir categorías")
                }
            }
        )

        // Mostrar el DropdownMenu de categorías
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

        // Botón para subir imagen
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Subir Imagen")
        }

        selectedImageUri?.let {
            Text(text = "Imagen seleccionada")
        }

        // Botón para seleccionar fecha y hora
        Button(onClick = { showDateTimePicker(context) { dateInMillis -> selectedDate = dateInMillis } }) {
            Text("Seleccionar fecha de finalización")
        }

        selectedDate?.let {
            Text(text = "Fecha seleccionada: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)}")
        }

        // Botón para crear la subasta
        Button(
            onClick = {
                // Verificar si la imagen está disponible antes de guardar la subasta
                if (imageUrl != null) {
                    val auction = Auction(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        imageUrl = imageUrl ?: "",
                        startingPrice = startingPrice.toDoubleOrNull() ?: 0.0,
                        minimumIncrease = minimumIncrease.toDoubleOrNull() ?: 0.0,
                        endTime = selectedDate ?: 0L,
                        userId = "ID_DEL_USUARIO", // Aquí puedes reemplazar por el ID del usuario
                        startTime = System.currentTimeMillis(),
                        category = selectedCategory ?: ""
                    )

                    saveAuction(auction)

                    // Navegar a la pantalla principal
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                } else {
                    // Mostrar un mensaje indicando que se debe seleccionar una imagen primero
                    Log.d("CreateAuction", "No se ha seleccionado una imagen")
                }
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
                // Llamar onSuccess con la URL de la imagen
                onSuccess(downloadUrl.toString())  // Solo se obtiene la URL, no se guarda la subasta
            }
        }
        .addOnFailureListener {
            // Maneja el error si ocurre
            Log.d("UploadImage", "Error al subir la imagen: ${it.message}")
        }
}

// Función para mostrar el DatePicker y TimePicker
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
