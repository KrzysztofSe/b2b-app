package b2b.controller;

import b2b.service.BasketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/basket")
public class BasketController {

    private static final Logger LOG = LoggerFactory.getLogger(BasketController.class);

    private final BasketService basketService;

    @Autowired
    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createBasket() {
        LOG.info("Received basket creation request");
        return  basketService.createBasket().getId();
    }

}
