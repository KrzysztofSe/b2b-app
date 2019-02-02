package b2b.controller;

import b2b.model.Basket;
import b2b.model.Product;
import b2b.model.VatNumberResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static b2b.model.BasketStatus.DELETED;
import static b2b.model.BasketStatus.ORDERED;
import static b2b.model.BasketStatus.PENDING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentMatchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class BasketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoOperations mongoOps;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    @Before
    public void before() {
        mongoOps.dropCollection("basket");
    }

    @Test
    public void shouldCreateNewBasket() throws Exception {
        MvcResult result = mockMvc.perform(post("/basket"))
                .andExpect(status().isOk())
                .andReturn();

        String resultId = result.getResponse().getContentAsString();

        assertThat(mongoOps.findById(resultId, Basket.class), is(not(equalTo(null))));
    }

    @Test
    public void shouldGetExistingBasket() throws Exception {
        Basket basket = new Basket("1", PENDING, Sets.newHashSet());

        mongoOps.insert(basket);

        MvcResult result = mockMvc.perform(get("/basket/{id}", basket.getId()))
                .andExpect(status().isOk())
                .andReturn();

        Basket resultBasket = objectMapper.readValue(result.getResponse().getContentAsString(), Basket.class);

        assertThat(resultBasket, is(basket));
    }

    @Test
    public void shouldNotAllowInvalidProduct() throws Exception {
        Product product = new Product("1", -1);
        Basket basket = new Basket("1", PENDING, Sets.newHashSet());

        mongoOps.insert(basket);

        String json = objectMapper.writeValueAsString(product);
        mockMvc.perform(put("/basket/{id}/product", basket.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldRemoveProductFromBasket() throws Exception {
        Product product = new Product("1", 1);
        Basket basket = new Basket("1", PENDING, Sets.newHashSet(Lists.newArrayList(product)));

        mongoOps.insert(basket);

        String json = objectMapper.writeValueAsString(new Product("1", 0));

        MvcResult result = mockMvc.perform(put("/basket/{id}/product", basket.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andReturn();

        Basket resultBasket = objectMapper.readValue(result.getResponse().getContentAsString(), Basket.class);
        assertThat(resultBasket.getId(), is(basket.getId()));
        assertThat(resultBasket.getProducts(), not(hasItem(product)));

        Basket persistentBasket = mongoOps.findById(basket.getId(), Basket.class);
        assertThat(persistentBasket.getProducts(), not(hasItem(product)));
        assertThat(persistentBasket.getLastModifiedDate(), greaterThan(basket.getLastModifiedDate()));

    }

    @Test
    public void shouldAddProductToBasket() throws Exception {
        Product product = new Product("1", 1);
        Basket basket = new Basket("1", PENDING, Sets.newHashSet());

        mongoOps.insert(basket);

        String json = objectMapper.writeValueAsString(product);

        MvcResult result = mockMvc.perform(put("/basket/{id}/product", basket.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andReturn();

        Basket resultBasket = objectMapper.readValue(result.getResponse().getContentAsString(), Basket.class);
        assertThat(resultBasket.getId(), is(basket.getId()));
        assertThat(resultBasket.getProducts(), containsInAnyOrder(product));

        Basket persistentBasket = mongoOps.findById(basket.getId(), Basket.class);
        assertThat(persistentBasket.getProducts(), containsInAnyOrder(product));
        assertThat(persistentBasket.getLastModifiedDate(), greaterThan(basket.getLastModifiedDate()));
    }

    @Test
    public void shouldUpdateProductInBasket() throws Exception {
        Product product = new Product("1", 1);
        Product updatedProduct = new Product("1", 10);
        Basket basket = new Basket("1", PENDING, Sets.newHashSet(Lists.newArrayList(product)));

        mongoOps.insert(basket);

        String json = objectMapper.writeValueAsString(updatedProduct);

        MvcResult result = mockMvc.perform(put("/basket/{id}/product", basket.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andReturn();

        Basket resultBasket = objectMapper.readValue(result.getResponse().getContentAsString(), Basket.class);
        assertThat(resultBasket.getId(), is(basket.getId()));
        assertThat(resultBasket.getProducts(), containsInAnyOrder(updatedProduct));
        assertThat(resultBasket.getProducts().iterator().next().getQuantity(), is(updatedProduct.getQuantity()));

        Basket persistentBasket = mongoOps.findById(basket.getId(), Basket.class);
        assertThat(persistentBasket.getProducts(), containsInAnyOrder(updatedProduct));
        assertThat(persistentBasket.getProducts().iterator().next().getQuantity(), is(updatedProduct.getQuantity()));
        assertThat(persistentBasket.getLastModifiedDate(), greaterThan(basket.getLastModifiedDate()));
    }

    @Test
    public void shouldDeleteBasket() throws Exception {
        Basket basket = new Basket("1", PENDING, Sets.newHashSet());

        mongoOps.insert(basket);

        MvcResult result = mockMvc.perform(delete("/basket/{id}", basket.getId()))
                .andExpect(status().isOk())
                .andReturn();

        Basket resultBasket = objectMapper.readValue(result.getResponse().getContentAsString(), Basket.class);
        assertThat(resultBasket, is(basket));

        Basket persistentBasket = mongoOps.findById(basket.getId(), Basket.class);
        assertThat(persistentBasket.getStatus(), is(DELETED));
        assertThat(persistentBasket.getLastModifiedDate(), greaterThan(basket.getLastModifiedDate()));
    }

    @Test
    public void shouldThrowExceptionWhenVatNumberInvalid() throws Exception {
        VatNumberResponse body = new VatNumberResponse();
        body.setValid(false);
        ResponseEntity<VatNumberResponse> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.getForEntity(ArgumentMatchers.any(URI.class), ArgumentMatchers.eq(VatNumberResponse.class)))
                .thenReturn(response);

        Basket basket = new Basket("1", PENDING, Sets.newHashSet());

        mongoOps.insert(basket);

        mockMvc.perform(post("/basket/{id}/order?vatNumber={vat}", basket.getId(), "12345"))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void shouldOrderBasket() throws Exception {
        VatNumberResponse body = new VatNumberResponse();
        body.setValid(true);
        ResponseEntity<VatNumberResponse> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.getForEntity(ArgumentMatchers.any(URI.class), ArgumentMatchers.eq(VatNumberResponse.class)))
                .thenReturn(response);

        Product product = new Product("1", 1);
        Basket basket = new Basket("1", PENDING, Sets.newHashSet(Lists.newArrayList(product)));

        mongoOps.insert(basket);

        MvcResult result = mockMvc.perform(post("/basket/{id}/order?vatNumber={vat}",
                basket.getId(), "12345"))
                .andExpect(status().isOk())
                .andReturn();

        Basket resultBasket = objectMapper.readValue(result.getResponse().getContentAsString(), Basket.class);
        assertThat(resultBasket.getId(), is(basket.getId()));

        Basket persistentBasket = mongoOps.findById(basket.getId(), Basket.class);
        assertThat(persistentBasket.getStatus(), is(ORDERED));
        assertThat(persistentBasket.getLastModifiedDate(), greaterThan(basket.getLastModifiedDate()));
    }

    @Test
    public void shouldReturn404ForNonExistentBasket() throws Exception {
        VatNumberResponse body = new VatNumberResponse();
        body.setValid(true);
        ResponseEntity<VatNumberResponse> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.getForEntity(ArgumentMatchers.any(URI.class), ArgumentMatchers.eq(VatNumberResponse.class)))
                .thenReturn(response);

        // get basket
        mockMvc.perform(get("/basket/{id}", "i-am-not-in-the-db"))
                .andExpect(status().isNotFound());

        // update product
        Product product = new Product("1", 1);
        String json = objectMapper.writeValueAsString(product);
        mockMvc.perform(put("/basket/{id}/product", "i-am-not-in-the-db")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());

        // delete basket
        mockMvc.perform(delete("/basket/{id}", "i-am-not-in-the-db"))
                .andExpect(status().isNotFound());

        // order basket
        mockMvc.perform(post("/basket/{id}/order?vatNumber={vat}", "i-am-not-in-the-db", "12345"))
                .andExpect(status().isNotFound());
    }

}