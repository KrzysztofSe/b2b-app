package b2b.repository;

import b2b.model.Basket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void shouldInsertNewBasket() {
        assertThat(mongoOps.findAll(Basket.class), hasSize(0));

        Basket result = basketRepository.insert(new Basket());

        assertThat(mongoOps.findAll(Basket.class), hasSize(1));
        assertThat(mongoOps.findById(result.getId(), Basket.class), is(result));
    }

}