package b2b.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRestException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(CustomRestException.class);

    public CustomRestException(String message) {
        super(message);
        LOG.warn(message);
    }
}
