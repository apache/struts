package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarInputStream;

/**
 * Keep these test on a separate class, they can't be in UrlUtilTest because the
 * registered URLStreamHandlerFactory would make them fail
 */
public class UrlUtilTest2 extends TestCase {

    public void testOpenWithJarProtocol() throws IOException {
        FileManager fileManager = new DefaultFileManager();

        URL url = ClassLoaderUtil.getResource("xwork-jar.jar", URLUtil.class);
        URL jarUrl = new URL("jar", "", url.toExternalForm() + "!/");
        URL outputURL = fileManager.normalizeToFileProtocol(jarUrl);

        assertNotNull(outputURL);
        assertUrlCanBeOpened(outputURL);
    }

    private void assertUrlCanBeOpened(URL url) throws IOException {
        InputStream is = url.openStream();
        JarInputStream jarStream = null;
        try {
            jarStream = new JarInputStream(is);
            assertNotNull(jarStream);
        } finally {
            if (jarStream != null)
                jarStream.close();

        }
    }
}
