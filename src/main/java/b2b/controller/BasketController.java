package b2b.controller;

import b2b.model.Basket;
import b2b.model.Product;
import b2b.service.BasketService;
import b2b.service.ProductValidator;
import b2b.service.VatNumberValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/basket")
public class BasketController {

    private static final Logger LOG = LoggerFactory.getLogger(BasketController.class);

    private final BasketService basketService;
    private final ProductValidator productValidator;
    private final VatNumberValidator vatNumberValidator;

    @Autowired
    public BasketController(final BasketService basketService,
                            final ProductValidator productValidator,
                            final VatNumberValidator vatNumberValidator) {
        this.basketService = basketService;
        this.productValidator = productValidator;
        this.vatNumberValidator = vatNumberValidator;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> createBasket() {
        LOG.info("Received basket creation request");
        return new ResponseEntity<>(basketService.createBasket().getId(), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Basket getBasket(final @PathVariable String id) {
        LOG.info("Received basket retrieval request for id {}", id);
        return basketService.getActiveBasket(id);
    }

    @RequestMapping(path = "/{id}/product", method = RequestMethod.PUT)
    public Basket updateProductInBasket(final @PathVariable String id,
                                        final @RequestBody Product product) {
        LOG.info("Received basket update request for id {}, {}", id, product);
        productValidator.validate(product);
        return basketService.updateProductInBasket(id, product);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public Basket deleteBasket(final @PathVariable String id) {
        LOG.info("Received basket deletion request for id {}", id);
        return basketService.deleteBasket(id);
    }

    @RequestMapping(path = "/{id}/order", method = RequestMethod.POST)
    public Basket orderBasket(final @PathVariable String id,
                              final @RequestParam String vatNumber) {
        LOG.info("Received basket order request for id {}, vatNumber {}", id, vatNumber);
        vatNumberValidator.validate(vatNumber);
        return basketService.orderBasket(id);
    }
}
