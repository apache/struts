package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.XWorkTestCase;

import java.io.InputStream;
import java.net.URL;

/**
 * FileManager Tester.
 *
 * @author <Lukasz>
 * @since <pre>02/18/2009</pre>
 * @version 1.0
 */
public class FileManagerTest extends XWorkTestCase {

    public void testGetFileInJar() throws Exception {
        testLoadFile("xwork-jar.xml");
        testLoadFile("xwork - jar.xml");
        testLoadFile("xwork-zip.xml");
        testLoadFile("xwork - zip.xml");
        testLoadFile("xwork-jar2.xml");
        testLoadFile("xwork - jar2.xml");
        testLoadFile("xwork-zip2.xml");
        testLoadFile("xwork - zip2.xml");
    }

    private void testLoadFile(String fileName) {
        FileManager.setReloadingConfigs(true);
        URL url = ClassLoaderUtil.getResource(fileName, FileManagerTest.class);
        InputStream file = FileManager.loadFile(url, true);
        assertNotNull(file);
        assertFalse(!FileManager.fileNeedsReloading(fileName));
    }

}
