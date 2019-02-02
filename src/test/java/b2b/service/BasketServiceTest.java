package b2b.service;

import b2b.exception.BasketNotFoundException;
import b2b.model.Basket;
import b2b.model.BasketStatus;
import b2b.model.Product;
import b2b.repository.BasketRepository;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static b2b.model.BasketStatus.DELETED;
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
        Basket basket = new Basket("1", PENDING, Sets.newHashSet());

        when(basketRepository.insert(any(Basket.class))).thenReturn(basket);

        Basket result = basketService.createBasket();

        assertThat(result, is(basket));

        verify(basketRepository, times(1)).insert(any(Basket.class));
        verifyNoMoreInteractions(basketRepository);
    }

    @Test
    public void shouldGetActiveBasket() {
        Basket basket = new Basket("1", PENDING, Sets.newHashSet());

        when(basketRepository.findByIdAndStatus(basket.getId(), PENDING)).thenReturn(Optional.of(basket));

        Basket result = basketService.getActiveBasket(basket.getId());

        assertThat(result, is(basket));

        verify(basketRepository, times(1)).findByIdAndStatus(basket.getId(), PENDING);
        verifyNoMoreInteractions(basketRepository);
    }

    @Test
    public void shouldThrowExceptionWhenBasketNotFound() {
        String basketId = "1";

        when(basketRepository.findByIdAndStatus(basketId, PENDING)).thenReturn(Optional.empty());

        expectedException.expect(BasketNotFoundException.class);
        expectedException.expectMessage("No active basket found with id " + basketId);
        basketService.getActiveBasket(basketId);

        verify(basketRepository, times(1)).findByIdAndStatus(basketId, PENDING);
        verifyNoMoreInteractions(basketRepository);
    }

    @Test
    public void shouldRemoveProductWhenGivenQuantityIs0() {
        Basket expectedBasket = new Basket("1", PENDING, Sets.newHashSet());
        Product product = new Product("1", 0);

        when(basketRepository.removeProduct(expectedBasket.getId(), product)).thenReturn(Optional.of(expectedBasket));

        Basket result = basketService.updateProductInBasket(expectedBasket.getId(), product);

        assertThat(result, is(expectedBasket));

        verify(basketRepository, times(1)).removeProduct(expectedBasket.getId(), product);
        verifyNoMoreInteractions(basketRepository);
    }

    @Test
    public void shouldThrowExceptionWhenTryingToRemoveProductFromNonExistentBasket() {
        String basketId = "1";
        Product product = new Product("1", 0);

        when(basketRepository.removeProduct(basketId, product)).thenReturn(Optional.empty());

        expectedException.expect(BasketNotFoundException.class);
        expectedException.expectMessage("No active basket found with id " + basketId);
        basketService.updateProductInBasket(basketId, product);

        verify(basketRepository, times(1)).removeProduct(basketId, product);
        verifyNoMoreInteractions(basketRepository);
    }

    @Test
    public void shouldInsertProductToBasket() {
        Product product = new Product("1", 1);
        Basket expectedBasket = new Basket("1", PENDING, Sets.newHashSet(Lists.newArrayList(product)));

        when(basketRepository.insertProduct(expectedBasket.getId(), product)).thenReturn(Optional.of(expectedBasket));

        Basket result = basketService.updateProductInBasket(expectedBasket.getId(), product);

        assertThat(result, is(expectedBasket));

        verify(basketRepository, times(1)).insertProduct(expectedBasket.getId(), product);
        verifyNoMoreInteractions(basketRepository);
    }

    @Test
    public void shouldUpdateProductInBasket() {
        Product product = new Product("1", 1);
        Basket expectedBasket = new Basket("1", PENDING, Sets.newHashSet(Lists.newArrayList(product)));

        when(basketRepository.insertProduct(expectedBasket.getId(), product)).thenReturn(Optional.empty());
        when(basketRepository.updateProduct(expectedBasket.getId(), product)).thenReturn(Optional.of(expectedBasket));

        Basket result = basketService.updateProductInBasket(expectedBasket.getId(), product);

        assertThat(result, is(expectedBasket));

        verify(basketRepository, times(1)).insertProduct(expectedBasket.getId(), product);
        verify(basketRepository, times(1)).updateProduct(expectedBasket.getId(), product);
        verifyNoMoreInteractions(basketRepository);
    }

    @Test
    public void shouldThrowExceptionWhenTryingToEditProductInNonExistentBasket() {
        String basketId = "1";
        Product product = new Product("1", 1);

        when(basketRepository.insertProduct(basketId, product)).thenReturn(Optional.empty());
        when(basketRepository.updateProduct(basketId, product)).thenReturn(Optional.empty());

        expectedException.expect(BasketNotFoundException.class);
        expectedException.expectMessage("No active basket found with id " + basketId);
        basketService.updateProductInBasket(basketId, product);

        verify(basketRepository, times(1)).insertProduct(basketId, product);
        verify(basketRepository, times(1)).updateProduct(basketId, product);
        verifyNoMoreInteractions(basketRepository);
    }

    @Test
    public void shouldDeleteBasket() {
        Basket expectedBasket = new Basket("1", DELETED, Sets.newHashSet());

        when(basketRepository.setStatus(expectedBasket.getId(), DELETED)).thenReturn(Optional.of(expectedBasket));

        Basket result = basketService.deleteBasket(expectedBasket.getId());

        assertThat(result, is(expectedBasket));

        verify(basketRepository, times(1)).setStatus(expectedBasket.getId(), DELETED);
        verifyNoMoreInteractions(basketRepository);
    }

    @Test
    public void shouldThrowExceptionWhenTryingToDeleteNonExistentBasket() {
        String basketId = "1";

        when(basketRepository.setStatus(basketId, DELETED)).thenReturn(Optional.empty());

        expectedException.expect(BasketNotFoundException.class);
        expectedException.expectMessage("No active basket found with id " + basketId);
        basketService.deleteBasket(basketId);

        verify(basketRepository, times(1)).setStatus(basketId, DELETED);
        verifyNoMoreInteractions(basketRepository);
    }

}