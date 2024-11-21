package com.example.tiratuoferta.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import com.example.tiratuoferta.models.Auction
import com.google.firebase.database.DatabaseReference
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class AuctionDetailsScreenUnitTest {

    private val composeTestRule = createComposeRule()
    private lateinit var navController: NavController
    private lateinit var auctionRef: DatabaseReference
    private lateinit var auction: Auction

    @Before
    fun setup() {
        // Inicializar los mocks y datos
        navController = mock()
        auctionRef = mock()

        auction = Auction(
            id = "auctionId",
            title = "Auction Title",
            description = "Auction Description",
            startTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis() + 10000000,
            startingPrice = 100.0,
            currentBid = 150.0,
            imageUrl = "http://example.com/image.jpg",
            Favorite = false
        )
    }

    @Test
    fun `test auction details screen shows auction data correctly`() {
        // Simular la pantalla de detalles de la subasta
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = auction.id)
        }

        // Verificar que los textos de la subasta están presentes en la UI
        composeTestRule.onNodeWithText(auction.title).assertExists()
        composeTestRule.onNodeWithText(auction.description).assertExists()
        composeTestRule.onNodeWithText("Precio inicial: ${auction.startingPrice}$").assertExists()
        composeTestRule.onNodeWithText("Puja actual: ${auction.currentBid}$").assertExists()
    }

    @Test
    fun `test auction favorite toggle`() {
        // Simular la pantalla de detalles de la subasta
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = auction.id)
        }

        // Verificar que el icono de favorito está presente
        val favoriteIcon = composeTestRule.onNodeWithContentDescription("Favorite")
        favoriteIcon.assertExists()

        // Hacer clic en el icono de favorito y verificar que se actualiza
        favoriteIcon.performClick()
        verify(auctionRef).child("Favorite").setValue(true)

        // Cambiar el valor de favorito para simular un cambio
        auction.Favorite = true
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = auction.id)
        }

        // Verificar que el icono de favorito se muestra como activado
        favoriteIcon.assertExists()
    }

    @Test
    fun `test auction time countdown works correctly`() {
        // Simular la pantalla de detalles de la subasta
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = auction.id)
        }

        // Verificar que el contador de tiempo aparece en el formato correcto
        composeTestRule.onNodeWithText("00 días 00 horas 00 minutos 00 segundos").assertExists()

        // Cambiar el tiempo de finalización para simular el paso del tiempo
        auction.endTime = System.currentTimeMillis() + 10000
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = auction.id)
        }

        // Verificar que el tiempo restante se actualiza
        composeTestRule.onNodeWithText("00 días 00 horas 00 minutos 10 segundos").assertExists()
    }

    @Test
    fun `test auction back navigation`() {
        // Simular la pantalla de detalles de la subasta
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = auction.id)
        }

        // Hacer clic en el botón de retroceso
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Verificar que la navegación hacia atrás ocurre
        verify(navController).popBackStack()
    }

    @Test
    fun `test auction bid button navigation`() {
        // Simular la pantalla de detalles de la subasta
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = auction.id)
        }

        // Verificar que el botón de realizar puja está presente
        val bidButton = composeTestRule.onNodeWithText("Realizar Puja")
        bidButton.assertExists()

        // Hacer clic en el botón de realizar puja
        bidButton.performClick()

        // Verificar que se navega hacia la pantalla de realizar puja
        verify(navController).navigate("placeBid/${auction.id}")
    }

    @Test
    fun `test auction loading state`() {
        // Simular la pantalla de detalles de la subasta con datos nulos
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = "invalidId")
        }

        // Verificar que el indicador de carga está presente
        composeTestRule.onNodeWithTag("loadingIndicator").assertExists()
    }

    @Test
    fun `test auction image loads correctly`() {
        // Simular la pantalla de detalles de la subasta
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = auction.id)
        }

        // Verificar que la imagen de la subasta se carga correctamente
        composeTestRule.onNodeWithContentDescription(auction.imageUrl).assertExists()
    }

    @Test
    fun `test auction description is displayed correctly`() {
        // Simular la pantalla de detalles de la subasta
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = auction.id)
        }

        // Verificar que la descripción de la subasta está visible
        composeTestRule.onNodeWithText(auction.description).assertExists()
    }

    @Test
    fun `test auction initial price is displayed correctly`() {
        // Simular la pantalla de detalles de la subasta
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = auction.id)
        }

        // Verificar que el precio inicial está visible
        composeTestRule.onNodeWithText("Precio inicial: ${auction.startingPrice}$").assertExists()
    }

    @Test
    fun `test auction current bid is displayed correctly`() {
        // Simular la pantalla de detalles de la subasta
        composeTestRule.setContent {
            AuctionDetailsScreen(navController = navController, auctionId = auction.id)
        }

        // Verificar que la puja actual está visible
        composeTestRule.onNodeWithText("Puja actual: ${auction.currentBid}$").assertExists()
    }
}
