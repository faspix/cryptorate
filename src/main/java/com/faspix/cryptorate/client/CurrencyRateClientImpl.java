package com.faspix.cryptorate.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateClientImpl implements CurrencyRateClient {

    private final WebClient webClient = WebClient.builder().build();

    @Value("${api.fiat.url}")
    private String fiatApiUrl;

    @Value("${api.crypto.url}")
    private String cryptoApiUrl;

    @Override
    public Mono<Map<String, BigDecimal>> getFiatRates() {
        return webClient.get()
                .uri(fiatApiUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    Map<String, Object> ratesRaw = (Map<String, Object>) response.get("rates");
                    return ratesRaw.entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> new BigDecimal(e.getValue().toString())
                            ));
                })
                .onErrorResume(e -> {
                    log.error("Failed to fetch fiat rates", e);
                    return Mono.empty();
                });
    }


    @Override
    public Mono<Map<String, BigDecimal>> getCryptoRates() {
        return webClient.get()
                .uri(cryptoApiUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Map<String, BigDecimal>>>() {})
                .map(response -> response.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().get("usd")
                        )))
                .onErrorResume(e -> {
                    log.error("Failed to fetch crypto rates", e);
                    return Mono.empty();
                });
    }


}
