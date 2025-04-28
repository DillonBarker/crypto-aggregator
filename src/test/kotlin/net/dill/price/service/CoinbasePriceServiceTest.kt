package net.dill.price.service

import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import net.dill.price.client.CoinbaseClient
import net.dill.price.repository.PriceRepository
import net.dill.price.service.model.TickerResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal

@MicronautTest
class CoinbasePriceServiceTest {

    private lateinit var coinbaseClient: CoinbaseClient
    private lateinit var priceRepository: PriceRepository
    private lateinit var coinbasePriceService: CoinbasePriceService

    @BeforeEach
    fun setup() {
        coinbaseClient = mock(CoinbaseClient::class.java)
        priceRepository = mock(PriceRepository::class.java)
        coinbasePriceService = CoinbasePriceService(coinbaseClient, priceRepository)
    }

    @Test
    fun `fetchLatestPrices successfully retrieves and saves prices for all symbols`() {
        val btcUsdResponse = TickerResponse(BigDecimal("50000.25"), "2025-04-28T14:30:00Z")
        val ethUsdResponse = TickerResponse(BigDecimal("3500.75"), "2025-04-28T14:30:00Z")
        val ethBtcResponse = TickerResponse(BigDecimal("0.07"), "2025-04-28T14:30:00Z")

        `when`(coinbaseClient.getTicker("BTC-USD")).thenReturn(btcUsdResponse)
        `when`(coinbaseClient.getTicker("ETH-USD")).thenReturn(ethUsdResponse)
        `when`(coinbaseClient.getTicker("ETH-BTC")).thenReturn(ethBtcResponse)

        coinbasePriceService.fetchLatestPrices()

        verify(priceRepository).savePrice("BTC-USD", BigDecimal("50000.25"))
        verify(priceRepository).savePrice("ETH-USD", BigDecimal("3500.75"))
        verify(priceRepository).savePrice("ETH-BTC", BigDecimal("0.07"))
    }

    @Test
    fun `fetchLatestPrices handles HTTP client exceptions and continues processing`() {
        val btcUsdResponse = TickerResponse(BigDecimal("50000.25"), "2025-04-28T14:30:00Z")
        val httpClientException = mock(HttpClientException::class.java)

        `when`(coinbaseClient.getTicker("BTC-USD")).thenReturn(btcUsdResponse)
        `when`(coinbaseClient.getTicker("ETH-USD")).thenThrow(httpClientException)
        `when`(coinbaseClient.getTicker("ETH-BTC")).thenReturn(TickerResponse(BigDecimal("0.07"), "2025-04-28T14:30:00Z"))

        coinbasePriceService.fetchLatestPrices()

        verify(priceRepository).savePrice("BTC-USD", BigDecimal("50000.25"))
        verify(priceRepository).savePrice("ETH-BTC", BigDecimal("0.07"))
    }

    @Test
    fun `fetchLatestPrices handles general exceptions and continues processing`() {
        val btcUsdResponse = TickerResponse(BigDecimal("50000.25"), "2025-04-28T14:30:00Z")
        val genericException = RuntimeException("Connection failed")

        `when`(coinbaseClient.getTicker("BTC-USD")).thenReturn(btcUsdResponse)
        `when`(coinbaseClient.getTicker("ETH-USD")).thenReturn(TickerResponse(BigDecimal("3500.75"), "2025-04-28T14:30:00Z"))
        `when`(coinbaseClient.getTicker("ETH-BTC")).thenThrow(genericException)

        coinbasePriceService.fetchLatestPrices()

        verify(priceRepository).savePrice("BTC-USD", BigDecimal("50000.25"))
        verify(priceRepository).savePrice("ETH-USD", BigDecimal("3500.75"))
    }
}