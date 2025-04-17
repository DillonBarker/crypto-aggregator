package net.dill.price.service.model

import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal

@Serdeable
data class TickerResponse(
    val price: BigDecimal,
    val time: String
)