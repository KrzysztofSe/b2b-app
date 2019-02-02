package b2b.service;

import b2b.exception.InvalidVatNumberException;
import b2b.exception.VatNumberValidatorException;
import b2b.model.VatNumberResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class VatNumberValidator {

    private static final Logger LOG = LoggerFactory.getLogger(VatNumberValidator.class);

    private final static String ACCESS_KEY = "access_key";
    private final static String VAT_NUMBER_KEY = "vat_number";
    private final static String FORMAT_KEY = "format";
    private final static String FORMAT_VALUE = "1";

    private final String token;
    private final String validatorUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public VatNumberValidator(RestTemplate restTemplate, @Value("${vat.validator.token}") String token,
                              @Value("${vat.validator.url}") String validatorUrl) {
        this.restTemplate = restTemplate;
        this.token = token;
        this.validatorUrl = validatorUrl;
    }

    public void validate(String vatNumber) {
        LOG.info("Validating vat number {}", vatNumber);
        URI uri = UriComponentsBuilder.fromHttpUrl(validatorUrl)
                .queryParam(ACCESS_KEY, token)
                .queryParam(VAT_NUMBER_KEY, vatNumber)
                .queryParam(FORMAT_KEY, FORMAT_VALUE)
                .build().toUri();

        try {
            validateResponse(restTemplate.getForEntity(uri, VatNumberResponse.class), vatNumber);
        } catch (RestClientException e) {
            throw new VatNumberValidatorException("A problem occurred when validating VAT number", e);
        }
    }

    private void validateResponse(ResponseEntity<VatNumberResponse> response, String vatNumber) {
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new VatNumberValidatorException("Could not retrieve VAT number validation response");
        }
        if (!response.getBody().isValid()) {
            throw new InvalidVatNumberException("The provided VAT number is invalid: " + vatNumber);
        }
    }
}
