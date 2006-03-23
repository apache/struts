package com.opensymphony.webwork.showcase.exception;

import org.apache.log4j.Logger;

/**
 * DeleteException.
 *
 * @author <a href="mailto:gielen@it-neering.net">Rene Gielen</a>
 */

public class DeleteException extends StorageException {

    private static final Logger log = Logger.getLogger(DeleteException.class);

    public DeleteException(String message) {
        super(message);
    }

    public DeleteException(Throwable cause) {
        super(cause);
    }

    public DeleteException(String message, Throwable cause) {
        super(message, cause);
    }

}
