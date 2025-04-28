package net.dill.price.controller

import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import net.dill.price.controller.model.ErrorResponse
import net.dill.price.controller.model.PriceResponse
import net.dill.price.repository.PriceRepository
import net.dill.price.repository.model.CryptoPrice
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.time.Instant

@MicronautTest
class PriceControllerTest {

    private lateinit var priceRepository: PriceRepository
    private lateinit var priceController: PriceController

    @BeforeEach
    fun setup() {
        priceRepository = mock(PriceRepository::class.java)
        priceController = PriceController(priceRepository)
    }

    @Test
    fun `getLatestPrice returns correct price when symbol exists`() {
        val symbol = "BTC-USD"
        val timestamp = Instant.now()
        val cryptoPrice = CryptoPrice(symbol, BigDecimal("50000.25"), timestamp)
        `when`(priceRepository.getLatestPrice(symbol)).thenReturn(cryptoPrice)

        val response = priceController.getLatestPrice(symbol)

        assertEquals(HttpStatus.OK, response.status)
        val body = response.body()
        assertTrue(body is PriceResponse)

        val priceResponse = body as PriceResponse
        assertEquals(symbol, priceResponse.symbol)
        assertEquals(BigDecimal("50000.25"), priceResponse.price)
        assertEquals(timestamp.toString(), priceResponse.timestamp)
    }

    @Test
    fun `getLatestPrice returns 404 when symbol does not exist`() {
        val symbol = "UNKNOWN-PAIR"
        `when`(priceRepository.getLatestPrice(symbol)).thenReturn(null)

        val response = priceController.getLatestPrice(symbol)

        assertEquals(HttpStatus.NOT_FOUND, response.status)
        val body = response.body()
        assertTrue(body is ErrorResponse)

        val errorResponse = body as ErrorResponse
        assertEquals(404, errorResponse.status)
        assertEquals("Price for symbol 'UNKNOWN-PAIR' not found or not available yet", errorResponse.message)
    }

    @Test
    fun `getLatestPrice handles different symbols correctly`() {
        val ethUsdSymbol = "ETH-USD"
        val ethUsdTimestamp = Instant.now()
        val ethUsdPrice = CryptoPrice(ethUsdSymbol, BigDecimal("3500.75"), ethUsdTimestamp)

        `when`(priceRepository.getLatestPrice(ethUsdSymbol)).thenReturn(ethUsdPrice)
        `when`(priceRepository.getLatestPrice("ETH-BTC")).thenReturn(null)

        val ethUsdResponse = priceController.getLatestPrice(ethUsdSymbol)
        assertEquals(HttpStatus.OK, ethUsdResponse.status)
        val ethUsdBody = ethUsdResponse.body() as PriceResponse
        assertEquals(ethUsdSymbol, ethUsdBody.symbol)
        assertEquals(BigDecimal("3500.75"), ethUsdBody.price)

        val ethBtcResponse = priceController.getLatestPrice("ETH-BTC")
        assertEquals(HttpStatus.NOT_FOUND, ethBtcResponse.status)
        val ethBtcBody = ethBtcResponse.body() as ErrorResponse
        assertEquals(404, ethBtcBody.status)
        assertEquals("Price for symbol 'ETH-BTC' not found or not available yet", ethBtcBody.message)
    }
}