package com.faspix.cryptorate.config;

import com.faspix.cryptorate.service.UpdateRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmUp {

    private final UpdateRateService updateRateService;

    @EventListener(ApplicationReadyEvent.class)
    void warmUpCache() {
        updateRateService.updateFiatRate().subscribe();
        updateRateService.updateCryptoRate().subscribe();
        log.info("Cache warmup completed.");
    }

}
