# CryptoRate API

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/b30b7b81c4774004befeb61364935aa5)](https://app.codacy.com/gh/faspix/cryptorate/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/b30b7b81c4774004befeb61364935aa5)](https://app.codacy.com/gh/faspix/cryptorate/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)
[![CodeFactor](https://www.codefactor.io/repository/github/faspix/cryptorate/badge)](https://www.codefactor.io/repository/github/faspix/cryptorate)

## Currency Converter API

A reactive Spring Boot application providing currency and cryptocurrency conversion rates with historical data support.  
Designed as a monolithic API to fetch, cache, and store exchange rates using Redis and MongoDB.

## Features

- Fetches fiat currency exchange rates from external APIs and caches them in Redis.
- Fetches cryptocurrency rates every 5 minutes, caches in Redis, and stores hourly snapshots in MongoDB.
- Provides endpoints to convert currencies and retrieve historical exchange rate data.
- Uses reactive programming with Spring WebFlux and Reactive MongoDB for high performance.
- Configurable cache TTL and scheduler cron expressions via application properties.
- History stored standardized against USD for simplified conversions.
- Logs and metrics can be integrated with Micrometer, Prometheus, and Grafana (optional).

## Technology Stack

- Java 17+
- Spring Boot (Reactive)
- Spring WebFlux
- MongoDB (NoSQL document store)
- Redis (in-memory cache with TTL)
- External Currency APIs (fiat and crypto)
- JUnit 5 + Mockito for testing
- Docker + Docker Compose

## API Endpoints
### All endpoints are also available at `/swagger-ui/index.html`
- `POST /convert?from={from}&to={to}&amount={amount}` - Convert amount from one currency to another.
- `GET /history?from={from}&to={to}&startDate={startDate}&endDate={endDate}` - Get historical exchange rates.

