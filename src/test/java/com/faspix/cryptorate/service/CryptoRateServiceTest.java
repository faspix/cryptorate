package com.faspix.cryptorate.service;

import com.faspix.cryptorate.dto.ResponseHistoryDTO;
import com.faspix.cryptorate.exception.ExchangeRateNotFoundException;
import com.faspix.cryptorate.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static com.faspix.cryptorate.utility.CurrencyFactory.makeCurrency;
import static com.faspix.cryptorate.utility.CurrencyFactory.makeHistoryDTO;

@ExtendWith(MockitoExtension.class)
public class CryptoRateServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ReactiveRedisTemplate<String, BigDecimal> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, BigDecimal> valueOperations;

    @InjectMocks
    private CryptoRateService cryptoRateService;


    private static final BigDecimal BTC_RATE = new BigDecimal("60000.00");
    private static final BigDecimal ETH_RATE = new BigDecimal("2000.00");

        @Test
    void convertTest_Success() {
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(valueOperations.get("BTC"))
                .thenReturn(Mono.just(BTC_RATE));
        when(valueOperations.get("ETH"))
                .thenReturn(Mono.just(ETH_RATE));

        BigDecimal amount = new BigDecimal("2");

        StepVerifier.create(cryptoRateService.convert("BTC", "ETH", amount))
                .assertNext(dto -> {
                    BigDecimal expectedRate = ETH_RATE.divide(BTC_RATE, 15, BigDecimal.ROUND_HALF_UP);
                    BigDecimal expectedResult = amount.multiply(expectedRate);

                    assertThat(dto.from(), equalTo("BTC"));

                    assertThat(dto.from(), equalTo("BTC"));
                    assertThat(dto.to(), equalTo("ETH"));
                    assertThat(dto.amount(), equalTo(amount));
                    assertThat(dto.rate(), equalTo(expectedRate));
                    assertThat(dto.convertedAmount(), equalTo(expectedResult));
                })
                .verifyComplete();
    }

    @Test
    void convertTest_FromRateNotFound() {
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(valueOperations.get("UNKNOWN"))
                .thenReturn(Mono.empty());
        when(valueOperations.get("ETH"))
                .thenReturn(Mono.just(ETH_RATE));

        StepVerifier.create(cryptoRateService.convert("UNKNOWN", "ETH", BigDecimal.ONE))
                .expectErrorMatches(e ->
                        e instanceof ExchangeRateNotFoundException &&
                        e.getMessage().contains("Exchange rate for 'UNKNOWN' not found"))
                .verify();
    }

    @Test
    void convertTest_ToRateNotFound() {
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(valueOperations.get("BTC"))
                .thenReturn(Mono.just(BTC_RATE));
        when(valueOperations.get("UNKNOWN"))
                .thenReturn(Mono.empty());

        StepVerifier.create(cryptoRateService.convert("BTC", "UNKNOWN", BigDecimal.ONE))
                .expectErrorMatches(e ->
                        e instanceof ExchangeRateNotFoundException &&
                        e.getMessage().contains("Exchange rate for 'UNKNOWN' not found"))
                .verify();
    }

    @Test
    void getHistoryTest_Success() {
        when(currencyRepository.findByCurrencyAndTimestampBetween(anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Flux.just(makeCurrency()));

        Flux<ResponseHistoryDTO> history = cryptoRateService.getHistory("USD", "USD",
                LocalDate.MIN, LocalDate.MIN);

        StepVerifier.create(history)
                .expectNext(makeHistoryDTO())
                .verifyComplete();
    }

}
