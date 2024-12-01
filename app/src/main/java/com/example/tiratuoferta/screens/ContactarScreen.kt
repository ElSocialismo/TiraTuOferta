package com.example.tiratuoferta.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun ContactarScreen() {
    val context = LocalContext.current // Obtener el contexto local
    val name = remember { mutableStateOf(TextFieldValue()) }
    val email = remember { mutableStateOf(TextFieldValue()) }
    val message = remember { mutableStateOf(TextFieldValue()) }

    // Estado para mostrar el mensaje de éxito
    val showToast = remember { mutableStateOf(false) }

    // Mostrar el toast usando LaunchedEffect cuando se active el estado showToast
    LaunchedEffect(showToast.value) {
        if (showToast.value) {
            Toast.makeText(context, "Mensaje enviado", Toast.LENGTH_SHORT).show()
            showToast.value = false // Resetear el estado del Toast
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5)) // Fondo suave para la pantalla
    ) {
        Text(
            text = "Contacto",
            style = MaterialTheme.typography.h4,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "¿Tienes alguna duda o sugerencia? ¡Contáctanos!",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Campo de nombre
        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Nombre") },
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = "Person Icon")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6200EE),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de correo electrónico
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Correo Electrónico") },
            leadingIcon = {
                Icon(Icons.Filled.Email, contentDescription = "Email Icon")
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6200EE),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de mensaje
        OutlinedTextField(
            value = message.value,
            onValueChange = { message.value = it },
            label = { Text("Mensaje") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp), // Tamaño más grande para el mensaje
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6200EE),
                unfocusedBorderColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* Manejo de acción Done, por ejemplo, ocultar teclado */ }
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de enviar
        Button(
            onClick = {
                showToast.value = true // Activar la variable para mostrar el Toast
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE))
        ) {
            Text(
                text = "Enviar Mensaje",
                style = MaterialTheme.typography.button,
                color = Color.White
            )
        }
    }
}
