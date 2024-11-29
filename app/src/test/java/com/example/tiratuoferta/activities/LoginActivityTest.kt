package com.example.tiratuoferta.activities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivityUnitTest {

    // Prueba 1: Verifica que un email inválido devuelve el mensaje de error adecuado
    @Test
    fun `email is invalid`() {
        // Se valida un email con formato incorrecto
        val result = validateLogin("invalid-email", "password123")
        // Se verifica que el mensaje de error sea el esperado para un email no válido
        assertEquals("Invalid email format", result)
    }

    // Prueba 2: Verifica que una contraseña vacía devuelve el mensaje de error adecuado
    @Test
    fun `password is empty`() {
        // Se valida que la contraseña esté vacía
        val result = validateLogin("test@example.com", "")
        // Se verifica que el mensaje de error sea el esperado para una contraseña vacía
        assertEquals("Password cannot be empty", result)
    }

    // Prueba 3: Verifica que un email vacío devuelve el mensaje de error adecuado
    @Test
    fun `email is empty`() {
        // Se valida que el email esté vacío
        val result = validateLogin("", "password123")
        // Se verifica que el mensaje de error sea el esperado para un email vacío
        assertEquals("Email cannot be empty", result)
    }

    // Prueba 4: Verifica que un login exitoso se maneje correctamente
    @Test
    fun `login successful`() {
        // Se crea un mock de FirebaseAuth y FirebaseUser para simular un login exitoso
        val mockAuth = mock(FirebaseAuth::class.java)
        val mockUser = mock(FirebaseUser::class.java)
        // Se simula que el usuario autenticado es el mock de FirebaseUser
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        // Se valida que el login sea exitoso
        val result = loginWithFirebase(mockAuth, "test@example.com", "password123")
        // Se verifica que el resultado sea true, lo que indica un login exitoso
        assertTrue(result)
    }

    // Prueba 5: Verifica que un login con credenciales incorrectas falle
    @Test
    fun `login fails with wrong credentials`() {
        // Se crea un mock de FirebaseAuth
        val mockAuth = mock(FirebaseAuth::class.java)
        // Se simula que el login con credenciales incorrectas lanza una excepción
        `when`(mockAuth.signInWithEmailAndPassword("wrong@example.com", "wrongpassword"))
            .thenThrow(RuntimeException("Firebase error"))
        // Se valida que el login falle con las credenciales incorrectas
        val result = loginWithFirebase(mockAuth, "wrong@example.com", "wrongpassword")
        // Se verifica que el resultado sea false, lo que indica que el login falló
        assertFalse(result)
    }

    // Prueba 6: Verifica que el login de Firebase con credenciales correctas sea exitoso
    @Test
    fun `Firebase login succeeds`() {
        // Se crea un mock de FirebaseAuth
        val mockAuth = mock(FirebaseAuth::class.java)
        // Se simula que el login con las credenciales correctas tenga éxito
        `when`(mockAuth.signInWithEmailAndPassword("test@example.com", "password123"))
            .thenReturn(mock())
        // Se valida que el login sea exitoso
        val result = loginWithFirebase(mockAuth, "test@example.com", "password123")
        // Se verifica que el resultado sea true, lo que indica un login exitoso
        assertTrue(result)
    }

    // Prueba 7: Verifica que el login de Firebase con credenciales correctas falle por un error
    @Test
    fun `Firebase login fails`() {
        // Se crea un mock de FirebaseAuth
        val mockAuth = mock(FirebaseAuth::class.java)
        // Se simula que el login con las credenciales correctas falle por un error
        `when`(mockAuth.signInWithEmailAndPassword("test@example.com", "password123"))
            .thenThrow(RuntimeException("Login error"))
        // Se valida que el login falle con las credenciales correctas
        val result = loginWithFirebase(mockAuth, "test@example.com", "password123")
        // Se verifica que el resultado sea false, lo que indica que el login falló
        assertFalse(result)
    }

    // Prueba 8: Verifica que el email y la contraseña sean válidos
    @Test
    fun `email and password are valid`() {
        // Se valida un login con un email y una contraseña válidos
        val result = validateLogin("test@example.com", "password123")
        // Se verifica que el resultado sea "Valid" para indicar que los datos son correctos
        assertEquals("Valid", result)
    }

    // Helpers para validación y simulación de login con Firebase

    // Función para validar el formato del email y la contraseña antes de intentar el login
    private fun validateLogin(email: String, password: String): String {
        return when {
            email.isBlank() -> "Email cannot be empty" // Si el email está vacío
            !email.contains("@") -> "Invalid email format" // Si el email no tiene formato correcto
            password.isBlank() -> "Password cannot be empty" // Si la contraseña está vacía
            else -> "Valid" // Si todo es válido
        }
    }

    // Función para intentar el login con Firebase
    private fun loginWithFirebase(auth: FirebaseAuth, email: String, password: String): Boolean {
        return try {
            // Se intenta realizar el login con Firebase
            auth.signInWithEmailAndPassword(email, password)
            true // Si el login tiene éxito, devuelve true
        } catch (e: Exception) {
            false // Si ocurre una excepción (error), devuelve false
        }
    }
}
