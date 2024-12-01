package com.example.tiratuoferta.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AyudaScreen() {
    // Crear un scrollable para la pantalla
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)  // Permitir el desplazamiento vertical
    ) {
        // Título
        Text(
            text = "Centro de Ayuda",
            style = MaterialTheme.typography.h5.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colors.primary
        )

        // Descripción
        Text(
            text = "Aquí encontrarás respuestas a las preguntas más frecuentes y guías para ayudarte a usar la aplicación.",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Botones con espaciado y diseño
        Button(
            onClick = { /* Navegar a la sección de FAQ */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
        ) {
            Text(text = "Preguntas Frecuentes", color = Color.White)
        }

        Button(
            onClick = { /* Navegar a la sección de contacto */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
        ) {
            Text(text = "Contacto con Soporte", color = Color.White)
        }

        Button(
            onClick = { /* Mostrar guía paso a paso */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
        ) {
            Text(text = "Guías de Uso", color = Color.Black)
        }

        // Alineación y espaciado en la parte inferior
        Spacer(modifier = Modifier.weight(1f))  // Para empujar los botones hacia la parte inferior
    }
}
