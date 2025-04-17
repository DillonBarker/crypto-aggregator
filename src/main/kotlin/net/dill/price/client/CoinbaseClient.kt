package net.dill.price.client

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.client.annotation.Client
import net.dill.price.service.model.TickerResponse

@Client("https://api.exchange.coinbase.com")
@Header(name = "User-Agent", value = "CryptoPriceAggregator/1.0")
interface CoinbaseClient {
    @Get("/products/{symbol}/ticker")
    fun getTicker(symbol: String): TickerResponse
}
