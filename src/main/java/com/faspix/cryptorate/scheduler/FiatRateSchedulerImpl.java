package com.faspix.cryptorate.scheduler;

import com.faspix.cryptorate.service.UpdateRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FiatRateSchedulerImpl implements FiatRateScheduler {

    private final UpdateRateService updateRateService;

    @Override
    @Scheduled(cron = "${scheduler.fiat-rate-cron}")
    public void updateFiatRates() {
        updateRateService.updateFiatRate()
                .doOnError(e -> log.error("Failed to update fiat cache", e))
                .subscribe();
    }
}
