package com.example.tiratuoferta.activities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class CreateAuctionUnitTest {

    @Test
    fun `validar titulo vacio`() {
        val result = validateAuction(
            title = "",
            description = "Descripción válida",
            startingPrice = "10",
            minimumIncrease = "1",
            imageUrl = "https://image.url",
            endTime = System.currentTimeMillis()
        )
        assertEquals("El título es obligatorio", result)
    }

    @Test
    fun `validar descripcion vacia`() {
        val result = validateAuction(
            title = "Subasta",
            description = "",
            startingPrice = "10",
            minimumIncrease = "1",
            imageUrl = "https://image.url",
            endTime = System.currentTimeMillis()
        )
        assertEquals("La descripción es obligatoria", result)
    }

    @Test
    fun `validar precio inicial vacio`() {
        val result = validateAuction(
            title = "Subasta",
            description = "Descripción válida",
            startingPrice = "",
            minimumIncrease = "1",
            imageUrl = "https://image.url",
            endTime = System.currentTimeMillis()
        )
        assertEquals("El precio inicial es obligatorio", result)
    }

    @Test
    fun `validar incremento minimo vacio`() {
        val result = validateAuction(
            title = "Subasta",
            description = "Descripción válida",
            startingPrice = "10",
            minimumIncrease = "",
            imageUrl = "https://image.url",
            endTime = System.currentTimeMillis()
        )
        assertEquals("El incremento mínimo es obligatorio", result)
    }

    @Test
    fun `validar precio inicial no numerico`() {
        val result = validateAuction(
            title = "Subasta",
            description = "Descripción válida",
            startingPrice = "abc",
            minimumIncrease = "1",
            imageUrl = "https://image.url",
            endTime = System.currentTimeMillis()
        )
        assertEquals("El precio inicial debe ser mayor a cero", result)
    }

    @Test
    fun `validar incremento minimo no numerico`() {
        val result = validateAuction(
            title = "Subasta",
            description = "Descripción válida",
            startingPrice = "10",
            minimumIncrease = "abc",
            imageUrl = "https://image.url",
            endTime = System.currentTimeMillis()
        )
        assertEquals("El incremento mínimo debe ser mayor a cero", result)
    }

    @Test
    fun `validar imagen no seleccionada`() {
        val result = validateAuction(
            title = "Subasta",
            description = "Descripción válida",
            startingPrice = "10",
            minimumIncrease = "1",
            imageUrl = null,
            endTime = System.currentTimeMillis()
        )
        assertEquals("Debes seleccionar una imagen", result)
    }

    @Test
    fun `validar fecha no seleccionada`() {
        val result = validateAuction(
            title = "Subasta",
            description = "Descripción válida",
            startingPrice = "10",
            minimumIncrease = "1",
            imageUrl = "https://image.url",
            endTime = 0L // Fecha no seleccionada
        )
        assertEquals("La fecha debe ser posterior a la actual", result)
    }

    @Test
    fun `validar fecha anterior a la actual`() {
        val result = validateAuction(
            title = "Subasta",
            description = "Descripción válida",
            startingPrice = "10",
            minimumIncrease = "1",
            imageUrl = "https://image.url",
            endTime = System.currentTimeMillis() - 100000 // Fecha pasada
        )
        assertEquals("La fecha debe ser posterior a la actual", result)
    }

    @Test
    fun `validar creacion exitosa`() {
        val result = validateAuction(
            title = "Subasta válida",
            description = "Descripción válida",
            startingPrice = "10",
            minimumIncrease = "1",
            imageUrl = "https://image.url",
            endTime = System.currentTimeMillis() + 100000 // Fecha futura
        )
        assertEquals("Válido", result)
    }

    fun validateAuction(
        title: String,
        description: String,
        startingPrice: String,
        minimumIncrease: String,
        imageUrl: String?,
        endTime: Long
    ): String {
        return when {
            title.isBlank() -> "El título es obligatorio"
            description.isBlank() -> "La descripción es obligatoria"
            startingPrice.isBlank() -> "El precio inicial es obligatorio"
            minimumIncrease.isBlank() -> "El incremento mínimo es obligatorio"
            startingPrice.toDoubleOrNull() == null || startingPrice.toDouble() <= 0 -> "El precio inicial debe ser mayor a cero"
            minimumIncrease.toDoubleOrNull() == null || minimumIncrease.toDouble() <= 0 -> "El incremento mínimo debe ser mayor a cero"
            imageUrl.isNullOrBlank() -> "Debes seleccionar una imagen"
            endTime <= System.currentTimeMillis() -> "La fecha debe ser posterior a la actual"
            else -> "Válido"
        }
    }

}
