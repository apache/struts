package org.apache.struts.action2.showcase.exception;

/**
 * StorageException.
 *
 * @author <a href="mailto:gielen@it-neering.net">Rene Gielen</a>
 */

public class StorageException extends Exception {

	private static final long serialVersionUID = -2528721270540362905L;
	
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
