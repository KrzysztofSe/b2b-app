package b2b.repository;

import b2b.model.Basket;
import b2b.model.BasketStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static b2b.model.BasketStatus.EXPIRED;
import static b2b.model.BasketStatus.PENDING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

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

        Basket basket = new Basket();
        basket.setId(correctId);
        basket.setStatus(correctStatus);

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

}