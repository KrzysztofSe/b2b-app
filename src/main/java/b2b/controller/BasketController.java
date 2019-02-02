package b2b.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/basket")
public class BasketController {

    @RequestMapping(method = RequestMethod.GET)
    public String createBasket() {
        return "Hello";
    }

}
