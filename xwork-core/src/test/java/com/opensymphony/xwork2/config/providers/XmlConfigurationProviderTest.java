/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.MockConfiguration;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.FileManager;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class XmlConfigurationProviderTest extends ConfigurationTestBase {

    public void testLoadOrder() throws Exception {
        configuration = new MockConfiguration();
        ((MockConfiguration) configuration).selfRegister();
        container = configuration.getContainer();

        XmlConfigurationProvider prov = new XmlConfigurationProvider("xwork-test-load-order.xml", true) {
            @Override
            protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
                List<URL> urls = new ArrayList<URL>();
                urls.add(ClassLoaderUtil.getResource("com/opensymphony/xwork2/config/providers/loadorder1/xwork-test-load-order.xml", XmlConfigurationProvider.class));
                urls.add(ClassLoaderUtil.getResource("com/opensymphony/xwork2/config/providers/loadorder2/xwork-test-load-order.xml", XmlConfigurationProvider.class));
                urls.add(ClassLoaderUtil.getResource("com/opensymphony/xwork2/config/providers/loadorder3/xwork-test-load-order.xml", XmlConfigurationProvider.class));
                return urls.iterator();
            }
        };
        prov.setObjectFactory(container.getInstance(ObjectFactory.class));
        prov.init(configuration);
        List<Document> docs = prov.getDocuments();
        assertEquals(3, docs.size());

        assertEquals(1, XmlHelper.getLoadOrder(docs.get(0)).intValue());
        assertEquals(2, XmlHelper.getLoadOrder(docs.get(1)).intValue());
        assertEquals(3, XmlHelper.getLoadOrder(docs.get(2)).intValue());
    }

    public static final long FILE_TS_WAIT_IN_MS = 3600000;

    private static void changeFileTime(File f) throws Exception {
        final long orig = f.lastModified();
        final long maxwait = orig + FILE_TS_WAIT_IN_MS;
        long curr;
        while (!f.setLastModified(curr = System.currentTimeMillis()) || orig == f.lastModified()) {
            Thread.sleep(500);
            assertTrue("Waited more than " + FILE_TS_WAIT_IN_MS + " ms to update timestamp on file: " + f, maxwait > curr);
        }
    }

    public void testNeedsReload() throws Exception {
        FileManager.setReloadingConfigs(true);
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-actions.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        assertTrue(!provider.needsReload());

        File file = new File(getClass().getResource("/" + filename).toURI());
        assertTrue("not exists: " + file.toString(), file.exists());
        changeFileTime(file);

        assertTrue(provider.needsReload());
    }

    public void testInheritence() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-include-parent.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        provider.init(configuration);
        provider.loadPackages();

        // test expectations
        assertEquals(6, configuration.getPackageConfigs().size());


        PackageConfig defaultPackage = configuration.getPackageConfig("default");
        assertNotNull(defaultPackage);
        assertEquals("default", defaultPackage.getName());


        PackageConfig namespace1 = configuration.getPackageConfig("namespace1");
        assertNotNull(namespace1);
        assertEquals("namespace1", namespace1.getName());
        assertEquals(defaultPackage, namespace1.getParents().get(0));

        PackageConfig namespace2 = configuration.getPackageConfig("namespace2");
        assertNotNull(namespace2);
        assertEquals("namespace2", namespace2.getName());
        assertEquals(1, namespace2.getParents().size());
        assertEquals(namespace1, namespace2.getParents().get(0));


        PackageConfig namespace4 = configuration.getPackageConfig("namespace4");
        assertNotNull(namespace4);
        assertEquals("namespace4", namespace4.getName());
        assertEquals(1, namespace4.getParents().size());
        assertEquals(namespace1, namespace4.getParents().get(0));


        PackageConfig namespace5 = configuration.getPackageConfig("namespace5");
        assertNotNull(namespace5);
        assertEquals("namespace5", namespace5.getName());
        assertEquals(1, namespace5.getParents().size());
        assertEquals(namespace4, namespace5.getParents().get(0));

        configurationManager.addContainerProvider(provider);
        configurationManager.reload();

        RuntimeConfiguration runtimeConfiguration = configurationManager.getConfiguration().getRuntimeConfiguration();
        assertNotNull(runtimeConfiguration.getActionConfig("/namespace1", "action1"));
        assertNotNull(runtimeConfiguration.getActionConfig("/namespace2", "action2"));
        assertNotNull(runtimeConfiguration.getActionConfig("/namespace4", "action4"));
        assertNotNull(runtimeConfiguration.getActionConfig("/namespace5", "action5"));
    }

    public void testGuessResultType() {
        XmlConfigurationProvider prov = new XmlConfigurationProvider();

        assertEquals(null, prov.guessResultType(null));
        assertEquals("foo", prov.guessResultType("foo"));
        assertEquals("foo", prov.guessResultType("foo-"));
        assertEquals("fooBar", prov.guessResultType("foo-bar"));
        assertEquals("fooBarBaz", prov.guessResultType("foo-bar-baz"));
    }

    public void testEmptySpaces() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork- test.xml";
        FileManager.setReloadingConfigs(true);

        ConfigurationProvider provider = buildConfigurationProvider(filename);
        assertTrue(!provider.needsReload());

        URI uri = ClassLoaderUtil.getResource(filename, ConfigurationProvider.class).toURI();

        File file = new File(uri);

        assertTrue(file.exists());
        changeFileTime(file);

        assertTrue(provider.needsReload());
    }

    public void testConfigsInJarFiles() throws Exception {
        FileManager.setReloadingConfigs(true);
        testProvider("xwork-jar.xml");
        testProvider("xwork-zip.xml");
        testProvider("xwork - jar.xml");
        testProvider("xwork - zip.xml");

        testProvider("xwork-jar2.xml");
        testProvider("xwork-zip2.xml");
        testProvider("xwork - jar2.xml");
        testProvider("xwork - zip2.xml");
    }

    private void testProvider(String configFile) throws Exception {
        ConfigurationProvider provider = buildConfigurationProvider(configFile);
        assertTrue(!provider.needsReload());

        String fullPath = ClassLoaderUtil.getResource(configFile, ConfigurationProvider.class).toString();

        int startIndex = fullPath.indexOf(":file:/");
        int endIndex = fullPath.indexOf("!/");

        String jar = fullPath.substring(startIndex + (":file:/".length() - 1), endIndex).replaceAll("%20", " ");

        File file = new File(jar);

        assertTrue("File [" + file + "] doesn't exist!", file.exists());
        file.setLastModified(System.currentTimeMillis());

        assertTrue(!provider.needsReload());
    }

}
