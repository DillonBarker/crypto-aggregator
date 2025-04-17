package net.dill.price.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import net.dill.price.controller.model.ErrorResponse
import net.dill.price.controller.model.PriceResponse
import net.dill.price.repository.PriceRepository

@Controller("/prices")
class PriceController(private val priceRepository: PriceRepository) {

    @Get("/{symbol}")
    fun getLatestPrice(@PathVariable symbol: String): HttpResponse<Any> {
        val price = priceRepository.getLatestPrice(symbol)

        return if (price != null) {
            HttpResponse.ok(
                PriceResponse(
                    symbol = price.symbol,
                    price = price.price,
                    timestamp = price.timestamp.toString()
                )
            )
        } else {
            HttpResponse.notFound(
                ErrorResponse(
                    status = 404,
                    message = "Price for symbol '$symbol' not found or not available yet"
                )
            )
        }
    }
}