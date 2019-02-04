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

import java.time.LocalDateTime;
import java.util.Optional;

import static b2b.model.BasketStatus.PENDING;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class CustomBasketRepositoryImpl implements CustomBasketRepository {

    private static final String ID = "_id";
    private static final String PRODUCTS = "products";
    private static final String QUANTITY = "quantity";
    private static final String STATUS = "status";
    private static final String LAST_MODIFIED_DATE = "lastModifiedDate";

    private static final FindAndModifyOptions OPTIONS = new FindAndModifyOptions().returnNew(true).upsert(false);

    private final MongoOperations mongoOperations;

    @Autowired
    public CustomBasketRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Optional<Basket> findByIdAndStatus(final String basketId,
                                              final BasketStatus status) {
        Query q = query(withIdAndStatus(basketId, status));
        return Optional.ofNullable(mongoOperations.findOne(q, Basket.class));
    }

    @Override
    public Optional<Basket> insertProduct(final String basketId,
                                          final Product product) {
        Query q = query(withIdPending(basketId).and(PRODUCTS + "." + ID).ne(product.getId()));
        Update u = update().push(PRODUCTS, product);
        return Optional.ofNullable(mongoOperations.findAndModify(q, u, OPTIONS, Basket.class));
    }

    @Override
    public Optional<Basket> updateProduct(final String basketId,
                                          final Product product) {
        Query q = query(withIdPending(basketId).and(PRODUCTS + "." + ID).is(product.getId()));
        Update u = update().set(PRODUCTS + ".$." + QUANTITY, product.getQuantity());
        return Optional.ofNullable(mongoOperations.findAndModify(q, u, OPTIONS, Basket.class));
    }

    @Override
    public Optional<Basket> removeProduct(final String basketId,
                                          final Product product) {
        Query q = query(withIdPending(basketId));
        Update u = update().pull(PRODUCTS, new Document(ID, product.getId()));
        return Optional.ofNullable(mongoOperations.findAndModify(q, u, OPTIONS, Basket.class));
    }

    @Override
    public Optional<Basket> setStatus(final String basketId,
                                      final BasketStatus status) {
        Query q = query(withIdPending(basketId));
        Update u = update().set(STATUS, status);
        return Optional.ofNullable(mongoOperations.findAndModify(q, u, OPTIONS, Basket.class));
    }

    @Override
    public Optional<Basket> setStatusForBasketWithProducts(final String basketId,
                                                           final BasketStatus status) {
        Query q = query(withIdPending(basketId).and(PRODUCTS).not().size(0));
        Update u = update().set(STATUS, status);
        return Optional.ofNullable(mongoOperations.findAndModify(q, u, OPTIONS, Basket.class));
    }

    @Override
    public long setStatusForOlderThan(final LocalDateTime date,
                                      final BasketStatus status) {
        Query q = query(where(STATUS).is(PENDING).and(LAST_MODIFIED_DATE).lt(date));
        Update u = update().set(STATUS, status);
        return mongoOperations.updateMulti(q, u, Basket.class).getModifiedCount();
    }

    private static Criteria withIdAndStatus(final String id,
                                            final BasketStatus status) {
        return where(ID).is(id).and(STATUS).is(status);
    }

    private static Criteria withIdPending(final String id) {
        return withIdAndStatus(id, PENDING);
    }

    private static Update update() {
        return new Update().currentDate(LAST_MODIFIED_DATE);
    }
}
