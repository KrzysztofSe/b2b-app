package b2b.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class BasketNotFoundException extends CustomRestException {

    public BasketNotFoundException(final String message) {
        super(message);
    }
}
