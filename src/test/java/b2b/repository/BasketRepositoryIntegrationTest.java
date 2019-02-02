package b2b.repository;

import b2b.model.Basket;
import b2b.model.BasketStatus;
import b2b.model.Product;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static b2b.model.BasketStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataMongoTest
@RunWith(SpringRunner.class)
public class BasketRepositoryIntegrationTest {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private MongoOperations mongoOps;

    @Before
    public void before() {
        mongoOps.dropCollection("basket");
    }

    @Test
    public void shouldInsertNewBasket() {
        Basket result = basketRepository.insert(new Basket());

        assertThat(mongoOps.findAll(Basket.class), hasSize(1));
        assertThat(mongoOps.findById(result.getId(), Basket.class), is(result));
    }

    @Test
    public void shouldFindBasketByIdAndStatus() {
        String correctId = "1";
        BasketStatus correctStatus = PENDING;
        Basket basket = new Basket(correctId, PENDING, Sets.newHashSet());

        mongoOps.insert(basket);

        // should not be found - wrong id
        Optional<Basket> result1 = basketRepository.findByIdAndStatus("2", correctStatus);
        assertThat(result1.isPresent(), is(false));

        // should not be found - wrong status
        Optional<Basket> result2 = basketRepository.findByIdAndStatus(correctId, EXPIRED);
        assertThat(result2.isPresent(), is(false));

        // should be found
        Optional<Basket> result3 = basketRepository.findByIdAndStatus(correctId, correctStatus);
        assertThat(result3.isPresent(), is(true));
        assertThat(result3.get(), is(basket));
    }

    @Test
    public void shouldInsertProduct() {
        Product existingProduct = new Product("1", 1);
        Product duplicateProductToInsert = new Product("1", 100);
        Product correctProductToInsert = new Product("100", 100);

        Basket basket = new Basket("1", PENDING, Sets.newHashSet(Lists.newArrayList(existingProduct)));
        Basket expiredBasket = new Basket("2", EXPIRED, Sets.newHashSet());

        mongoOps.insert(basket);
        mongoOps.insert(expiredBasket);

        // should not insert - wrong basket id
        Optional<Basket> result1 = basketRepository.insertProduct("xxx", correctProductToInsert);
        assertThat(result1.isPresent(), is(false));

        // should not insert - basket expired
        Optional<Basket> result2 = basketRepository.insertProduct(expiredBasket.getId(), correctProductToInsert);
        assertThat(result2.isPresent(), is(false));

        // should not insert - product already added to basket
        Optional<Basket> result3 = basketRepository.insertProduct(basket.getId(), duplicateProductToInsert);
        assertThat(result3.isPresent(), is(false));

        // should insert
        Optional<Basket> result4 = basketRepository.insertProduct(basket.getId(), correctProductToInsert);
        assertThat(result4.isPresent(), is(true));

        Basket updatedBasket = result4.get();
        assertThat(updatedBasket, is(basket));
        assertThat(updatedBasket.getStatus(), is(basket.getStatus()));
        assertThat(updatedBasket.getLastModifiedDate(), greaterThan(basket.getLastModifiedDate()));
        assertThat(updatedBasket.getProducts(), containsInAnyOrder(existingProduct, correctProductToInsert));
    }

    @Test
    public void shouldUpdateProduct() {
        Product existingProduct = new Product("1", 1);
        Product correctProductToUpdate = new Product("1", 100);
        Product incorrectProductToUpdate = new Product("100", 100);

        Basket basket = new Basket("1", PENDING, Sets.newHashSet(Lists.newArrayList(existingProduct)));
        Basket expiredBasket = new Basket("2", EXPIRED, Sets.newHashSet());

        mongoOps.insert(basket);
        mongoOps.insert(expiredBasket);

        // should not update - wrong basket id
        Optional<Basket> result1 = basketRepository.updateProduct("xxx", correctProductToUpdate);
        assertThat(result1.isPresent(), is(false));

        // should not update - basket expired
        Optional<Basket> result2 = basketRepository.updateProduct(expiredBasket.getId(), correctProductToUpdate);
        assertThat(result2.isPresent(), is(false));

        // should not update - basket doesn't contain product
        Optional<Basket> result3 = basketRepository.updateProduct(basket.getId(), incorrectProductToUpdate);
        assertThat(result3.isPresent(), is(false));

        // should update
        Optional<Basket> result4 = basketRepository.updateProduct(basket.getId(), correctProductToUpdate);
        assertThat(result4.isPresent(), is(true));

        Basket updatedBasket = result4.get();
        assertThat(updatedBasket, is(basket));
        assertThat(updatedBasket.getStatus(), is(basket.getStatus()));
        assertThat(updatedBasket.getLastModifiedDate(), greaterThan(basket.getLastModifiedDate()));
        assertThat(updatedBasket.getProducts(), containsInAnyOrder(correctProductToUpdate));
        assertThat(updatedBasket.getProducts().iterator().next().getQuantity(), is(correctProductToUpdate.getQuantity()));
    }

