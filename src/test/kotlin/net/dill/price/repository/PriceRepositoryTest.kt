package net.dill.price.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class PriceRepositoryTest {

    private lateinit var repository: PriceRepository

    @BeforeEach
    fun setUp() {
        repository = PriceRepository()
    }

    @Test
    fun `savePrice should store the price correctly`() {
        val symbol = "BTC"
        val price = BigDecimal("68000.50")

        repository.savePrice(symbol, price)
        val storedPrice = repository.getLatestPrice(symbol)

        assertNotNull(storedPrice)
        assertEquals(symbol, storedPrice?.symbol)
        assertEquals(price, storedPrice?.price)
        assertNotNull(storedPrice?.timestamp)
    }

    @Test
    fun `getLatestPrice should return null for unknown symbol`() {
        val unknownSymbol = "DOGE"
        val result = repository.getLatestPrice(unknownSymbol)
        assertNull(result)
    }

    @Test
    fun `savePrice should overwrite the price for the same symbol`() {
        val symbol = "ETH"
        val firstPrice = BigDecimal("3100.00")
        val secondPrice = BigDecimal("3200.00")

        repository.savePrice(symbol, firstPrice)
        Thread.sleep(10)  // Ensure timestamps differ
        repository.savePrice(symbol, secondPrice)

        val updatedPrice = repository.getLatestPrice(symbol)

        assertNotNull(updatedPrice)
        assertEquals(symbol, updatedPrice?.symbol)
        assertEquals(secondPrice, updatedPrice?.price)
        assertTrue(updatedPrice!!.timestamp.isAfter(Instant.EPOCH))
    }
}
