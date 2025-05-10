package com.faspix.cryptorate.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoryItem(
        LocalDateTime date,
        BigDecimal rate
) {}
