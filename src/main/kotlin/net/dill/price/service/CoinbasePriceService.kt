package net.dill.price.service

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import net.dill.price.client.CoinbaseClient
import org.slf4j.LoggerFactory
import io.micronaut.http.client.exceptions.HttpClientException
import net.dill.price.repository.PriceRepository

@Singleton
class CoinbasePriceService(
    private val coinbaseClient: CoinbaseClient,
    private val priceRepository: PriceRepository
) {
    private val logger = LoggerFactory.getLogger(CoinbasePriceService::class.java)
    private val symbols = listOf("BTC-USD", "ETH-USD", "ETH-BTC")

    @Scheduled(fixedRate = "10s")
    fun fetchLatestPrices() {
        symbols.forEach { symbol ->
            try {
                val ticker = coinbaseClient.getTicker(symbol)
                priceRepository.savePrice(symbol, ticker.price)
                logger.info("Updated price for $symbol: ${ticker.price}")
            } catch (e: HttpClientException) {
                logger.error("Error fetching price for $symbol", e)
            } catch (e: Exception) {
                logger.error("Unexpected error fetching price for $symbol", e)
            }
        }
    }
}