package com.faspix.cryptorate.service;

import com.faspix.cryptorate.dto.HistoryItem;
import com.faspix.cryptorate.dto.ResponseConvertDTO;
import com.faspix.cryptorate.dto.ResponseHistoryDTO;
import com.faspix.cryptorate.entity.Currency;
import com.faspix.cryptorate.exception.ExchangeRateNotFoundException;
import com.faspix.cryptorate.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CryptoRateService {

    private final CurrencyRepository currencyRepository;

    private final ReactiveRedisTemplate<String, BigDecimal> reactiveRedisTemplate;

    @Transactional(readOnly = true)
    public Flux<ResponseConvertDTO> convert(String from, String to, BigDecimal amount) {
        Mono<BigDecimal> fromRateMono = reactiveRedisTemplate.opsForValue().get(from)
                .switchIfEmpty(Mono.error(new ExchangeRateNotFoundException("Exchange rate for '" + from + "' not found in cache")));

        Mono<BigDecimal> toRateMono = reactiveRedisTemplate.opsForValue().get(to)
                .switchIfEmpty(Mono.error(new ExchangeRateNotFoundException("Exchange rate for '" + to + "' not found in cache")));

        return Mono.zip(fromRateMono, toRateMono)
                .<ResponseConvertDTO>handle((tuple, sink) -> {
                    BigDecimal fromRate = tuple.getT1();
                    BigDecimal toRate = tuple.getT2();

                    BigDecimal rate = toRate.divide(fromRate, 15, RoundingMode.HALF_UP);
                    BigDecimal result = amount.multiply(rate);

                    sink.next(new ResponseConvertDTO(
                            from,
                            to,
                            amount,
                            result,
                            rate
                    ));
                })
                .flux();
    }

    @Transactional(readOnly = true)
    public Flux<ResponseHistoryDTO> getHistory(String from, String to, LocalDate startDate, LocalDate endDate) {
        Flux<Currency> dataFrom = currencyRepository.findByCurrencyAndTimestampBetween(
                from,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );
        Flux<Currency> dataTo = currencyRepository.findByCurrencyAndTimestampBetween(
                to,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        return dataFrom.zipWith(dataTo, (currencyFrom, currencyTo) -> {
            BigDecimal exchangeRateFrom = currencyFrom.getExchangeRateToUSD();
            BigDecimal exchangeRateTo = currencyTo.getExchangeRateToUSD();

            BigDecimal exchangeRate = exchangeRateFrom.divide(exchangeRateTo, 15, RoundingMode.HALF_UP);

            HistoryItem historyItem = new HistoryItem(
                    currencyFrom.getTimestamp(),
                    exchangeRate
            );

            return new ResponseHistoryDTO(
                    from,
                    to,
                    List.of(historyItem)
            );
        });
    }

}
