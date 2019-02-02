package b2b.service;

import b2b.model.Basket;
import b2b.repository.BasketRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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



}