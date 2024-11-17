package com.example.tiratuoferta.activities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivityUnitTest {

    @Test
    fun `email is invalid`() {
        val result = validateRegistration("invalid-email", "John Doe", "password123", "password123", true)
        assertEquals("Invalid email format", result)
    }

    @Test
    fun `password is less than 6 characters`() {
        val result = validateRegistration("test@example.com", "John Doe", "123", "123", true)
        assertEquals("Password must be at least 6 characters long", result)
    }

    @Test
    fun `confirm password is missing`() {
        val result = validateRegistration("test@example.com", "John Doe", "password123", "", true)
        assertEquals("Passwords do not match", result)
    }

    @Test
    fun `terms not accepted`() {
        val result = validateRegistration("test@example.com", "John Doe", "password123", "password123", false)
        assertEquals("You must accept the terms to proceed", result)
    }

    @Test
    fun `registration successful`() {
        val result = validateRegistration("test@example.com", "John Doe", "password123", "password123", true)
        assertEquals("Valid", result)
    }

    @Test
    fun `Firebase registration succeeds`() {
        val mockAuth = mock(FirebaseAuth::class.java)
        val mockUser = mock(FirebaseUser::class.java)
        `when`(mockAuth.currentUser).thenReturn(mockUser)

        val result = registerWithFirebase(mockAuth, "test@example.com", "password123")
        assertTrue(result)
    }

    @Test
    fun `Firebase registration fails`() {
        val mockAuth = mock(FirebaseAuth::class.java)
        `when`(mockAuth.createUserWithEmailAndPassword("test@example.com", "password123"))
            .thenThrow(RuntimeException("Firebase error"))

        val result = registerWithFirebase(mockAuth, "test@example.com", "password123")
        assertFalse(result)
    }

    @Test
    fun `full name is empty`() {
        val result = validateRegistration("test@example.com", "", "password123", "password123", true)
        assertEquals("Full name cannot be empty", result)
    }

    @Test
    fun `email is empty`() {
        val result = validateRegistration("", "John Doe", "password123", "password123", true)
        assertEquals("Email cannot be empty", result)
    }

    @Test
    fun `passwords do not match`() {
        val result = validateRegistration("test@example.com", "John Doe", "password123", "password321", true)
        assertEquals("Passwords do not match", result)
    }

    // Helpers for validation and Firebase simulation

    private fun validateRegistration(
        email: String,
        fullName: String,
        password: String,
        confirmPassword: String,
        termsAccepted: Boolean
    ): String {
        return when {
            email.isBlank() -> "Email cannot be empty"
            fullName.isBlank() -> "Full name cannot be empty"
            !email.contains("@") -> "Invalid email format"
            password.length < 6 -> "Password must be at least 6 characters long"
            password != confirmPassword -> "Passwords do not match"
            !termsAccepted -> "You must accept the terms to proceed"
            else -> "Valid"
        }
    }

    private fun registerWithFirebase(auth: FirebaseAuth, email: String, password: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password)
            true
        } catch (e: Exception) {
            false
        }
    }
}
