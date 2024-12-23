package com.example.tiratuoferta.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiratuoferta.ui.theme.TiraTuOfertaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        setContent {
            TiraTuOfertaTheme {
                RegisterScreen(onRegisterSuccess = {
                    // Mostrar un mensaje de registro exitoso
                    Toast.makeText(this, "Registro exitoso. Redirigiendo al login...", Toast.LENGTH_SHORT).show()

                    // Redirigir al Login después de registrarse
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var registerResult by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val dbRef = FirebaseDatabase.getInstance().reference

    val backgroundColor = Color(0xFFF1F1F1) // Fondo suave

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                color = Color(0xFF6200EE),
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Campo de correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de nombre completo
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de teléfono
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de DNI
            OutlinedTextField(
                value = dni,
                onValueChange = { dni = it },
                label = { Text("DNI") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirmar contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Aceptar términos
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6200EE))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Accept terms", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de registro
            Button(
                onClick = {
                    if (password != confirmPassword) {
                        registerResult = "Passwords do not match"
                    } else if (!termsAccepted) {
                        registerResult = "You must accept the terms to proceed"
                    } else if (phoneNumber.length < 9) {
                        registerResult = "Phone number must have at least 9 digits"
                    } else if (dni.length != 8) {
                        registerResult = "DNI must be 8 digits"
                    } else {
                        // Crear usuario con Firebase Auth
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid
                                    val user = mapOf(
                                        "fullName" to fullName,
                                        "email" to email,
                                        "phoneNumber" to phoneNumber,
                                        "dni" to dni
                                    )

                                    if (userId != null) {
                                        dbRef.child("users").child(userId).setValue(user)
                                            .addOnSuccessListener {
                                                registerResult = "Registration successful!"

                                                // Crear el chat con otro usuario
                                                val otherUserId = "someOtherUserId" // Aquí puedes poner el ID de un usuario existente o predeterminado
                                                createChat(userId, otherUserId) // Crea un chat único entre los dos usuarios

                                                onRegisterSuccess() // Redirige o realiza la acción
                                            }
                                            .addOnFailureListener { e ->
                                                registerResult = "Registration failed: ${e.message}"
                                            }
                                    } else {
                                        registerResult = "Registration failed: User ID is null"
                                    }
                                } else {
                                    registerResult = "Registration failed: ${task.exception?.message}"
                                }
                            }


                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF6200EE))
            ) {
                Text(text = "Sign Up", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar el mensaje de resultado
            if (registerResult.isNotEmpty()) {
                Text(
                    text = registerResult,
                    color = if (registerResult.contains("successful")) Color.Green else Color.Red,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                )
            }
        }
    }
}
// Función para crear un chat entre dos usuarios
fun createChat(userId1: String, userId2: String) {
    // Crear el ID del chat basado en los IDs de los usuarios
    val chatId = if (userId1 < userId2) "$userId1-$userId2" else "$userId2-$userId1"

    // Crear una referencia en la base de datos de Firebase
    val dbRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId)

    // Crear un objeto de chat con los detalles de los usuarios
    val chat = mapOf(
        "user1" to userId1,
        "user2" to userId2,
        "createdAt" to System.currentTimeMillis()
    )

    // Guardar el chat en Firebase
    dbRef.setValue(chat).addOnSuccessListener {
        Log.d("Chat", "Chat creado con ID: $chatId")
    }.addOnFailureListener { e ->
        Log.e("Chat", "Error al crear el chat: ${e.message}")
    }
}

