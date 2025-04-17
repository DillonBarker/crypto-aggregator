package net.dill.price.repository

import jakarta.inject.Singleton
import net.dill.price.repository.model.CryptoPrice
import java.math.BigDecimal
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Singleton
class PriceRepository {
    private val prices = ConcurrentHashMap<String, CryptoPrice>()

    fun savePrice(symbol: String, price: BigDecimal) {
        prices[symbol] = CryptoPrice(symbol, price, Instant.now())
    }

    fun getLatestPrice(symbol: String): CryptoPrice? {
        return prices[symbol]
    }
}