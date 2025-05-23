package com.faspix.cryptorate.scheduler;

import com.faspix.cryptorate.service.UpdateRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FiatRateSchedulerTest {

    @Mock
    private UpdateRateService updateRateService;

    @InjectMocks
    private FiatRateCronScheduler fiatRateScheduler;

    @Test
    void testUpdateFiatRates_Success() {
        when(updateRateService.updateFiatRate()).thenReturn(Mono.empty());

        fiatRateScheduler.updateFiatRates();

        verify(updateRateService, times(1)).updateFiatRate();
        verifyNoMoreInteractions(updateRateService);
    }

    @Test
    void testUpdateFiatRates_Error() {
        when(updateRateService.updateFiatRate()).thenReturn(Mono.error(new RuntimeException("Fiat update failed")));

        fiatRateScheduler.updateFiatRates();

        verify(updateRateService, times(1)).updateFiatRate();
        verifyNoMoreInteractions(updateRateService);
    }
}