    @Test
    public void shouldRemoveProduct() {
        Product product = new Product("1", 1);

        Basket basket = new Basket("1", PENDING, Sets.newHashSet(Lists.newArrayList(product)));
        Basket expiredBasket = new Basket("2", EXPIRED, Sets.newHashSet());

        mongoOps.insert(basket);
        mongoOps.insert(expiredBasket);

        // should not remove - wrong basket id
        Optional<Basket> result1 = basketRepository.removeProduct("xxx", product);
        assertThat(result1.isPresent(), is(false));

        // should not remove - basket expired
        Optional<Basket> result2 = basketRepository.removeProduct(expiredBasket.getId(), product);
        assertThat(result2.isPresent(), is(false));

        // should remove
        Optional<Basket> result3 = basketRepository.removeProduct(basket.getId(), product);
        assertThat(result3.isPresent(), is(true));

        Basket updatedBasket = result3.get();
        assertThat(updatedBasket, is(basket));
        assertThat(updatedBasket.getStatus(), is(basket.getStatus()));
        assertThat(updatedBasket.getLastModifiedDate(), greaterThan(basket.getLastModifiedDate()));
        assertThat(updatedBasket.getProducts(), not(hasItem(product)));
    }

    @Test
    public void shouldSetStatus() {
        Basket basket = new Basket("1", PENDING, Sets.newHashSet());
        Basket expiredBasket = new Basket("2", EXPIRED, Sets.newHashSet());

        mongoOps.insert(basket);
        mongoOps.insert(expiredBasket);

        // should not set status - wrong basket id
        Optional<Basket> result1 = basketRepository.setStatus("xxx", DELETED);
        assertThat(result1.isPresent(), is(false));

        // should not set status - basket expired
        Optional<Basket> result2 = basketRepository.setStatus(expiredBasket.getId(), DELETED);
        assertThat(result2.isPresent(), is(false));

        // should set status
        Optional<Basket> result3 = basketRepository.setStatus(basket.getId(), DELETED);
        assertThat(result3.isPresent(), is(true));

        Basket updatedBasket = result3.get();
        assertThat(updatedBasket, is(basket));
        assertThat(updatedBasket.getStatus(), is(DELETED));
        assertThat(updatedBasket.getLastModifiedDate(), greaterThan(basket.getLastModifiedDate()));
    }

    @Test
    public void shouldSetStatusForBasketWithProducts() {
        Product product = new Product("1", 1);

        Basket basketWithProducts = new Basket("1", PENDING, Sets.newHashSet(Lists.newArrayList(product)));
        Basket basketWithoutProducts = new Basket("2", PENDING, Sets.newHashSet());
        Basket expiredBasket = new Basket("3", EXPIRED, Sets.newHashSet());

        mongoOps.insert(basketWithProducts);
        mongoOps.insert(basketWithoutProducts);
        mongoOps.insert(expiredBasket);

        // should not update - wrong basket id
        Optional<Basket> result1 = basketRepository.setStatusForBasketWithProducts("xxx", ORDERED);
        assertThat(result1.isPresent(), is(false));

        // should not update - basket expired
        Optional<Basket> result2 = basketRepository.setStatusForBasketWithProducts(expiredBasket.getId(), ORDERED);
        assertThat(result2.isPresent(), is(false));

        // should not update - basket doesn't contain products
        Optional<Basket> result3 = basketRepository.setStatusForBasketWithProducts(basketWithoutProducts.getId(), ORDERED);
        assertThat(result3.isPresent(), is(false));

        // should update
        Optional<Basket> result4 = basketRepository.setStatusForBasketWithProducts(basketWithProducts.getId(), ORDERED);
        assertThat(result4.isPresent(), is(true));

        Basket updatedBasket = result4.get();
        assertThat(updatedBasket, is(basketWithProducts));
        assertThat(updatedBasket.getStatus(), is(ORDERED));
        assertThat(updatedBasket.getLastModifiedDate(), greaterThan(basketWithProducts.getLastModifiedDate()));
    }

    @Test
    public void shouldSetStatusForOldBaskets() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);

        Basket basket = new Basket("1", PENDING, Sets.newHashSet());

        Basket oldBasket = new Basket("2", PENDING, Sets.newHashSet());
        oldBasket.setLastModifiedDate(LocalDateTime.now().minusMinutes(60));

        Basket basketInWrongState = new Basket("3", DELETED, Sets.newHashSet());
        basketInWrongState.setLastModifiedDate(LocalDateTime.now().minusDays(10));

        mongoOps.insert(basket);
        mongoOps.insert(oldBasket);
        mongoOps.insert(basketInWrongState);

        long count = basketRepository.setStatusForOlderThan(threshold, EXPIRED);
        assertThat(count, is(1L));

        Basket basketAfter = mongoOps.findById(basket.getId(), Basket.class);
        assertThat(basketAfter.getStatus(), is(basket.getStatus()));
        assertThat(basketAfter.getLastModifiedDate(), is(basket.getLastModifiedDate()));

        Basket oldBasketAfter = mongoOps.findById(oldBasket.getId(), Basket.class);
        assertThat(oldBasketAfter.getStatus(), is(EXPIRED));
        assertThat(oldBasketAfter.getLastModifiedDate(), greaterThan(oldBasket.getLastModifiedDate()));

        Basket basketInWrongStateAfter = mongoOps.findById(basketInWrongState.getId(), Basket.class);
        assertThat(basketInWrongStateAfter.getStatus(), is(basketInWrongState.getStatus()));
        assertThat(basketInWrongStateAfter.getLastModifiedDate(), is(basketInWrongState.getLastModifiedDate()));
    }

}