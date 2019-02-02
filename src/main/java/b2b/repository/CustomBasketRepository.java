package b2b.repository;

import b2b.model.Basket;
import b2b.model.BasketStatus;
import b2b.model.Product;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomBasketRepository {
    Optional<Basket> findByIdAndStatus(String basketId, BasketStatus status);

    Optional<Basket> removeProduct(String basketId, Product product);

    Optional<Basket> insertProduct(String basketId, Product product);

    Optional<Basket> updateProduct(String basketId, Product product);

    Optional<Basket> setStatus(String basketId, BasketStatus status);

    Optional<Basket> setStatusForBasketWithProducts(String basketId, BasketStatus status);

    long setStatusForOlderThan(LocalDateTime threshold, BasketStatus status);
}
