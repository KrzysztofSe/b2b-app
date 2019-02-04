package b2b.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class VatNumberValidatorException extends CustomRestException {

    public VatNumberValidatorException(final String message) {
        super(message);
    }

    public VatNumberValidatorException(final String message,
                                       final Throwable cause) {
        super(message, cause);
    }
}
