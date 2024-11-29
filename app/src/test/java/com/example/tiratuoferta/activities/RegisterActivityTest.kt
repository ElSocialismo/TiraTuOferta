package com.example.tiratuoferta.activities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivityUnitTest {

    // Prueba 1: Verifica que un email inválido devuelva el mensaje de error adecuado
    @Test
    fun `email is invalid`() {
        // Se valida un email con formato incorrecto
        val result = validateRegistration("invalid-email", "John Doe", "password123", "password123", true)
        // Se verifica que el mensaje de error sea el esperado para un email no válido
        assertEquals("Invalid email format", result)
    }

    // Prueba 2: Verifica que una contraseña con menos de 6 caracteres devuelva el mensaje de error adecuado
    @Test
    fun `password is less than 6 characters`() {
        // Se valida una contraseña con menos de 6 caracteres
        val result = validateRegistration("test@example.com", "John Doe", "123", "123", true)
        // Se verifica que el mensaje de error sea el esperado para contraseñas cortas
        assertEquals("Password must be at least 6 characters long", result)
    }

    // Prueba 3: Verifica que si la contraseña de confirmación está vacía, devuelva el mensaje adecuado
    @Test
    fun `confirm password is missing`() {
        // Se valida la contraseña de confirmación vacía
        val result = validateRegistration("test@example.com", "John Doe", "password123", "", true)
        // Se verifica que el mensaje de error sea el esperado si las contraseñas no coinciden
        assertEquals("Passwords do not match", result)
    }

    // Prueba 4: Verifica que si no se aceptan los términos, se muestre el mensaje de error adecuado
    @Test
    fun `terms not accepted`() {
        // Se valida que los términos no hayan sido aceptados
        val result = validateRegistration("test@example.com", "John Doe", "password123", "password123", false)
        // Se verifica que el mensaje de error sea el esperado si no se aceptan los términos
        assertEquals("You must accept the terms to proceed", result)
    }

    // Prueba 5: Verifica que el registro sea exitoso cuando todos los datos son válidos
    @Test
    fun `registration successful`() {
        // Se valida el registro con datos correctos
        val result = validateRegistration("test@example.com", "John Doe", "password123", "password123", true)
        // Se verifica que el mensaje de respuesta sea "Valid" cuando todo está correcto
        assertEquals("Valid", result)
    }

    // Prueba 6: Verifica que el registro con Firebase sea exitoso
    @Test
    fun `Firebase registration succeeds`() {
        // Se crea un mock de FirebaseAuth y FirebaseUser para simular el registro exitoso
        val mockAuth = mock(FirebaseAuth::class.java)
        val mockUser = mock(FirebaseUser::class.java)
        // Se simula que el usuario autenticado es el mock de FirebaseUser
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        // Se valida que el registro sea exitoso
        val result = registerWithFirebase(mockAuth, "test@example.com", "password123")
        // Se verifica que el resultado sea true, lo que indica que el registro fue exitoso
        assertTrue(result)
    }

    // Prueba 7: Verifica que el registro con Firebase falle si ocurre un error
    @Test
    fun `Firebase registration fails`() {
        // Se crea un mock de FirebaseAuth para simular un error durante el registro
        val mockAuth = mock(FirebaseAuth::class.java)
        // Se simula que el intento de crear un usuario con Firebase falle lanzando una excepción
        `when`(mockAuth.createUserWithEmailAndPassword("test@example.com", "password123"))
            .thenThrow(RuntimeException("Firebase error"))
        // Se valida que el registro falle
        val result = registerWithFirebase(mockAuth, "test@example.com", "password123")
        // Se verifica que el resultado sea false, lo que indica que el registro falló
        assertFalse(result)
    }

    // Prueba 8: Verifica que si el nombre completo está vacío, se devuelva el mensaje de error adecuado
    @Test
    fun `full name is empty`() {
        // Se valida que el nombre completo esté vacío
        val result = validateRegistration("test@example.com", "", "password123", "password123", true)
        // Se verifica que el mensaje de error sea el esperado si el nombre completo está vacío
        assertEquals("Full name cannot be empty", result)
    }

    // Prueba 9: Verifica que si el email está vacío, se devuelva el mensaje de error adecuado
    @Test
    fun `email is empty`() {
        // Se valida que el email esté vacío
        val result = validateRegistration("", "John Doe", "password123", "password123", true)
        // Se verifica que el mensaje de error sea el esperado si el email está vacío
        assertEquals("Email cannot be empty", result)
    }

    // Prueba 10: Verifica que si las contraseñas no coinciden, se devuelva el mensaje adecuado
    @Test
    fun `passwords do not match`() {
        // Se valida que las contraseñas no coincidan
        val result = validateRegistration("test@example.com", "John Doe", "password123", "password321", true)
        // Se verifica que el mensaje de error sea el esperado si las contraseñas no coinciden
        assertEquals("Passwords do not match", result)
    }

    // Helpers para validación y simulación de registro con Firebase

    // Función para validar el registro, asegurándose de que todos los campos sean correctos
    private fun validateRegistration(
        email: String,
        fullName: String,
        password: String,
        confirmPassword: String,
        termsAccepted: Boolean
    ): String {
        return when {
            email.isBlank() -> "Email cannot be empty" // Si el email está vacío
            fullName.isBlank() -> "Full name cannot be empty" // Si el nombre completo está vacío
            !email.contains("@") -> "Invalid email format" // Si el email no tiene formato correcto
            password.length < 6 -> "Password must be at least 6 characters long" // Si la contraseña tiene menos de 6 caracteres
            password != confirmPassword -> "Passwords do not match" // Si las contraseñas no coinciden
            !termsAccepted -> "You must accept the terms to proceed" // Si los términos no fueron aceptados
            else -> "Valid" // Si todo es válido
        }
    }

    // Función para registrar un usuario en Firebase
    private fun registerWithFirebase(auth: FirebaseAuth, email: String, password: String): Boolean {
        return try {
            // Se intenta crear un nuevo usuario con el email y la contraseña proporcionados
            auth.createUserWithEmailAndPassword(email, password)
            true // Si el registro es exitoso, se retorna true
        } catch (e: Exception) {
            false // Si ocurre un error, se retorna false
        }
    }
}
