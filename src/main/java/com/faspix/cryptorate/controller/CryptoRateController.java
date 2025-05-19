package com.faspix.cryptorate.controller;

import com.faspix.cryptorate.dto.ResponseConvertDTO;
import com.faspix.cryptorate.dto.ResponseHistoryDTO;
import com.faspix.cryptorate.service.CryptoRateService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Validated
public class CryptoRateController {

    private final CryptoRateService cryptoRateService;

    @GetMapping("/convert")
    public Flux<ResponseConvertDTO> convert (
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @Positive(message = "Amount must be greater then 0") BigDecimal amount
    ) {
        return cryptoRateService.convert(from, to, amount);
    }

    @GetMapping("/history")
    public Flux<ResponseHistoryDTO> getHistory (
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return cryptoRateService.getHistory(from, to, startDate, endDate);
    }

}
