package com.opensymphony.webwork.showcase.exception;

import org.apache.log4j.Logger;

/**
 * UpdateException.
 *
 * @author <a href="mailto:gielen@it-neering.net">Rene Gielen</a>
 */

public class UpdateException extends StorageException {

    private static final Logger log = Logger.getLogger(UpdateException.class);

    public UpdateException(String message) {
        super(message);
    }

    public UpdateException(Throwable cause) {
        super(cause);
    }

    public UpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
