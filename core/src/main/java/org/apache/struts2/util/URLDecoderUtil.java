package org.apache.struts2.util;

import org.apache.struts2.util.tomcat.buf.UDecoder;

/**
 * URLDecoderUtil serves as a facade for a correct URL decoding implementation.
 * As of Struts 2.3.25 it uses Tomcat URLDecoder functionality rather than the one found in java.io.
 */
public class URLDecoderUtil {

    /**
     * Decodes a <code>x-www-form-urlencoded</code> string.
     * @param sequence the String to decode
     * @param charset The name of a supported character encoding.
     * @return the newly decoded <code>String</code>
     * @exception IllegalArgumentException If the encoding is not valid
     */
    public static String decode(String sequence, String charset) {
        return UDecoder.URLDecode(sequence, charset);
    }

    /**
     * Decodes a <code>x-www-form-urlencoded</code> string.
     * @param sequence the String to decode
     * @param charset The name of a supported character encoding.
     * @param isQueryString whether input is a query string. If <code>true</code> other decoding rules apply.
     * @return the newly decoded <code>String</code>
     * @exception IllegalArgumentException If the encoding is not valid
     */
    public static String decode(String sequence, String charset, boolean isQueryString) {
        return UDecoder.URLDecode(sequence, charset, isQueryString);
    }

}
