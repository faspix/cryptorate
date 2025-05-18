package com.faspix.cryptorate.repository;

import com.faspix.cryptorate.entity.Currency;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface CurrencyRepository extends ReactiveCrudRepository<Currency, String> {

    Flux<Currency> findByCurrencyAndTimestampBetween(String currency,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate
    );


    Mono<Currency> findTopByCurrencyOrderByTimestampDesc(String currency);

}
