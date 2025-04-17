package net.dill.price.controller.model

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class ErrorResponse(
    val status: Int,
    val message: String
)