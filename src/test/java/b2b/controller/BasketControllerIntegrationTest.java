package b2b.controller;

import b2b.model.Basket;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        Basket basket = new Basket();
        basket.setId("1");

        mongoOps.insert(basket);

        MvcResult result = mockMvc.perform(get("/basket/{id}", basket.getId()))
                .andExpect(status().isOk())
                .andReturn();

        Basket resultBasket = objectMapper.readValue(result.getResponse().getContentAsString(), Basket.class);

        assertThat(resultBasket, is(basket));
    }

    @Test
    public void shouldReturn404ForNotExistingBasket() throws Exception {
        mockMvc.perform(get("/basket/{id}", "i-am-not-in-the-db"))
                .andExpect(status().isNotFound());
    }

}