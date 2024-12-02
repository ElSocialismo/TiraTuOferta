@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tiratuoferta.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun TiraTuOfertaLogin(
    onRegisterClicked: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginResult by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    // Colores personalizados
    val primaryColor = Color(0xFF00695C)
    val secondaryColor = Color(0xFFFF7043)
    val backgroundColor = Color(0xFFECEFF1)
    val textColor = Color(0xFF37474F)
    val accentColor = Color(0xFFFDD835)

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título principal
            Text(
                text = "Tira Tu Oferta",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                modifier = Modifier.padding(bottom = 40.dp),
                color = primaryColor
            )

            // Campo de correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = textColor) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = primaryColor,
                    cursorColor = accentColor
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = textColor) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = primaryColor,
                    cursorColor = accentColor
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de login
            Button(
                onClick = {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                loginResult = "Login exitoso"
                                onLoginSuccess()
                            } else {
                                loginResult = "Fallo en el login: ${task.exception?.message}"
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(primaryColor)
            ) {
                Text(text = "Login", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto para registrarse
            TextButton(onClick = onRegisterClicked) {
                Text(
                    text = "¿No tienes cuenta? Registrate",
                    style = MaterialTheme.typography.bodyMedium.copy(color = secondaryColor)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje de resultado
            if (loginResult.isNotEmpty()) {
                Text(
                    text = loginResult,
                    color = if (loginResult.contains("exitoso")) primaryColor else secondaryColor,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                )
            }
        }
    }
}
