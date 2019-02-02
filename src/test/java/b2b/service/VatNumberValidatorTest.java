package b2b.service;

import b2b.exception.InvalidVatNumberException;
import b2b.exception.VatNumberValidatorException;
import b2b.model.VatNumberResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VatNumberValidatorTest {

    @Mock
    private RestTemplate restTemplate;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private VatNumberValidator vatNumberValidator;

    @Before
    public void setUp() {
        vatNumberValidator = new VatNumberValidator(restTemplate, "token", "http://validator.url");
    }

    @Test
    public void shouldThrowExceptionWhenProblemConnectingWithService() {
        when(restTemplate.getForEntity(any(URI.class), eq(VatNumberResponse.class)))
                .thenThrow(new RestClientException("rest client exception"));

        expectedException.expect(VatNumberValidatorException.class);
        expectedException.expectMessage("A problem occurred when validating VAT number");
        vatNumberValidator.validate("1234");
    }

    @Test
    public void shouldThrowExceptionWhenResponseCodeNotOk() {
        ResponseEntity<VatNumberResponse> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.getForEntity(any(URI.class), eq(VatNumberResponse.class))).thenReturn(response);

        expectedException.expect(VatNumberValidatorException.class);
        expectedException.expectMessage("Could not retrieve VAT number validation response");
        vatNumberValidator.validate("1234");
    }

    @Test
    public void shouldThrowExceptionWhenVatNumberInvalid() {
        String vatNumber = "12345";
        VatNumberResponse body = new VatNumberResponse();
        body.setValid(false);
        ResponseEntity<VatNumberResponse> response = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.getForEntity(any(URI.class), eq(VatNumberResponse.class))).thenReturn(response);

        expectedException.expect(InvalidVatNumberException.class);
        expectedException.expectMessage("The provided VAT number is invalid: " + vatNumber);
        vatNumberValidator.validate(vatNumber);
    }

    @Test
    public void shouldValidateVatNumber() {
        VatNumberResponse body = new VatNumberResponse();
        body.setValid(true);
        ResponseEntity<VatNumberResponse> response = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.getForEntity(any(URI.class), eq(VatNumberResponse.class))).thenReturn(response);

        vatNumberValidator.validate("1234");
    }

}