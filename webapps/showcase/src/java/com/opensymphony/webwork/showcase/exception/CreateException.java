package com.opensymphony.webwork.showcase.exception;

import org.apache.log4j.Logger;

/**
 * CreateException.
 *
 * @author <a href="mailto:gielen@it-neering.net">Rene Gielen</a>
 */

public class CreateException extends StorageException {

    private static final Logger log = Logger.getLogger(CreateException.class);

    public CreateException(String message) {
        super(message);
    }

    public CreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateException(Throwable cause) {
        super(cause);
    }
}
