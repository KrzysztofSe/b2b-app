package b2b.service;

import b2b.model.Basket;
import b2b.repository.BasketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
