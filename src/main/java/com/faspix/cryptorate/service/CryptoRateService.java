package com.faspix.cryptorate.service;

import com.faspix.cryptorate.dto.HistoryItem;
import com.faspix.cryptorate.dto.ResponseConvertDTO;
import com.faspix.cryptorate.dto.ResponseHistoryDTO;
import com.faspix.cryptorate.entity.Currency;
import com.faspix.cryptorate.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CryptoRateService {

    private final CurrencyRepository currencyRepository;

    @Transactional(readOnly = true)
    public Flux<ResponseConvertDTO> convert(String from, String to, BigDecimal amount) {
        return null;
    }

    @Transactional(readOnly = true)
    public Flux<ResponseHistoryDTO> getHistory(String from, String to, LocalDate startDate, LocalDate endDate) {
        Flux<Currency> dataFrom = currencyRepository.findByCurrencyAndTimestampBetween(
                from,
                startDate.atStartOfDay(),
                endDate.atStartOfDay()
        );
        Flux<Currency> dataTo = currencyRepository.findByCurrencyAndTimestampBetween(
                to,
                startDate.atStartOfDay(),
                endDate.atStartOfDay()
        );

        return dataFrom.zipWith(dataTo, (currencyFrom, currencyTo) -> {
            BigDecimal exchangeRateFrom = currencyFrom.getExchangeRateToUSD();
            BigDecimal exchangeRateTo = currencyTo.getExchangeRateToUSD();

            BigDecimal exchangeRate = exchangeRateFrom.divide(exchangeRateTo, 10, RoundingMode.HALF_UP);

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
