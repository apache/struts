package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultTypeConverterTest {

    private DefaultTypeConverter converter;

    @Before
    public void setUp() {
        converter = new DefaultTypeConverter();
    }

    @After
    public void tearDown() {
        converter = null;
    }

    public void check(Object value, Class toType, Object expected, String message, Map<String, Object> context) throws Exception {
        Object result = converter.convertValue(context, value, toType);
        assertEquals(message, expected, result);
    }

    @Test
    public void shouldConvertToTrue() throws Exception {
        // String
        check("true", Boolean.TYPE, Boolean.TRUE, "Literally 'true' should be true!", null);
        check("True", Boolean.TYPE, Boolean.TRUE, "Literally 'True' should be true!", null);
        check("true", Boolean.class, Boolean.TRUE, "Literally 'true' should be true!", null);
        check("True", Boolean.class, Boolean.TRUE, "Literally 'True' should be true!", null);

        // String's array
        Object converted = converter.convertValue(null, new String[]{"true", "true"}, boolean[].class);
        assertTrue("Array of Strings should be converted to [true, true]", Arrays.equals(new boolean[]{true, true}, (boolean[]) converted));

        // Character
        check('1', Boolean.class, Boolean.TRUE, "Character '1' should be converted to true", null);

        // Integer
        check(2, Boolean.TYPE, Boolean.TRUE, "Integer 2 should be converted to true", null);

        // Long
        check(1L, Boolean.TYPE, Boolean.TRUE, "Long 1 should be converted to true", null);
        check(110L, Boolean.TYPE, Boolean.TRUE, "Long 110 should be converted to true", null);

        // Double
        check(1.0, Boolean.class, Boolean.TRUE, "Double 1.0 should be converted to true", null);
        check(-1.0, Boolean.class, Boolean.TRUE, "Double -1.0 should be converted to true", null);
        check(1.3, Boolean.class, Boolean.TRUE, "Double 1.3 should be converted to true", null);

        // Float
        check(1.0F, Boolean.class, Boolean.TRUE, "Float 1.0 should be converted to true", null);
        check(-1.0F, Boolean.class, Boolean.TRUE, "Float -1.0 should be converted to true", null);
        check(1.3F, Boolean.class, Boolean.TRUE, "Float 1.3 should be converted to true", null);

        // Byte
        check(Byte.valueOf("2"), Boolean.class, Boolean.TRUE, "Byte 2 should be converted to true", null);

        // BigInteger
        check(BigInteger.valueOf(20L), Boolean.class, Boolean.TRUE, "BigInteger 20 should be converted to true", null);

        // BigDecimal
        check(BigDecimal.valueOf(30L), Boolean.class, Boolean.TRUE, "BigDecimal 30 should be converted to true", null);

        // Short
        check((short) 30, Boolean.class, Boolean.TRUE, "Short 30 should be converted to true", null);
    }

    @Test
    public void shouldConvertToFalse() throws Exception {
        // String
        check(null, Boolean.TYPE, Boolean.FALSE, "Converting null should return false!", null);
        check(null, Boolean.class, null, "Converting null should return null!", null);
        check("FalsE", Boolean.TYPE, Boolean.FALSE, "Literally 'FalsE' should be false!", null);
        check("false", Boolean.class, Boolean.FALSE, "Literally 'false' should be false!", null);
        check("0", Boolean.class, Boolean.FALSE, "Literally '0' should be false!", null);
        check("1", Boolean.class, Boolean.FALSE, "Literally '1' should be false!", null);

        // Character
        check('0', Boolean.class, Boolean.FALSE, "Character '0' should be converted to false", null);
        check('A', Boolean.class, Boolean.FALSE, "Character 'A' should be converted to false", null);

        // Integer
        check(0, Boolean.TYPE, Boolean.FALSE, "Integer 0 should be converted to false", null);

        // Long
        check(0L, Boolean.TYPE, Boolean.FALSE, "Long 1 should be converted to false", null);

        // Double
        check(0.0, Boolean.class, Boolean.FALSE, "Double 0.0 should be converted to false", null);

        // Float
        check(0.0F, Boolean.class, Boolean.FALSE, "Float 0.0 should be converted to false", null);

        // Byte
        check(Byte.valueOf("0"), Boolean.class, Boolean.FALSE, "Byte 0 should be converted to false", null);

        // BigInteger
        check(BigInteger.valueOf(0L), Boolean.class, Boolean.FALSE, "BigInteger 0 should be converted to false", null);

        // BigDecimal
        check(BigDecimal.valueOf(0L), Boolean.class, Boolean.FALSE, "BigDecimal 0 should be converted to false", null);

        // Short
        check((short) 0, Boolean.class, Boolean.FALSE, "Short 0 should be converted to false", null);
    }

    @Test
    public void shouldConvertToDouble() throws Exception {
        // null
        check(null, Double.class, null, "Null should be converted to null", null);

        check(null, Double.TYPE, 0.0, "Null (primitive type) should be converted 0.0", null);

        // String PL-pl
        check("1,2", Double.TYPE, 1.2, "'1,2' (Polish) should be converted to 1.2", buildContext("pl", "PL"));
        // English
        check("1.2", Double.class, 1.2, "'1.2' (English) should be converted to 1.2", buildContext("en", "GB"));

        // English with text
        check("1.2foo", Double.TYPE, 1.2, "'1.2foo' (English) should be converted to 1.2", buildContext("en", "GB"));
        check("boo3.4", Double.class, null, "'boo1.2' (English) should be converted to 1.2", buildContext("en", "GB"));
        check("boo3.4", Double.TYPE, 0.0, "'boo1.2' (English) should be converted to 1.2", buildContext("en", "GB"));

        // Integer
        check(Integer.valueOf("200"), Double.class, 200.0, "200 should be converted to 200.0", null);

        // Boolean
        check(Boolean.FALSE, Double.TYPE, 0.0, "False should be converted to 0.0", null);
        check(Boolean.TRUE, Double.TYPE, 1.0, "True should be converted to 1.0", null);

        // Character
        check('W', Double.class, 87.0, "Character 'W' should be converted to 87.0", null);

        // Long
        check(100L, Double.class, 100.0, "Long 100 should be converted to 100.0", null);

        // Float
        check(40F, Double.class, 40.0, "Float 40 should be converted to 40.0", null);

        // Byte
        check(Byte.valueOf("1"), Double.class, 1.0, "Byte 1 should be converted to 11", null);

        // BigInteger
        check(BigInteger.valueOf(145L), Double.class, 145.0, "BigInteger 145 should be converted to 145.0", null);

        // BigDecimal
        check(BigDecimal.valueOf(23.44), Double.class, 23.44, "BigDecimal 23.44 should be converted to 23.44", null);

        // Short
        check((short) 30, Double.class, 30.0, "Short 30 should be converted to 30.0", null);
    }

    @Test
    public void shouldConvertToFloat() throws Exception {
        // null
        check(null, Float.class, null, "Null should be converted to null", null);

        check(null, Float.TYPE, 0.0F, "Null (primitive type) should be converted 0.0", null);

        // String PL-pl
        check("1,2", Float.TYPE, 1.2F, "'1,2' (Polish) should be converted to 1.2", buildContext("pl", "PL"));
        // English
        check("1.2", Float.class, 1.2F, "'1.2' (English) should be converted to 1.2", buildContext("en", "GB"));

        float[] floats = (float[]) converter.convertValue(null, new String[]{"123", "456"}, float[].class);
        assertTrue(Arrays.equals(floats, new float[]{123F, 456F}));

        // English with text
        check("1.2foo", Float.TYPE, 1.2F, "'1.2foo' (English) should be converted to 1.2", buildContext("en", "GB"));
        check("boo3.4", Float.class, null, "'boo1.2' (English) should be converted to 1.2", buildContext("en", "GB"));
        check("boo3.4", Float.TYPE, 0F, "'boo1.2' (English) should be converted to 1.2", buildContext("en", "GB"));

        // Integer
        check(Integer.valueOf("200"), Float.class, 200.0F, "200 should be converted to 200.0", null);

        // Boolean
        check(Boolean.FALSE, Float.TYPE, 0.0F, "False should be converted to 0.0", null);
        check(Boolean.TRUE, Float.TYPE, 1.0F, "True should be converted to 1.0", null);

        // Character
        check('W', Float.class, 87.0F, "Character 'W' should be converted to 87.0", null);

        // Long
        check(100L, Float.class, 100.0F, "Long 100 should be converted to 100.0", null);

        // Float
        check(40F, Float.class, 40.0F, "Float 40 should be converted to 40.0", null);

        // Byte
        check(Byte.valueOf("1"), Float.class, 1.0F, "Byte 1 should be converted to 11", null);

        // BigInteger
        check(BigInteger.valueOf(145L), Float.class, 145.0F, "BigInteger 145 should be converted to 145.0", null);

        // BigDecimal
        check(BigDecimal.valueOf(23.44), Float.class, 23.44F, "BigDecimal 23.44 should be converted to 23.44", null);

        // Short
        check((short) 30, Float.class, 30.0F, "Short 30 should be converted to 30.0", null);
    }

    @Test
    public void shouldConvertToByte() throws Exception {
        // null
        check(null, Byte.class, null, "Null should be converted to null", null);

        check(null, Byte.TYPE, (byte) 0, "Null (primitive type) should be converted 0", null);

        // String PL-pl
        check("1,2", Byte.TYPE, (byte) 1, "'1,2' (Polish) should be converted to 1", buildContext("pl", "PL"));
        // English
        check("1.2", Byte.class, (byte) 1, "'1.2' (English) should be converted to 1", buildContext("en", "GB"));

        // English with text
        check("1.2foo", Byte.TYPE, (byte) 1, "'1.2foo' (English) should be converted to 1", buildContext("en", "GB"));
        check("boo3.4", Byte.class, null, "'boo1.2' (English) should be converted to null", buildContext("en", "GB"));
        check("boo3.4", Byte.TYPE, (byte) 0, "'boo1.2' (English) should be converted to 0", buildContext("en", "GB"));

        // Integer
        check(Integer.valueOf("200"), Byte.class, (byte) -56, "200 should be converted to -56", null);

        // Boolean
        check(Boolean.FALSE, Byte.TYPE, (byte) 0, "False should be converted to 0", null);
        check(Boolean.TRUE, Byte.TYPE, (byte) 1, "True should be converted to 1", null);

        // Character
        check('W', Byte.class, (byte) 87, "Character 'W' should be converted to 87", null);

        // Long
        check(100L, Byte.class, (byte) 100, "Long 100 should be converted to 100", null);

        // Float
        check(40F, Byte.class, (byte) 40, "Float 40 should be converted to 40", null);

        // Byte
        check(Byte.valueOf("1"), Byte.class, (byte) 1, "Byte 1 should be converted to 1", null);

        // BigInteger
        check(BigInteger.valueOf(145L), Byte.class, (byte) 145, "BigInteger 145 should be converted to 145", null);

        // BigDecimal
        check(BigDecimal.valueOf(23.44), Byte.class, (byte) 23.44, "BigDecimal 23.44 should be converted to 23", null);

        // Short
        check((short) 30, Byte.class, (byte) 30, "Short 30 should be converted to 30", null);
    }

    @Test
    public void shouldConvertToInteger() throws Exception {
        // null
        check(null, Integer.class, null, "Null should be converted to null", null);

        check(null, Integer.TYPE, 0, "Null (primitive type) should be converted 0", null);

        // String PL-pl
        check("1,2", Integer.TYPE, 1, "'1,2' (Polish) should be converted to 1", buildContext("pl", "PL"));
        // English
        check("1.2", Integer.class, 1, "'1.2' (English) should be converted to 1", buildContext("en", "GB"));

        // English with text
        check("1.2foo", Integer.TYPE, 1, "'1.2foo' (English) should be converted to 1", buildContext("en", "GB"));
        check("boo3.4", Integer.class, null, "'boo1.2' (English) should be converted to null", buildContext("en", "GB"));
        check("boo3.4", Integer.TYPE, 0, "'boo1.2' (English) should be converted to 0", buildContext("en", "GB"));

        // Integer
        check(Integer.valueOf("200"), Integer.class, 200, "200 should be converted to 200", null);

        // Boolean
        check(Boolean.FALSE, Integer.TYPE, 0, "False should be converted to 0", null);
        check(Boolean.TRUE, Integer.TYPE, 1, "True should be converted to 1", null);

        // Character
        check('W', Integer.class, 87, "Character 'W' should be converted to 87", null);

        // Long
        check(100L, Integer.class, 100, "Long 100 should be converted to 100", null);

        // Float
        check(40F, Integer.class, 40, "Float 40 should be converted to 40", null);

        // Byte
        check(Byte.valueOf("1"), Integer.class, 1, "Byte 1 should be converted to 1", null);

        // BigInteger
        check(BigInteger.valueOf(145L), Integer.class, 145, "BigInteger 145 should be converted to 145", null);

        // BigDecimal
        check(BigDecimal.valueOf(23.44), Integer.class, 23, "BigDecimal 23.44 should be converted to 23", null);

        // Short
        check((short) 30, Integer.class, 30, "Short 30 should be converted to 30", null);
    }

    @Test
    public void shouldConvertToLong() throws Exception {
        // null
        check(null, Long.class, null, "Null should be converted to null", null);

        check(null, Long.TYPE, 0L, "Null (primitive type) should be converted 0", null);

        // String PL-pl
        check("1,2", Long.TYPE, 1L, "'1,2' (Polish) should be converted to 1", buildContext("pl", "PL"));
        // English
        check("1.2", Long.class, 1L, "'1.2' (English) should be converted to 1", buildContext("en", "GB"));

        // English with text
        check("1.2foo", Long.TYPE, 1L, "'1.2foo' (English) should be converted to 1", buildContext("en", "GB"));
        check("boo3.4", Long.class, null, "'boo1.2' (English) should be converted to null", buildContext("en", "GB"));
        check("boo3.4", Long.TYPE, 0L, "'boo1.2' (English) should be converted to 0", buildContext("en", "GB"));

        // Integer
        check(Integer.valueOf("200"), Long.class, 200L, "200 should be converted to 200", null);

        // Boolean
        check(Boolean.FALSE, Long.TYPE, 0L, "False should be converted to 0", null);
        check(Boolean.TRUE, Long.TYPE, 1L, "True should be converted to 1", null);

        // Character
        check('W', Long.class, 87L, "Character 'W' should be converted to 87", null);

        // Long
        check(100L, Long.class, 100L, "Long 100 should be converted to 100", null);

        // Float
        check(40F, Long.class, 40L, "Float 40 should be converted to 40", null);

        // Byte
        check(Byte.valueOf("1"), Long.class, 1L, "Byte 1 should be converted to 1", null);

        // BigInteger
        check(BigInteger.valueOf(145L), Long.class, 145L, "BigInteger 145 should be converted to 145", null);

        // BigDecimal
        check(BigDecimal.valueOf(23.44), Long.class, 23L, "BigDecimal 23.44 should be converted to 23", null);

        // Short
        check((short) 30, Long.class, 30L, "Short 30 should be converted to 30", null);
    }

    @Test
    public void shouldConvertToShort() throws Exception {
        // null
        check(null, Short.class, null, "Null should be converted to null", null);

        check(null, Short.TYPE, (short) 0, "Null (primitive type) should be converted 0", null);

        // String PL-pl
        check("1,2", Short.TYPE, (short) 1, "'1,2' (Polish) should be converted to 1", buildContext("pl", "PL"));
        // English
        check("1.2", Short.class, (short) 1, "'1.2' (English) should be converted to 1", buildContext("en", "GB"));

        // English with text
        check("1.2foo", Short.TYPE, (short) 1, "'1.2foo' (English) should be converted to 1", buildContext("en", "GB"));
        check("boo3.4", Short.class, null, "'boo1.2' (English) should be converted to null", buildContext("en", "GB"));
        check("boo3.4", Short.TYPE, (short) 0, "'boo1.2' (English) should be converted to 0", buildContext("en", "GB"));

        // Integer
        check(Integer.valueOf("200"), Short.class, (short) 200, "200 should be converted to 200", null);

        // Boolean
        check(Boolean.FALSE, Short.TYPE, (short) 0, "False should be converted to 0", null);
        check(Boolean.TRUE, Short.TYPE, (short) 1, "True should be converted to 1", null);

        // Character
        check('W', Short.class, (short) 87, "Character 'W' should be converted to 87", null);

        // Long
        check(100L, Short.class, (short) 100, "Long 100 should be converted to 100", null);

        // Float
        check(40F, Short.class, (short) 40, "Float 40 should be converted to 40", null);

        // Byte
        check(Byte.valueOf("1"), Short.class, (short) 1, "Byte 1 should be converted to 1", null);

        // BigInteger
        check(BigInteger.valueOf(145L), Short.class, (short) 145, "BigInteger 145 should be converted to 145", null);

        // BigDecimal
        check(BigDecimal.valueOf(23.44), Short.class, (short) 23, "BigDecimal 23.44 should be converted to 23", null);

        // Short
        check((short) 30, Short.class, (short) 30, "Short 30 should be converted to 30", null);
    }

    @Test
    public void shouldConvertToCharacter() throws Exception {
        // null
        check(null, Character.class, null, "Null should be converted to null", null);

        check(null, Character.TYPE, (char) 0, "Null (primitive type) should be converted 0", null);

        // String PL-pl
        check("1,2", Character.TYPE, "1".charAt(0), "'1,2' (Polish) should be converted to 1", buildContext("pl", "PL"));
        // English
        check("1.2", Character.class, "1".charAt(0), "'1.2' (English) should be converted to 1", buildContext("en", "GB"));

        // English with text
        check("1.2foo", Character.TYPE, "1".charAt(0), "'1.2foo' (English) should be converted to 1", buildContext("en", "GB"));
        check("boo3.4", Character.class, "b".charAt(0), "'boo1.2' (English) should be converted to null", buildContext("en", "GB"));
        check("boo3.4", Character.TYPE, "b".charAt(0), "'boo1.2' (English) should be converted to 0", buildContext("en", "GB"));

        // Integer
        check(Integer.valueOf("200"), Character.class, (char) 200, "200 should be converted to 200", null);

        // Boolean
        check(Boolean.FALSE, Character.TYPE, (char) 0, "False should be converted to 0", null);
        check(Boolean.TRUE, Character.TYPE, (char) 1, "True should be converted to 1", null);

        // Character
        check('W', Character.class, 'W', "Character 'W' should be converted to W", null);
        check('W', Character.TYPE, 'W', "Character 'W' should be converted to W", null);

        // Long
        check(100L, Character.class, (char) 100, "Long 100 should be converted to 100", null);

        // Float
        check(40F, Character.class, (char) 40, "Float 40 should be converted to 40", null);

        // Byte
        check(Byte.valueOf("1"), Character.class, (char) 1, "Byte 1 should be converted to 1", null);

        // BigInteger
        check(BigInteger.valueOf(145L), Character.class, (char) 145, "BigInteger 145 should be converted to 145", null);

        // BigDecimal
        check(BigDecimal.valueOf(23.44), Character.class, (char) 23, "BigDecimal 23.44 should be converted to 23", null);

        // Short
        check((short) 30, Character.class, (char) 30, "Short 30 should be converted to 30", null);
    }

    private Map<String, Object> buildContext(final String language, final String country) {
        return new HashMap<String, Object>() {
            {
                put(ActionContext.LOCALE, new Locale(language, country));
            }
        };
    }

}
