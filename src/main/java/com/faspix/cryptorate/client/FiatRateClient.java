package com.faspix.cryptorate.client;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

public interface FiatRateClient {

    Mono<Map<String, BigDecimal>> getFiatRates();

}
