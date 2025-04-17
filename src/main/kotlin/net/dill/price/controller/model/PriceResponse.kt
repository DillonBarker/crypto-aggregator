package net.dill.price.controller.model

import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal

@Serdeable
data class PriceResponse(
    val symbol: String,
    val price: BigDecimal,
    val timestamp: String
)