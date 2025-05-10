package com.faspix.cryptorate.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Document(collation = "currency_history")
public class Currency {

    @Id
    private String id;

    @Indexed
    private String currency;

    private LocalDateTime timestamp;

    private BigDecimal exchangeRateToUSD;

}
