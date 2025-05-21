package com.faspix.cryptorate.controller;

import com.faspix.cryptorate.dto.ResponseConvertDTO;
import com.faspix.cryptorate.dto.ResponseHistoryDTO;
import com.faspix.cryptorate.entity.Currency;
import com.faspix.cryptorate.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.faspix.cryptorate.utility.CurrencyFactory.makeCurrency;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureWebTestClient
public class CryptoRateControllerTest {

    private static final String API_VERSION_URI = "/api/v1";

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(27017);

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.2")
            .withExposedPorts(6379);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private ReactiveRedisTemplate<String, BigDecimal> reactiveRedisTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @BeforeEach
    void setUp() {
        reactiveMongoTemplate.dropCollection(Currency.class).block();
        reactiveRedisTemplate.opsForValue().delete("BTC").block();
        reactiveRedisTemplate.opsForValue().delete("ETH").block();
    }

    @Test
    void testConvertEndpoint_Success() {
        reactiveRedisTemplate.opsForValue().set("BTC", new BigDecimal("50000")).block();
        reactiveRedisTemplate.opsForValue().set("ETH", new BigDecimal("4000")).block();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_VERSION_URI + "/convert")
                        .queryParam("from", "BTC")
                        .queryParam("to", "ETH")
                        .queryParam("amount", "2")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ResponseConvertDTO.class)
                .value(response -> {
                    assertThat(response, hasSize(1));
                    ResponseConvertDTO dto = response.get(0);
                    assertThat(dto.from(), equalTo("BTC"));
                    assertThat(dto.to(), equalTo("ETH"));
                    assertThat(dto.amount(), equalTo(new BigDecimal("2")));
                    assertThat(dto.convertedAmount(), equalTo(new BigDecimal("0.160000000000000")));
                    assertThat(dto.rate(), equalTo(new BigDecimal("0.080000000000000")));
                });
    }

    @Test
    void testConvertEndpoint_InvalidAmount() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_VERSION_URI + "/convert")
                        .queryParam("from", "BTC")
                        .queryParam("to", "ETH")
                        .queryParam("amount", "-1")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testConvertEndpoint_RateNotFound() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_VERSION_URI + "/convert")
                        .queryParam("from", "BTC")
                        .queryParam("to", "ETH")
                        .queryParam("amount", "2")
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testHistoryEndpoint_Success() {
        LocalDateTime timestamp = LocalDate.of(2025, 5, 18).atStartOfDay();
        Currency btcCurrency = new Currency("BTC", timestamp, new BigDecimal("50000"));

        Currency ethCurrency = new Currency("ETH", timestamp, new BigDecimal("4000"));

        currencyRepository.saveAll(List.of(btcCurrency, ethCurrency)).blockLast();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_VERSION_URI + "/history")
                        .queryParam("from", "BTC")
                        .queryParam("to", "ETH")
                        .queryParam("startDate", "2025-05-17")
                        .queryParam("endDate", "2025-05-18")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ResponseHistoryDTO.class)
                .value(response -> {
                    assertThat(response, hasSize(1));
                    ResponseHistoryDTO dto = response.get(0);
                    assertThat(dto.from(), equalTo("BTC"));
                    assertThat(dto.to(), equalTo("ETH"));
                    assertThat(dto.history(), hasSize(1));
                    assertThat(dto.history().get(0).date(), equalTo(timestamp));
                    assertThat(dto.history().get(0).rate(), equalTo(new BigDecimal("12.500000000000000")));
                });
    }

    @Test
    void testHistoryEndpoint_NoData() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_VERSION_URI + "/history")
                        .queryParam("from", "BTC")
                        .queryParam("to", "ETH")
                        .queryParam("startDate", "2025-05-18")
                        .queryParam("endDate", "2025-05-18")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ResponseHistoryDTO.class)
                .value(response -> assertThat(response, hasSize(0)));
    }
}
