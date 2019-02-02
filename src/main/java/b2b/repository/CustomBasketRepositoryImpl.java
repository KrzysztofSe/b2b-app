package b2b.repository;

import b2b.model.Basket;
import b2b.model.BasketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class CustomBasketRepositoryImpl implements CustomBasketRepository {

    private final static String ID = "_id";
    private final static String STATUS = "status";

    private final MongoOperations mongoOperations;

    @Autowired
    public CustomBasketRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Optional<Basket> findByIdAndStatus(String basketId, BasketStatus status) {
        Query q = query(withIdAndStatus(basketId, status));
        return Optional.ofNullable(mongoOperations.findOne(q, Basket.class));
    }

    private static Criteria withIdAndStatus(String id, BasketStatus status) {
        return where(ID).is(id).and(STATUS).is(status);
    }
}
