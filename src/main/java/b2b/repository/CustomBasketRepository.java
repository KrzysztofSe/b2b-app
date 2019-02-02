package b2b.repository;

import b2b.model.Basket;
import b2b.model.BasketStatus;

import java.util.Optional;

public interface CustomBasketRepository {
    Optional<Basket> findByIdAndStatus(String basketId, BasketStatus status);
}
