package com.faspix.cryptorate.utility;

import com.faspix.cryptorate.dto.HistoryItem;
import com.faspix.cryptorate.dto.ResponseHistoryDTO;
import com.faspix.cryptorate.entity.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

public class CurrencyFactory {

    public static Currency makeCurrency() {
        return new Currency(
                "1",
                "USD",
                LocalDateTime.MIN,
                BigDecimal.TEN
        );
    }

    public static ResponseHistoryDTO makeHistoryDTO() {
        return new ResponseHistoryDTO(
                "USD",
                "USD",
                Collections.singletonList(
                        new HistoryItem(LocalDateTime.MIN, new BigDecimal("1.000000000000000"))
                )
        );
    }

}
