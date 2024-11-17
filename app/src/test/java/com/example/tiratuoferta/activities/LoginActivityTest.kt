package com.example.tiratuoferta.activities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivityUnitTest {

    @Test
    fun `email is invalid`() {
        val result = validateLogin("invalid-email", "password123")
        assertEquals("Invalid email format", result)
    }

    @Test
    fun `password is empty`() {
        val result = validateLogin("test@example.com", "")
        assertEquals("Password cannot be empty", result)
    }

    @Test
    fun `email is empty`() {
        val result = validateLogin("", "password123")
        assertEquals("Email cannot be empty", result)
    }

    @Test
    fun `login successful`() {
        val mockAuth = mock(FirebaseAuth::class.java)
        val mockUser = mock(FirebaseUser::class.java)
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        val result = loginWithFirebase(mockAuth, "test@example.com", "password123")
        assertTrue(result)
    }

    @Test
    fun `login fails with wrong credentials`() {
        val mockAuth = mock(FirebaseAuth::class.java)
        `when`(mockAuth.signInWithEmailAndPassword("wrong@example.com", "wrongpassword"))
            .thenThrow(RuntimeException("Firebase error"))
        val result = loginWithFirebase(mockAuth, "wrong@example.com", "wrongpassword")
        assertFalse(result)
    }

    @Test
    fun `Firebase login succeeds`() {
        val mockAuth = mock(FirebaseAuth::class.java)
        `when`(mockAuth.signInWithEmailAndPassword("test@example.com", "password123"))
            .thenReturn(mock())
        val result = loginWithFirebase(mockAuth, "test@example.com", "password123")
        assertTrue(result)
    }

    @Test
    fun `Firebase login fails`() {
        val mockAuth = mock(FirebaseAuth::class.java)
        `when`(mockAuth.signInWithEmailAndPassword("test@example.com", "password123"))
            .thenThrow(RuntimeException("Login error"))
        val result = loginWithFirebase(mockAuth, "test@example.com", "password123")
        assertFalse(result)
    }

    @Test
    fun `email and password are valid`() {
        val result = validateLogin("test@example.com", "password123")
        assertEquals("Valid", result)
    }

    // Helpers for validation and Firebase simulation

    private fun validateLogin(email: String, password: String): String {
        return when {
            email.isBlank() -> "Email cannot be empty"
            !email.contains("@") -> "Invalid email format"
            password.isBlank() -> "Password cannot be empty"
            else -> "Valid"
        }
    }

    private fun loginWithFirebase(auth: FirebaseAuth, email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password)
            true
        } catch (e: Exception) {
            false
        }
    }
}
