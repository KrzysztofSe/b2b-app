package b2b.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED)
public class InvalidVatNumberException extends CustomRestException {

    public InvalidVatNumberException(String message) {
        super(message);
    }

}
