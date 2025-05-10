package com.faspix.cryptorate.controller;

import com.faspix.cryptorate.dto.ResponseConvertDTO;
import com.faspix.cryptorate.dto.ResponseHistoryDTO;
import com.faspix.cryptorate.service.CryptoRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class CryptoRateController {

    private final CryptoRateService cryptoRateService;

    @GetMapping("/convert")
    public Flux<ResponseConvertDTO> convert (
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount
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
