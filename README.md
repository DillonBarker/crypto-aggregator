# Crypto Aggregator

## How to use

### Run the application

```
./gradlew run
```

### Retrieve a price

Where <PRICE> can be BTC-USD, ETH-USD and ETH-BTC
```
curl -X GET "http://localhost:8080/prices/<PRICE>" -H "Accept: application/json"
```

## How to run tests

```
./gradlew test
```

## Design

- In-memory storage using `ConcurrentHashMap`, thread-sage storage. To improve a database implementation would allow data to be kept upon restart.
- Fixed symbol list, to increase the number of symbols accepted, I would remove the hardcoded options and parse/validate the request.

## Scaling

- To implement multiple exchanges, I would use an `ExchangeClient` to implement to ensure data processing was uniform between them.
- I would need to add a controller for each exchange and the endpoint would be `/exchanges/{exchange}/prices/{symbol}`.
  - If we want to display all the different exchanges (so we can compare), we would keep to the same structure but fetch data from multiple exchanges.
- If I needed to make the application more resilient, it would make sense to implement a message queue for fetching prices.