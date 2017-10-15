package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class JarEntryRevisionTest extends XWorkTestCase {

    private FileManager fileManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fileManager = container.getInstance(FileManagerFactory.class).getFileManager();
    }

    private void createJarFile(long time) throws Exception {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        FileOutputStream fos = new FileOutputStream("target/JarEntryRevisionTest_testNeedsReloading.jar", false);
        JarOutputStream target = new JarOutputStream(fos, manifest);
        target.putNextEntry(new ZipEntry("com/opensymphony/xwork2/util/fs/"));
        ZipEntry entry = new ZipEntry("com/opensymphony/xwork2/util/fs/JarEntryRevisionTest.class");
        entry.setTime(time);
        target.putNextEntry(entry);
        InputStream source = getClass().getResourceAsStream("/com/opensymphony/xwork2/util/fs/JarEntryRevisionTest.class");
        IOUtils.copy(source, target);
        source.close();
        target.closeEntry();
        target.close();
        fos.close();
    }

    public void testNeedsReloading() throws Exception {
        long now = System.currentTimeMillis();

        createJarFile(now);
        URL url = new URL("jar:file:target/JarEntryRevisionTest_testNeedsReloading.jar!/com/opensymphony/xwork2/util/fs/JarEntryRevisionTest.class");
        Revision entry = JarEntryRevision.build(url, fileManager);
        assertFalse(entry.needsReloading());

        createJarFile(now + 60000);
        assertTrue(entry.needsReloading());
    }
}
