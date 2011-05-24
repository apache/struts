package com.opensymphony.xwork2.util;

import junit.framework.TestCase;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLStreamHandlerFactory;
import java.net.URLStreamHandler;
import java.net.URLConnection;
import java.io.IOException;

public class URLUtilTest extends TestCase {

    public void testSimpleFile() throws MalformedURLException {
        URL url = new URL("file:c:/somefile.txt");
        URL outputURL = URLUtil.normalizeToFileProtocol(url);

        assertNull(outputURL);
    }

    public void testJarFile() throws MalformedURLException {
        URL url = new URL("jar:file:/c:/somefile.jar!/");
        URL outputURL = URLUtil.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("jar:file:/c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("jar:file:c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.jar", outputURL.toExternalForm());
    }

    public void testJarFileWithJarWordInsidePath() throws MalformedURLException {
        URL url = new URL("jar:file:/c:/workspace/projar/somefile.jar!/");
        URL outputURL = URLUtil.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/workspace/projar/somefile.jar", outputURL.toExternalForm());

        url = new URL("jar:file:/c:/workspace/projar/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:/c:/workspace/projar/somefile.jar", outputURL.toExternalForm());

        url = new URL("jar:file:c:/workspace/projar/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:c:/workspace/projar/somefile.jar", outputURL.toExternalForm());
    }

    public void testZipFile() throws MalformedURLException {
        URL url = new URL("zip:/c:/somefile.zip!/");
        URL outputURL = URLUtil.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.zip", outputURL.toExternalForm());

        url = new URL("zip:/c:/somefile.zip!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.zip", outputURL.toExternalForm());

        url = new URL("zip:c:/somefile.zip!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.zip", outputURL.toExternalForm());
    }

    public void testWSJarFile() throws MalformedURLException {
        URL url = new URL("wsjar:file:/c:/somefile.jar!/");
        URL outputURL = URLUtil.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("wsjar:file:/c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("wsjar:file:c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.jar", outputURL.toExternalForm());
    }

    public void testVsFile() throws MalformedURLException {
        URL url = new URL("vfsfile:/c:/somefile.jar!/");
        URL outputURL = URLUtil.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfsfile:/c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfsfile:c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfszip:/c:/somefile.war/somelibrary.jar");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.war/somelibrary.jar", outputURL.toExternalForm());
    }

    public void testJBossFile() throws MalformedURLException {
        URL url = new URL("vfszip:/c:/somefile.jar!/");
        URL outputURL = URLUtil.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfszip:/c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfsmemory:c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfsmemory:/c:/somefile.war/somelibrary.jar");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.war/somelibrary.jar", outputURL.toExternalForm());
    }

    protected void setUp() throws Exception {
        super.setUp();

        try {
            URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
                public URLStreamHandler createURLStreamHandler(String protocol) {
                    return new URLStreamHandler() {
                        protected URLConnection openConnection(URL u) throws IOException {
                            return null;
                        }
                    };
                }
            });
        } catch (Throwable e) {
            //the factory cant be set multiple times..just ignore exception no biggie
        }
    }

    public void testVerifyUrl() {
        assertEquals(false, URLUtil.verifyUrl(null));
        assertEquals(false, URLUtil.verifyUrl(""));
        assertEquals(false, URLUtil.verifyUrl("   "));
        assertEquals(false, URLUtil.verifyUrl("no url"));

        assertEquals(true, URLUtil.verifyUrl("http://www.opensymphony.com"));
        assertEquals(true, URLUtil.verifyUrl("https://www.opensymphony.com"));
        assertEquals(true, URLUtil.verifyUrl("https://www.opensymphony.com:443/login"));
        assertEquals(true, URLUtil.verifyUrl("http://localhost:8080/myapp"));
    }

    public void testIsJarURL() throws Exception {
        assertTrue(URLUtil.isJarURL(new URL("jar:file:/c:/somelibrary.jar!/com/opensymphony")));
        assertTrue(URLUtil.isJarURL(new URL("zip:/c:/somelibrary.jar!/com/opensymphony")));
        assertTrue(URLUtil.isJarURL(new URL("wsjar:/c:/somelibrary.jar!/com/opensymphony")));
        assertTrue(URLUtil.isJarURL(new URL("vfsfile:/c:/somelibrary.jar!/com/opensymphony")));
        assertTrue(URLUtil.isJarURL(new URL("vfszip:/c:/somelibrary.jar/com/opensymphony")));
    }

    public void testIsJBoss5Url() throws Exception {
        assertTrue(URLUtil.isJBoss5Url(new URL("vfszip:/c:/somewar.war/somelibrary.jar")));
        assertFalse(URLUtil.isJBoss5Url(new URL("vfsfile:/c:/somewar.war/somelibrary.jar")));
        assertFalse(URLUtil.isJBoss5Url(new URL("jar:file:/c:/somelibrary.jar")));
        assertTrue(URLUtil.isJBoss5Url(new URL("vfsmemory:/c:/somewar.war/somelibrary.jar")));
    }
}
