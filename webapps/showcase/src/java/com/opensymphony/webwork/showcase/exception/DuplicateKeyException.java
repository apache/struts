package com.opensymphony.webwork.showcase.exception;

import org.apache.log4j.Logger;

/**
 * DuplicateKeyException.
 *
 * @author <a href="mailto:gielen@it-neering.net">Rene Gielen</a>
 */

public class DuplicateKeyException extends CreateException {

    private static final Logger log = Logger.getLogger(DuplicateKeyException.class);

    public DuplicateKeyException(String message) {
        super(message);
    }

    public DuplicateKeyException(Throwable cause) {
        super(cause);
    }

    public DuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

}
