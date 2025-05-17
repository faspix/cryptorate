package com.faspix.cryptorate.service;

import com.faspix.cryptorate.client.CurrencyRateClient;
import com.faspix.cryptorate.entity.Currency;
import com.faspix.cryptorate.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static com.faspix.cryptorate.utility.CronUtility.computeTTL;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateRateService {

    private static final long TTL_BUFFER = 60;

    private final CurrencyRateClient currencyRateClient;

    private final ReactiveRedisTemplate<String, BigDecimal> reactiveRedisTemplate;

    private final CurrencyRepository currencyRepository;

    @Value("${scheduler.crypto-rate-cron}")
    private String cryptoCacheRateCron;

    @Value("${scheduler.fiat-rate-cron}")
    private String fiatCacheRateCron;


    public Mono<Void> updateFiatRate() {
        return currencyRateClient.getFiatRates() // Mono<Map<String, BigDecimal>>
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .flatMap(entry -> {
                    String currency = entry.getKey();
                    BigDecimal rateToUSD = entry.getValue();

                    // save to Redis
                    Mono<Boolean> saveToRedis = reactiveRedisTemplate.opsForValue()
                            .set(currency, rateToUSD, computeTTL(fiatCacheRateCron).plusSeconds(TTL_BUFFER));

                    // save to Mongo
                    Currency currencyDoc = new Currency(
                            currency,
                            LocalDateTime.now(),
                            rateToUSD
                    );
                    Mono<Currency> saveToMongo = currencyRepository.save(currencyDoc);

                    return Mono.when(saveToRedis, saveToMongo);
                })
                .then();
    }

    // Save crypto rate to redis
    public Mono<Void> updateCryptoRate() {
        return currencyRateClient.getCryptoRates()
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .flatMap(entry -> {
                    String currency = entry.getKey();
                    BigDecimal rateToUSD = entry.getValue();
                    return reactiveRedisTemplate.opsForValue()
                            .set(currency, rateToUSD, computeTTL(cryptoCacheRateCron).plusSeconds(TTL_BUFFER));
                })
                .then();
    }

    // Save crypto rate to mongo
    public Mono<Void> saveCryptoRateToDb() {
        return currencyRateClient.getCryptoRates()
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .flatMap(entry -> {
                    String currency = entry.getKey();
                    BigDecimal rateToUSD = entry.getValue();

                    Currency currencyDoc = new Currency(
                            currency,
                            LocalDateTime.now(),
                            rateToUSD
                    );

                    return currencyRepository.save(currencyDoc);
                })
                .then();
    }

}
