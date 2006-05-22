package org.apache.struts.action2.spi;

import static org.easymock.EasyMock.*;
import junit.framework.TestCase;

import java.util.concurrent.Callable;

/**
 * @author crazybob@google.com (Bob Lee)
 */
public class ThreadLocalRequestTest extends TestCase {

    public void testSetAndCall() throws Exception {
        final RequestContext r1 = createMock(RequestContext.class);
        final RequestContext r2 = createMock(RequestContext.class);

        ensureNotSet();

        String result = ThreadLocalRequestContext.setAndCall(r1, new Callable<String>() {
            public String call() throws Exception {
                assertSame(r1, ThreadLocalRequestContext.get());
                String result = ThreadLocalRequestContext.setAndCall(r2, new Callable<String>() {
                    public String call() throws Exception {
                        assertSame(r2, ThreadLocalRequestContext.get());
                        return "foo";
                    }
                });
                assertSame(r1, ThreadLocalRequestContext.get());
                return result;
            }
        });

        ensureNotSet();

        assertEquals("foo", result);
    }

    private void ensureNotSet() {
        try {
           ThreadLocalRequestContext.get();
           fail();
        }
        catch (IllegalStateException e) { /* ignore */ }
    }
}
