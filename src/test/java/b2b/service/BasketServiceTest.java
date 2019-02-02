package b2b.service;

import b2b.exception.BasketNotFoundException;
import b2b.model.Basket;
import b2b.model.BasketStatus;
import b2b.repository.BasketRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static b2b.model.BasketStatus.PENDING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @InjectMocks
    private BasketService basketService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldCreateNewBasket() {
        Basket basket = new Basket();
        basket.setId("1");

        when(basketRepository.insert(any(Basket.class))).thenReturn(basket);

        Basket result = basketService.createBasket();

        assertThat(result, is(basket));

        verify(basketRepository, times(1)).insert(any(Basket.class));
        verifyNoMoreInteractions(basketRepository);
    }

    @Test
    public void shouldGetActiveBasket() {
        Basket basket = new Basket();
        basket.setId("1");

        when(basketRepository.findByIdAndStatus(basket.getId(), PENDING)).thenReturn(Optional.of(basket));

        Basket result = basketService.getActiveBasket(basket.getId());

        assertThat(result, is(basket));

        verify(basketRepository, times(1)).findByIdAndStatus(basket.getId(), PENDING);
        verifyNoMoreInteractions(basketRepository);
    }

    @Test
    public void shouldThrowExceptionWhenBasketNotFound() {
        String id = "1";

        when(basketRepository.findByIdAndStatus(id, PENDING)).thenReturn(Optional.empty());

        expectedException.expect(BasketNotFoundException.class);
        expectedException.expectMessage("No active basket found with id " + id);
        basketService.getActiveBasket(id);

        verify(basketRepository, times(1)).findByIdAndStatus(id, PENDING);
        verifyNoMoreInteractions(basketRepository);
    }



}