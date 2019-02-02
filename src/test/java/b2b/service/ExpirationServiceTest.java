package b2b.service;

import b2b.model.BasketStatus;
import b2b.repository.BasketRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ExpirationServiceTest {

    @Mock
    private BasketRepository basketRepository;

    private ExpirationService expirationService;

    @Before
    public void before() {
        expirationService = new ExpirationService(30, basketRepository);
    }

    @Test
    public void shouldExpireAbandonedBaskets() {
        expirationService.expireAbandonedBaskets();
        verify(basketRepository, times(1)).setStatusForOlderThan(any(LocalDateTime.class), eq(BasketStatus.EXPIRED));
        verifyNoMoreInteractions(basketRepository);
    }

}