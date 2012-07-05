package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
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
public class DefaultFileManagerTest extends XWorkTestCase {

    private FileManager fileManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fileManager = container.getInstance(FileManagerFactory.class).getFileManager();
    }

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
        fileManager.setReloadingConfigs("true");
        URL url = ClassLoaderUtil.getResource(fileName, DefaultFileManagerTest.class);
        InputStream file = fileManager.loadFile(url);
        assertNotNull(file);
        assertFalse(!fileManager.fileNeedsReloading(fileName));
    }

}
