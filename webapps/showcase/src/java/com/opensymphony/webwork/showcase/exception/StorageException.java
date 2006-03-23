package com.opensymphony.webwork.showcase.exception;

import org.apache.log4j.Logger;

/**
 * StorageException.
 *
 * @author <a href="mailto:gielen@it-neering.net">Rene Gielen</a>
 */

public class StorageException extends Exception {

    private static final Logger log = Logger.getLogger(StorageException.class);

    public StorageException(String message) {
        super(message);
    }

    public StorageException(Throwable cause) {
        super(cause);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
