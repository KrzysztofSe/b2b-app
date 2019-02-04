package b2b.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidBasketException extends CustomRestException {

    public InvalidBasketException(final String message) {
        super(message);
    }
}
