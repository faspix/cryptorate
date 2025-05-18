package com.faspix.cryptorate.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Document(collection = "currency_history")
public class Currency {

    @Id
    private String id;

    @Indexed
    private String currency;

    private LocalDateTime timestamp;

    private BigDecimal exchangeRateToUSD;

    public Currency(String currency, LocalDateTime timestamp, BigDecimal exchangeRateToUSD) {
        this.currency = currency;
        this.timestamp = timestamp;
        this.exchangeRateToUSD = exchangeRateToUSD;
    }

}
