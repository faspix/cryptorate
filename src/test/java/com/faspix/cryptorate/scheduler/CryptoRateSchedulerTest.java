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
public class CryptoRateSchedulerTest {

    @Mock
    private UpdateRateService updateRateService;

    @InjectMocks
    private CryptoRateCronScheduler cryptoRateScheduler;

    @Test
    void testUpdateCryptoRates_Success() {
        when(updateRateService.updateCryptoRate()).thenReturn(Mono.empty());

        cryptoRateScheduler.updateCryptoRates();

        verify(updateRateService, times(1)).updateCryptoRate();
        verifyNoMoreInteractions(updateRateService);
    }

    @Test
    void testUpdateCryptoRates_Error() {
        when(updateRateService.updateCryptoRate()).thenReturn(Mono.error(new RuntimeException("Update failed")));

        cryptoRateScheduler.updateCryptoRates();

        verify(updateRateService, times(1)).updateCryptoRate();
        verifyNoMoreInteractions(updateRateService);
    }

    @Test
    void testSaveCryptoHistory_Success() {
        when(updateRateService.saveCryptoRateToDb()).thenReturn(Mono.empty());

        cryptoRateScheduler.saveCryptoHistory();

        verify(updateRateService, times(1)).saveCryptoRateToDb();
        verifyNoMoreInteractions(updateRateService);
    }

    @Test
    void testSaveCryptoHistory_Error() {
        when(updateRateService.saveCryptoRateToDb()).thenReturn(Mono.error(new RuntimeException("Save failed")));

        cryptoRateScheduler.saveCryptoHistory();

        verify(updateRateService, times(1)).saveCryptoRateToDb();
        verifyNoMoreInteractions(updateRateService);
    }
}
