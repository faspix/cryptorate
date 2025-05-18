package com.faspix.cryptorate.scheduler;

import com.faspix.cryptorate.service.UpdateRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CryptoRateSchedulerImpl implements CryptoRateScheduler {

    private final UpdateRateService updateRateService;

    @Override
    @Scheduled(cron = "${scheduler.crypto-rate-cron}")
    public void updateCryptoRates() {
        updateRateService.updateCryptoRate()
                .doOnError(e -> log.error("Failed to update crypto cache", e))
                .subscribe();
    }

    @Override
    @Scheduled(cron = "${scheduler.crypto-save-history-cron}")
    public void saveCryptoHistory() {
        updateRateService.saveCryptoRateToDb()
                .doOnError(e -> log.error("Failed to save crypto history", e))
                .subscribe();
    }
}
