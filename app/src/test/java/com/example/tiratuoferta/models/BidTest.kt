package com.example.tiratuoferta.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BidModelUnitTest {

    @Test
    fun `crear oferta con valores predeterminados`() {
        val bid = Bid()
        assertEquals("", bid.userId)
        assertEquals(0.0, bid.amount)
        assertTrue(bid.timestamp <= System.currentTimeMillis())
    }

    @Test
    fun `crear oferta con datos completos`() {
        val bid = Bid(
            userId = "user123",
            amount = 150.0,
            timestamp = System.currentTimeMillis()
        )

        assertEquals("user123", bid.userId)
        assertEquals(150.0, bid.amount)
        assertTrue(bid.timestamp <= System.currentTimeMillis())
    }

    @Test
    fun `validar oferta menor al precio actual`() {
        val currentBid = 100.0
        val bid = Bid(userId =  "user123", amount = 90.0)

        assertTrue(bid.amount < currentBid, "La oferta no puede ser menor al precio actual")
    }

    @Test
    fun `validar oferta mayor al precio actual`() {
        val currentBid = 100.0
        val bid = Bid(userId = "user123", amount = 110.0)

        assertTrue(bid.amount > currentBid, "La oferta debe ser mayor al precio actual")
    }
}
