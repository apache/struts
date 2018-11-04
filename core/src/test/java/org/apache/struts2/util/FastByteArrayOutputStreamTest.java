/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import junit.framework.TestCase;


/**
 * Basic tests of FastByteArrayOutputStream
 */
public class FastByteArrayOutputStreamTest extends TestCase {

    final String utf16_String = new String("Standard string with accented characters \u00E7\u00E8\u00E9 in the middle");
    final String utf8_String = new String(utf16_String.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    final String iso8859_1_String = new String(utf16_String.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);

    /**
     * Test usage UTF8 to UTF8
     *
     * No Warn log output produced
     */
    public void testUTF8WriteToUTF8() {
        FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        OutputStreamWriter osw = null;

        try {
          assertTrue("UTF-8 string length not same as UTF-16", utf16_String.length() == utf8_String.length());
          fbaos.write(utf8_String.getBytes("UTF-8"));
          osw = new OutputStreamWriter(baos, "UTF-8");
          fbaos.writeTo(osw, "UTF-8");
          osw.flush();
          // Expect matching encodings will result in consistent output
          assertTrue("Result is empty", baos.toString("UTF-8").length() > 0);
          assertEquals("UTF8 string doesn't match buffer write result", utf8_String, baos.toString("UTF-8"));
        }
        catch(UnsupportedEncodingException ue) {
            fail("Unexpected UnsupportedEncodingException during test: " + ue);
        }
        catch(IOException ioe) {
            fail("Unexpected IOException during test: " + ioe);
        }
        finally {
            if (osw != null) {
                try {
                    osw.close();
                }
                catch (Exception ex) {}
            }
            if (baos != null) {
                try {
                    baos.close();
                }
                catch (Exception ex) {}
            }
            if (fbaos != null) {
                try {
                    fbaos.close();
                }
                catch (Exception ex) {}
            }
        }
    }

    /**
     * Test Usage ISO8859-1 to ISO8859-1
     *
     * No Warn log output produced
     */
    public void testISO8859_1WriteToISO8859_1() {
        FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        OutputStreamWriter osw = null;

        try {
            assertTrue("ISO-8859-1 string length not same as UTF-16", utf16_String.length() == iso8859_1_String.length());
            fbaos.write(iso8859_1_String.getBytes("ISO-8859-1"));
            osw = new OutputStreamWriter(baos, "ISO-8859-1");
            fbaos.writeTo(osw, "ISO-8859-1");
            osw.flush();
            // Expect matching encodings will result in consistent output
            assertTrue("Result is empty", baos.toString("ISO-8859-1").length() > 0);
            assertEquals("ISO-8859-1 string doesn't match buffer write result", iso8859_1_String, baos.toString("ISO-8859-1"));
        }
        catch(UnsupportedEncodingException ue) {
            fail("Unexpected UnsupportedEncodingException during test: " + ue);
        }
        catch(IOException ioe) {
            fail("Unexpected IOException during test: " + ioe);
        }
        finally {
            if (osw != null) {
                try {
                    osw.close();
                }
                catch (Exception ex) {}
            }
            if (baos != null) {
                try {
                    baos.close();
                }
                catch (Exception ex) {}
            }
            if (fbaos != null) {
                try {
                    fbaos.close();
                }
                catch (Exception ex) {}
            }
        }
    }

    /**
     * Test Usage UTF8 to ISO8859-1
     *
     * No Warn log output produced
     */
    public void testUTF8WriteToISO8859_1() {
        FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        OutputStreamWriter osw = null;

        try {
            assertTrue("UTF-8 string length not same as UTF-16", utf16_String.length() == utf8_String.length());
            fbaos.write(utf8_String.getBytes("UTF-8"));
            osw = new OutputStreamWriter(baos, "ISO-8859-1");
            fbaos.writeTo(osw, "ISO-8859-1");
            osw.flush();
            // Although encodings mismatch, decoding the test UTF-8 string into ISO-8859-1 does not result in truncation.
            // Behaviour may be related to the CodingErrorAction.REPLACE setting for the CharsetDecoder.
            assertTrue("Result is empty", baos.toString("UTF-8").length() > 0);
            assertTrue("UTF-8 string does not match buffer write result (truncation)", utf8_String.equals(baos.toString("UTF-8")));
            assertFalse("Buffer write result truncated", baos.toString("UTF-8").length() < utf8_String.length());
        }
        catch(UnsupportedEncodingException ue) {
            fail("Unexpected UnsupportedEncodingException during test: " + ue);
        }
        catch(IOException ioe) {
            fail("Unexpected IOException during test: " + ioe);
        }
        finally {
            if (osw != null) {
                try {
                    osw.close();
                }
                catch (Exception ex) {}
            }
            if (baos != null) {
                try {
                    baos.close();
                }
                catch (Exception ex) {}
            }
            if (fbaos != null) {
                try {
                    fbaos.close();
                }
                catch (Exception ex) {}
            }
        }
    }

    /**
     * Test Usage ISO8859-1 to UTF8
     *
     * A single Warn log output produced
     */
    public void testISO8859_1WriteToUTF8() {
        FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        OutputStreamWriter osw = null;

        try {
            assertTrue("ISO-8859-1 string length not same as UTF-16", utf16_String.length() == iso8859_1_String.length());
            fbaos.write(iso8859_1_String.getBytes("ISO-8859-1"));
            osw = new OutputStreamWriter(baos, "UTF-8");
            fbaos.writeTo(osw, "UTF-8");
            osw.flush();
            // Expect mismatched encodings will result in inconsistent output.
            //   This will produce a Warn log output from FastByteArrayOutputStream
            // The UTF-8 decoder interprets the accented characters (C0-FF) as malformed
            //   input and decoding halts (which results in truncation of the input string at
            //   the point of the accented characters).
            assertTrue("Result is empty", baos.toString("UTF-8").length() > 0);
            assertFalse("UTF-8 string matches buffer write result (no truncation)", utf8_String.equals(baos.toString("UTF-8")));
            assertTrue("Buffer write result not truncated", baos.toString("UTF-8").length() < utf8_String.length());
        }
        catch(UnsupportedEncodingException ue) {
            fail("Unexpected UnsupportedEncodingException during test: " + ue);
        }
        catch(IOException ioe) {
            fail("Unexpected IOException during test: " + ioe);
        }
        finally {
            if (osw != null) {
                try {
                    osw.close();
                }
                catch (Exception ex) {}
            }
            if (baos != null) {
                try {
                    baos.close();
                }
                catch (Exception ex) {}
            }
            if (fbaos != null) {
                try {
                    fbaos.close();
                }
                catch (Exception ex) {}
            }
        }
    }

}