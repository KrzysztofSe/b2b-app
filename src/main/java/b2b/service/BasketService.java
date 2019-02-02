package b2b.service;

import b2b.exception.BasketNotFoundException;
import b2b.model.Basket;
import b2b.model.Product;
import b2b.repository.BasketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

import static b2b.model.BasketStatus.DELETED;
import static b2b.model.BasketStatus.PENDING;

@Service
public class BasketService {

    private static final Logger LOG = LoggerFactory.getLogger(BasketService.class);

    private final BasketRepository basketRepository;

    @Autowired
    public BasketService(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    public Basket createBasket() {
        LOG.info("Creating new basket");
        return basketRepository.insert(new Basket());
    }

    public Basket getActiveBasket(String basketId) {
        LOG.info("Getting active basket with id {}", basketId);
        return execute(() -> basketRepository.findByIdAndStatus(basketId, PENDING), basketId);
    }

    public Basket updateProductInBasket(String basketId, Product product) {
        if (product.getQuantity() == 0) {
            LOG.info("Removing {} from basket with id {}", product, basketId);
            return execute(() -> basketRepository.removeProduct(basketId, product), basketId);
        }
        LOG.info("Updating {} in basket with id {}", product, basketId);
        return basketRepository.insertProduct(basketId, product)
                .orElseGet(() -> execute(() -> basketRepository.updateProduct(basketId, product), basketId));
    }

    public Basket deleteBasket(String basketId) {
        return execute(() -> basketRepository.setStatus(basketId, DELETED), basketId);
    }

    private Basket execute(Supplier<Optional<Basket>> function, String id) {
        return function.get().orElseThrow(() -> new BasketNotFoundException("No active basket found with id " + id));
    }


}
