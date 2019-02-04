package b2b.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRestException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(CustomRestException.class);

    public CustomRestException(final String message) {
        super(message);
        LOG.warn(message);
    }

    public CustomRestException(final String message,
                               final Throwable cause) {
        super(message, cause);
        LOG.warn(message);
    }
}