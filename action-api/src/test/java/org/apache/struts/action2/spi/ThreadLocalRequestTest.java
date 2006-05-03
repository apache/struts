package org.apache.struts.action2.spi;

import static org.easymock.EasyMock.*;
import junit.framework.TestCase;

import java.util.concurrent.Callable;

/**
 * @author crazybob@google.com (Bob Lee)
 */
public class ThreadLocalRequestTest extends TestCase {

    public void testSetAndCall() throws Exception {
        final Request r1 = createMock(Request.class);
        final Request r2 = createMock(Request.class);

        ensureNotSet();

        String result = ThreadLocalRequest.setAndCall(r1, new Callable<String>() {
            public String call() throws Exception {
                assertSame(r1, ThreadLocalRequest.get());
                String result = ThreadLocalRequest.setAndCall(r2, new Callable<String>() {
                    public String call() throws Exception {
                        assertSame(r2, ThreadLocalRequest.get());
                        return "foo";
                    }
                });
                assertSame(r1, ThreadLocalRequest.get());
                return result;
            }
        });

        ensureNotSet();

        assertEquals("foo", result);
    }

    private void ensureNotSet() {
        try {
           ThreadLocalRequest.get();
           fail();
        }
        catch (IllegalStateException e) { /* ignore */ }
    }
}
