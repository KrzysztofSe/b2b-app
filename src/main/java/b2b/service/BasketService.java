package b2b.service;

import b2b.exception.BasketNotFoundException;
import b2b.exception.InvalidBasketException;
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
import static b2b.model.BasketStatus.ORDERED;
import static b2b.model.BasketStatus.PENDING;

@Service
public class BasketService {

    private static final Logger LOG = LoggerFactory.getLogger(BasketService.class);

    private final BasketRepository basketRepository;

    @Autowired
    public BasketService(final BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    public Basket createBasket() {
        LOG.info("Creating new basket");
        return basketRepository.insert(new Basket());
    }

    public Basket getActiveBasket(final String basketId) {
        LOG.info("Getting active basket with id {}", basketId);
        return execute(() -> basketRepository.findByIdAndStatus(basketId, PENDING), basketId);
    }

    public Basket updateProductInBasket(final String basketId,
                                        final Product product) {
        if (product.getQuantity() == 0) {
            LOG.info("Removing {} from basket with id {}", product, basketId);
            return execute(() -> basketRepository.removeProduct(basketId, product), basketId);
        }
        LOG.info("Updating {} in basket with id {}", product, basketId);
        return basketRepository.insertProduct(basketId, product)
                .orElseGet(() -> execute(() -> basketRepository.updateProduct(basketId, product), basketId));
    }

    public Basket deleteBasket(final String basketId) {
        LOG.info("Deleting basket with id {}", basketId);
        return execute(() -> basketRepository.setStatus(basketId, DELETED), basketId);
    }

    public Basket orderBasket(final String basketId) {
        LOG.info("Ordering basket with id {}", basketId);
        Optional<Basket> opt = basketRepository.setStatusForBasketWithProducts(basketId, ORDERED);
        if (opt.isPresent()) {
            return opt.get();
        }
        execute(() -> basketRepository.findByIdAndStatus(basketId, PENDING), basketId);
        throw new InvalidBasketException("Basket with id " + basketId + " has no products");
    }

    private Basket execute(final Supplier<Optional<Basket>> function,
                           final String id) {
        return function.get().orElseThrow(() -> new BasketNotFoundException("No active basket found with id " + id));
    }
}
