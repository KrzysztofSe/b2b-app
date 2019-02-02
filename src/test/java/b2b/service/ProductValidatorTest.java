package b2b.service;

import b2b.exception.InvalidProductException;
import b2b.model.Product;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ProductValidatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ProductValidator validator = new ProductValidator();

    @Test
    public void shouldThrowExceptionWhenProductIsNull() {
        expectedException.expect(InvalidProductException.class);
        expectedException.expectMessage("The product cannot be null");
        validator.validate(null);
    }

    @Test
    public void shouldThrowExceptionWhenProductHasNullId() {
        expectedException.expect(InvalidProductException.class);
        expectedException.expectMessage("The product cannot have null id");
        validator.validate(new Product());
    }

    @Test
    public void shouldThrowExceptionWhenProductHasNullQuantity() {
        Product product = new Product();
        product.setId("1");

        expectedException.expect(InvalidProductException.class);
        expectedException.expectMessage("The product cannot have null quantity");
        validator.validate(product);
    }

    @Test
    public void shouldThrowExceptionWhenProductHasNegativeQuantity() {
        Product product = new Product();
        product.setId("1");
        product.setQuantity(-1);

        expectedException.expect(InvalidProductException.class);
        expectedException.expectMessage("The product cannot have negative quantity");
        validator.validate(product);
    }

    @Test
    public void shouldValidateProduct() {
        Product product = new Product();
        product.setId("1");
        product.setQuantity(1);

        validator.validate(product);
    }

}