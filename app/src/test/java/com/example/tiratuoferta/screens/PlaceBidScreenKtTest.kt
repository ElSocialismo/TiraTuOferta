package com.example.tiratuoferta.screens

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import com.example.tiratuoferta.models.Bid
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class PlaceBidScreenUnitTest {

    @Test
    fun `bid lower than current fails`() {
        val currentBid = 50.0
        val newBid = 40.0

        val result = validateBid(currentBid, newBid)
        assertEquals("La puja debe ser mayor a la puja actual de 50.0$", result)
    }

    @Test
    fun `bid equal to current fails`() {
        val currentBid = 50.0
        val newBid = 50.0

        val result = validateBid(currentBid, newBid)
        assertEquals("La puja debe ser mayor a la puja actual de 50.0$", result)
    }

    @Test
    fun `bid higher than current succeeds`() {
        val currentBid = 50.0
        val newBid = 60.0

        val result = validateBid(currentBid, newBid)
        assertEquals("Valid", result)
    }

    @Test
    fun `saving new bid updates Firebase correctly`() {
        val mockDatabase = mock(DatabaseReference::class.java)
        val newBid = Bid(userId = "test@example.com", amount = 60.0, timestamp = System.currentTimeMillis())

        saveBid(mockDatabase, "auctionId", newBid)

        verify(mockDatabase.child("auctionId").child("bids").push()).setValue(newBid)
        verify(mockDatabase.child("auctionId").child("currentBid")).setValue(newBid.amount)
    }

    @Test
    fun `non-numeric bid is invalid`() {
        val currentBid = 50.0
        val newBid = "ABC".toDoubleOrNull() // Simular entrada no válida

        val result = if (newBid == null) "Invalid amount" else validateBid(currentBid, newBid)
        assertEquals("Invalid amount", result)
    }

    @Test
    fun `bid history loads correctly`() {
        val mockDatabase = mock(DatabaseReference::class.java)
        val mockSnapshot = mock(DataSnapshot::class.java)

        // Simular el historial de pujas con 3 entradas
        val bids = listOf(
            Bid(userId = "user1", amount = 100.0, timestamp = System.currentTimeMillis()),
            Bid(userId = "user2", amount = 120.0, timestamp = System.currentTimeMillis()),
            Bid(userId = "user3", amount = 150.0, timestamp = System.currentTimeMillis())
        )

        // Configurar el mock para devolver las pujas simuladas
        `when`(mockSnapshot.children).thenReturn(bids.map { mockSnapshot })

        val bidHistory = mutableListOf<Bid>()
        for (snapshot in mockSnapshot.children) {
            val bid = snapshot.getValue(Bid::class.java)
            bid?.let { bidHistory.add(it) }
        }

        assertEquals(3, bidHistory.size)
    }

    @Test
    fun `currentBid updates correctly`() {
        val mockDatabase = mock(DatabaseReference::class.java)
        val auctionId = "auctionId"
        val newBid = 60.0

        // Mockear el comportamiento de setValue (solo verificar llamada)
        val mockTask = mock(com.google.android.gms.tasks.Task::class.java) as com.google.android.gms.tasks.Task<Void>
        `when`(mockDatabase.child(auctionId).child("currentBid").setValue(newBid)).thenReturn(mockTask)

        // Simular la actualización de currentBid
        mockDatabase.child(auctionId).child("currentBid").setValue(newBid)

        // Verificar que se llamó correctamente
        verify(mockDatabase.child(auctionId).child("currentBid")).setValue(newBid)
    }


    @Test
    fun `error message disappears after valid bid`() {
        val currentBid = 50.0
        val previousErrorMessage = "La puja debe ser mayor a la puja actual de 50.0$"
        val newBid = 60.0

        val result = if (newBid > currentBid) "" else previousErrorMessage
        assertEquals("", result)
    }

    @Test
    fun `bid equal to zero fails`() {
        val currentBid = 50.0
        val newBid = 0.0

        val result = validateBid(currentBid, newBid)
        assertEquals("La puja debe ser mayor a la puja actual de 50.0$", result)
    }

    @Test
    fun `empty bid fails`() {
        val currentBid = 50.0
        val newBid: Double? = null

        val result = if (newBid == null) "Invalid amount" else validateBid(currentBid, newBid)
        assertEquals("Invalid amount", result)
    }

    // Helpers
    private fun validateBid(currentBid: Double, newBid: Double?): String {
        return if (newBid == null || newBid <= currentBid) {
            "La puja debe ser mayor a la puja actual de ${currentBid}$"
        } else {
            "Valid"
        }
    }

    private fun saveBid(database: DatabaseReference, auctionId: String, bid: Bid) {
        database.child(auctionId).child("bids").push().setValue(bid)
        database.child(auctionId).child("currentBid").setValue(bid.amount)
    }
}
