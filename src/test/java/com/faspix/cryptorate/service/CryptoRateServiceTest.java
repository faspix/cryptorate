package com.faspix.cryptorate.service;

import com.faspix.cryptorate.dto.ResponseHistoryDTO;
import com.faspix.cryptorate.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static com.faspix.cryptorate.utility.CurrencyFactory.makeCurrency;
import static com.faspix.cryptorate.utility.CurrencyFactory.makeHistoryDTO;

@ExtendWith(MockitoExtension.class)
public class CryptoRateServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CryptoRateService cryptoRateService;

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
