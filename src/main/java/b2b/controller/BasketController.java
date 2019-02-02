package b2b.controller;

import b2b.model.Basket;
import b2b.model.Product;
import b2b.service.BasketService;
import b2b.service.ProductValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/basket")
public class BasketController {

    private static final Logger LOG = LoggerFactory.getLogger(BasketController.class);

    private final BasketService basketService;
    private final ProductValidator productValidator;

    @Autowired
    public BasketController(BasketService basketService, ProductValidator productValidator) {
        this.basketService = basketService;
        this.productValidator = productValidator;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createBasket() {
        LOG.info("Received basket creation request");
        return basketService.createBasket().getId();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Basket getBasket(@PathVariable String id) {
        LOG.info("Received basket retrieval request for id {}", id);
        return basketService.getActiveBasket(id);
    }

    @RequestMapping(path = "/{id}/product", method = RequestMethod.PUT)
    public Basket updateProductInBasket(@PathVariable String id, @RequestBody Product product) {
        LOG.info("Received basket update request for id {}, {}", id, product);
        productValidator.validate(product);
        return basketService.updateProductInBasket(id, product);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public Basket deleteBasket(@PathVariable String id) {
        return basketService.deleteBasket(id);
    }

}
