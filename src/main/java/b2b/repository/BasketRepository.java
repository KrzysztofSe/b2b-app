package b2b.repository;

import b2b.model.Basket;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BasketRepository extends MongoRepository<Basket, String> {
}
