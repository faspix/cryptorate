package com.faspix.cryptorate.client;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class CurrencyRateClientImpl implements CurrencyRateClient {
    @Override
    public Mono<Map<String, BigDecimal>> getFiatRates() {
        return null;
    }

    @Override
    public Mono<Map<String, BigDecimal>> getCryptoRates() {
        return null;
    }
}
