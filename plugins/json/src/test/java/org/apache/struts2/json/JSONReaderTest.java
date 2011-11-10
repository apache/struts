package org.apache.struts2.json;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: mcucchiara
 * Date: 10/11/11
 * Time: 17.26
 */
public class JSONReaderTest {
    private JSONReader reader = new JSONReader();

    @Test
    public void testExponentialNumber() throws Exception {
        Object ret = reader.read("5e-5");
        assertNotNull(ret);
        assertEquals(Double.class, ret.getClass());
        assertEquals(5.0E-5, ret);
    }

    @Test
    public void testExponentialNumber2() throws Exception {
        Object ret = reader.read("123.4e10");
        assertNotNull(ret);
        assertEquals(Double.class, ret.getClass());
        assertEquals(123.4e10, ret);
    }

    @Test
    public void testDecimalNumber() throws Exception {
        Object ret = reader.read("3.2");
        assertNotNull(ret);
        assertEquals(Double.class, ret.getClass());
        assertEquals(3.2, ret);
    }

    @Test
    public void testNaturalNumber() throws Exception {
        Object ret = reader.read("123");
        assertNotNull(ret);
        assertEquals(Long.class, ret.getClass());
        assertEquals(123L, ret);
    }
}
