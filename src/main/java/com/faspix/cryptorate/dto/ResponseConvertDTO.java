package com.faspix.cryptorate.dto;

import java.math.BigDecimal;

public record ResponseConvertDTO(
        String from,
        String to,
        BigDecimal amount,
        BigDecimal convertedAmount,
        BigDecimal rate
) {}
