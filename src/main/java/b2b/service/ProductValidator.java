package b2b.service;

import b2b.exception.InvalidProductException;
import b2b.model.Product;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class ProductValidator {

    public void validate(final Product product) {
        if (isNull(product)) throw new InvalidProductException("The product cannot be null");
        if (isNull(product.getId())) throw new InvalidProductException("The product cannot have null id");
        if (isNull(product.getQuantity())) throw new InvalidProductException("The product cannot have null quantity");
        if (product.getQuantity() < 0) throw new InvalidProductException("The product cannot have negative quantity");
    }
}
