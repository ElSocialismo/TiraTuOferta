package com.example.tiratuoferta.models


import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AuctionModelUnitTest {

    @Test
    fun `crear subasta con valores predeterminados`() {
        val auction = Auction(startTime = System.currentTimeMillis())
        assertEquals("", auction.id)
        assertEquals("", auction.title)
        assertEquals("", auction.description)
        assertEquals("", auction.imageUrl)
        assertEquals(0.0, auction.startingPrice)
        assertEquals(0.0, auction.currentBid)
        assertEquals(0.0, auction.minimumIncrease)
        assertEquals(0L, auction.endTime)
        assertEquals("", auction.userId)
        assertNull(auction.participants)
        assertFalse(auction.Favorite)
    }

    @Test
    fun `validar subasta con datos completos`() {
        val auction = Auction(
            id = "1",
            title = "Subasta Test",
            description = "Descripción de prueba",
            imageUrl = "https://example.com/image.jpg",
            startingPrice = 100.0,
            currentBid = 150.0,
            minimumIncrease = 10.0,
            endTime = System.currentTimeMillis() + 100000,
            userId = "user123",
            participants = mutableMapOf("user123" to true, "user456" to true),
            Favorite = true,
            startTime = System.currentTimeMillis()
        )

        assertEquals("1", auction.id)
        assertEquals("Subasta Test", auction.title)
        assertEquals("Descripción de prueba", auction.description)
        assertEquals("https://example.com/image.jpg", auction.imageUrl)
        assertEquals(100.0, auction.startingPrice)
        assertEquals(150.0, auction.currentBid)
        assertEquals(10.0, auction.minimumIncrease)
        assertTrue(auction.endTime > System.currentTimeMillis())
        assertEquals("user123", auction.userId)
        assertNotNull(auction.participants)
        assertTrue(auction.Favorite)
    }

    @Test
    fun `validar incremento minimo incorrecto`() {
        val auction = Auction(
            title = "Subasta Test",
            startingPrice = 100.0,
            currentBid = 105.0,
            minimumIncrease = 10.0,
            startTime = System.currentTimeMillis()
        )
        assertFalse(auction.currentBid - auction.startingPrice >= auction.minimumIncrease)
    }

    @Test
    fun `validar que la subasta expiro`() {
        val auction = Auction(
            title = "Subasta Test",
            endTime = System.currentTimeMillis() - 10000,
            startTime = System.currentTimeMillis() // Fecha pasada
        )
        assertTrue(auction.endTime < System.currentTimeMillis(), "La subasta ya expiró")
    }
}
