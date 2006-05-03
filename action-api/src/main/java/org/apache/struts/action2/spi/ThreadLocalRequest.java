package org.apache.struts.action2.spi;

import java.util.concurrent.Callable;

/**
 * Provides a reference to the current {@link Request} for this thread.
 *
 * <p>Actions which spawn additional threads are responsible for setting this value if access to Struts from the
 * additional thread is needed.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public final class ThreadLocalRequest {

    static ThreadLocal<Request> threadLocalRequest = new ThreadLocal<Request>();

    private ThreadLocalRequest() {}

    /**
     * Sets {@link Request} for the current thread and invokes the provided {@link Callable}. Restores previous {@code
     * Request} (if any) when finished.
     *
     * @param request for current thread
     * @param callable
     * @return result of {@code callable}
     * @throws Exception from {@code callable}
     */
    public static <T> T setAndCall(Request request, Callable<T> callable) throws Exception {
        Request old = threadLocalRequest.get();
        try {
            threadLocalRequest.set(request);
            return callable.call();
        } finally {
            if (old == null)
                threadLocalRequest.remove();
            else
                threadLocalRequest.set(old);
        }
    }

    /**
     * Gets the {@link Request} for the current thread.
     *
     * @return request for current thread
     * @throws IllegalStateException if no request has been set
     */
    public static Request get() {
        Request request = threadLocalRequest.get();
        if (request == null) {
            throw new IllegalStateException(ThreadLocalRequest.class.getName() + " has not been set.");
        }
        return request;
    }
}
