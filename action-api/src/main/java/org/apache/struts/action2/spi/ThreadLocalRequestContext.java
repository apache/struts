package org.apache.struts.action2.spi;

import java.util.concurrent.Callable;

/**
 * Provides a reference to the current {@link RequestContext} for this thread.
 *
 * <p>Actions which spawn additional threads are responsible for setting this value if access to Struts from the
 * additional thread is needed.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public final class ThreadLocalRequestContext {

    static ThreadLocal<RequestContext> threadLocalRequestContext = new ThreadLocal<RequestContext>();

    private ThreadLocalRequestContext() {}

    /**
     * Sets {@link RequestContext} for the current thread and invokes the provided {@link Callable}. Restores previous
     * {@code RequestContext} (if any) when finished.
     *
     * @param requestContext for current thread
     * @param callable
     * @return result of {@code callable}
     * @throws Exception from {@code callable}
     */
    public static <T> T setAndCall(RequestContext requestContext, Callable<T> callable) throws Exception {
        RequestContext old = threadLocalRequestContext.get();
        try {
            threadLocalRequestContext.set(requestContext);
            return callable.call();
        } finally {
            if (old == null)
                threadLocalRequestContext.remove();
            else
                threadLocalRequestContext.set(old);
        }
    }

    /**
     * Gets the {@link RequestContext} for the current thread.
     *
     * @return request for current thread
     * @throws IllegalStateException if no request has been set
     */
    public static RequestContext get() {
        RequestContext requestContext = threadLocalRequestContext.get();
        if (requestContext == null) {
            throw new IllegalStateException(ThreadLocalRequestContext.class.getName() + " has not been set.");
        }
        return requestContext;
    }
}
