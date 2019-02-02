package b2b.repository;

import b2b.model.Basket;
import b2b.model.BasketStatus;
import b2b.model.Product;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Optional;

import static b2b.model.BasketStatus.PENDING;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class CustomBasketRepositoryImpl implements CustomBasketRepository {

    private final static String ID = "_id";
    private final static String PRODUCTS = "products";
    private final static String QUANTITY = "quantity";
    private final static String STATUS = "status";
    private final static String LAST_MODIFIED_DATE = "lastModifiedDate";

    private final static FindAndModifyOptions OPTIONS = new FindAndModifyOptions().returnNew(true).upsert(false);

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

    @Override
    public Optional<Basket> insertProduct(String basketId, Product product) {
        Query q = query(withIdPending(basketId).and(PRODUCTS +"."+ ID).ne(product.getId()));
        Update u = update().push(PRODUCTS, product);
        return Optional.ofNullable(mongoOperations.findAndModify(q, u, OPTIONS, Basket.class));
    }

    @Override
    public Optional<Basket> updateProduct(String basketId, Product product) {
        Query q = query(withIdPending(basketId).and(PRODUCTS +"."+ ID).is(product.getId()));
        Update u = update().set(PRODUCTS +".$."+ QUANTITY, product.getQuantity());
        return Optional.ofNullable(mongoOperations.findAndModify(q, u, OPTIONS, Basket.class));
    }

    @Override
    public Optional<Basket> removeProduct(String basketId, Product product) {
        Query q = query(withIdPending(basketId));
        Update u = update().pull(PRODUCTS, new Document(ID, product.getId()));
        return Optional.ofNullable(mongoOperations.findAndModify(q, u, OPTIONS, Basket.class));
    }

    @Override
    public Optional<Basket> setStatus(String basketId, BasketStatus status) {
        Query q = query(withIdPending(basketId));
        Update u = update().set(STATUS, status);
        return Optional.ofNullable(mongoOperations.findAndModify(q, u, OPTIONS, Basket.class));
    }

    private static Criteria withIdAndStatus(String id, BasketStatus status) {
        return where(ID).is(id).and(STATUS).is(status);
    }

    private static Criteria withIdPending(String id) {
        return withIdAndStatus(id, PENDING);
    }

    private static Update update() {
        return new Update().currentDate(LAST_MODIFIED_DATE);
    }
}
