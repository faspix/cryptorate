package com.faspix.cryptorate.service;

import com.faspix.cryptorate.client.CurrencyRateClient;
import com.faspix.cryptorate.entity.Currency;
import com.faspix.cryptorate.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static com.faspix.cryptorate.utility.CurrencyFactory.makeCurrency;

@ExtendWith(MockitoExtension.class)
public class UpdateRateServiceTest {

    @Mock
    private CurrencyRateClient currencyRateClient;

    @Mock
    private ReactiveRedisTemplate<String, BigDecimal> reactiveRedisTemplate;

    @Mock
    private ReactiveValueOperations<String, BigDecimal> valueOperations;

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private UpdateRateService updateRateService;

    private static final String fiatCron = "0 0 * * * *"; // every hour
    private static final String cryptoCron = "0 */5 * * * *"; // every 5 minutes

    @BeforeEach
    void setup() {
        // @Value fields
        setField(updateRateService, "fiatCacheRateCron", fiatCron);
        setField(updateRateService, "cryptoCacheRateCron", cryptoCron);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateFiatRate_savesRatesToRedisAndMongo() {
        Map<String, BigDecimal> rates = Map.of(
                "USD", new BigDecimal("1.0"),
                "EUR", new BigDecimal("0.9")
        );

        when(currencyRateClient.getFiatRates()).thenReturn(Mono.just(rates));
        when(valueOperations.set(anyString(), any(), any(Duration.class))).thenReturn(Mono.just(true));
        when(currencyRepository.save(any())).thenReturn(Mono.just(makeCurrency()));
        when(reactiveRedisTemplate.opsForValue()).thenReturn(valueOperations);

        Mono<Void> result = updateRateService.updateFiatRate();

        StepVerifier.create(result)
                .verifyComplete();

        verify(valueOperations, times(2)).set(anyString(), any(), any(Duration.class));
        verify(currencyRepository, times(2)).save(any(Currency.class));
    }

    @Test
    void updateCryptoRate_savesRatesToRedis() {
        Map<String, BigDecimal> rates = Map.of(
                "BTC", new BigDecimal("30000"),
                "ETH", new BigDecimal("2000")
        );

        when(currencyRateClient.getCryptoRates()).thenReturn(Mono.just(rates));
        when(valueOperations.set(anyString(), any(), any(Duration.class))).thenReturn(Mono.just(true));
        when(reactiveRedisTemplate.opsForValue()).thenReturn(valueOperations);

        Mono<Void> result = updateRateService.updateCryptoRate();

        StepVerifier.create(result)
                .verifyComplete();

        verify(valueOperations, times(2)).set(anyString(), any(), any(Duration.class));
        verify(currencyRepository, never()).save(any());
    }

    @Test
    void saveCryptoRateToDb_savesRatesToMongo() {
        Map<String, BigDecimal> rates = Map.of(
                "BTC", new BigDecimal("30000"),
                "ETH", new BigDecimal("2000")
        );

        when(currencyRateClient.getCryptoRates()).thenReturn(Mono.just(rates));
        when(currencyRepository.save(any())).thenReturn(Mono.just(makeCurrency()));

        Mono<Void> result = updateRateService.saveCryptoRateToDb();

        StepVerifier.create(result)
                .verifyComplete();

        verify(currencyRepository, times(2)).save(any(Currency.class));
        verify(valueOperations, never()).set(anyString(), any(), any());
    }
}