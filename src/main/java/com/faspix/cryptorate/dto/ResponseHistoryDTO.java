package com.faspix.cryptorate.dto;


import java.util.List;

public record ResponseHistoryDTO(
        String from,
        String to,
        List<HistoryItem> history
) {}
