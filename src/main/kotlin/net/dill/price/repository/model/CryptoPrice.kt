package net.dill.price.repository.model

import java.math.BigDecimal
import java.time.Instant

data class CryptoPrice(
    val symbol: String,
    val price: BigDecimal,
    val timestamp: Instant
)