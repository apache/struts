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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.impl.MockConfiguration;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.result.ServletDispatcherResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class XmlConfigurationProviderTest extends ConfigurationTestBase {

    public void testLoadOrder() throws Exception {
        configuration = new MockConfiguration();
        ((MockConfiguration) configuration).selfRegister();
        container = configuration.getContainer();

        XmlConfigurationProvider prov = new StrutsXmlConfigurationProvider("xwork-test-load-order.xml") {
            @Override
            protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
                List<URL> urls = new ArrayList<>();
                urls.add(ClassLoaderUtil.getResource("com/opensymphony/xwork2/config/providers/loadorder1/xwork-test-load-order.xml", XmlConfigurationProvider.class));
                urls.add(ClassLoaderUtil.getResource("com/opensymphony/xwork2/config/providers/loadorder2/xwork-test-load-order.xml", XmlConfigurationProvider.class));
                urls.add(ClassLoaderUtil.getResource("com/opensymphony/xwork2/config/providers/loadorder3/xwork-test-load-order.xml", XmlConfigurationProvider.class));
                return urls.iterator();
            }
        };
        prov.setObjectFactory(container.getInstance(ObjectFactory.class));
        prov.setFileManagerFactory(container.getInstance(FileManagerFactory.class));
        prov.init(configuration);
        List<Document> docs = prov.getDocuments();
        assertEquals(3, docs.size());

        assertEquals(1, XmlHelper.getLoadOrder(docs.get(0)).intValue());
        assertEquals(2, XmlHelper.getLoadOrder(docs.get(1)).intValue());
        assertEquals(3, XmlHelper.getLoadOrder(docs.get(2)).intValue());
    }

    public static final long FILE_TS_WAIT_IN_MS = 3600000;

    private static void changeFileTime(String filename, File f) throws Exception {
        final long orig = f.lastModified();
        final long maxwait = orig + FILE_TS_WAIT_IN_MS;
        long curr;
        while (!f.setLastModified(curr = System.currentTimeMillis()) || orig == f.lastModified()) {
            Thread.sleep(500);
            assertTrue("Waited more than " + FILE_TS_WAIT_IN_MS + " ms to update timestamp on file: " + f, maxwait > curr);
        }
        ActionContext.getContext().with("configurationReload-" + filename, null);
    }

    public void testNeedsReload() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-actions.xml";
        ConfigurationProvider provider = new StrutsXmlConfigurationProvider(filename);
        container.inject(provider);
        provider.init(configuration);
        provider.loadPackages();
        container.getInstance(FileManagerFactory.class).getFileManager().setReloadingConfigs(true);

        assertFalse(provider.needsReload()); // Revision exists and timestamp didn't change

        File file = new File(getClass().getResource("/" + filename).toURI());
        assertTrue("not exists: " + file.toString(), file.exists());
        changeFileTime(filename, file);

        assertTrue(provider.needsReload());
    }

    public void testReload() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-reload.xml";
        ConfigurationProvider provider = new StrutsXmlConfigurationProvider(filename);
        loadConfigurationProviders(provider);

        assertFalse(provider.needsReload()); // Revision exists and timestamp didn't change

        File file = new File(getClass().getResource("/" + filename).toURI());
        assertTrue("not exists: " + file.toString(), file.exists());

        Path configPath = Paths.get(file.getAbsolutePath());
        String content = new String(Files.readAllBytes(configPath));
        content = content.replaceAll("<constant name=\"struts.configuration.xml.reload\" value=\"true\" />",
            "<constant name=\"struts.configuration.xml.reload\" value=\"false\" />");
        Files.write(configPath, content.getBytes()); // user demand: stop reloading configs

        try {
            assertTrue(provider.needsReload()); // config file has changed in previous lines

            configurationManager.reload();

            changeFileTime(filename, file);
            assertFalse(provider.needsReload());    // user already has stopped reloading configs
        } finally {
            content = content.replaceAll("<constant name=\"struts.configuration.xml.reload\" value=\"false\" />",
                "<constant name=\"struts.configuration.xml.reload\" value=\"true\" />");
            Files.write(configPath, content.getBytes());
        }
    }

    public void testNeedsReloadNotReloadingConfigs() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-actions.xml";
        buildConfigurationProvider(filename);
        ConfigurationProvider provider = new StrutsXmlConfigurationProvider(filename);
        container.getInstance(FileManagerFactory.class).getFileManager().setReloadingConfigs(false);
        container.inject(provider);
        provider.init(configuration);
        provider.loadPackages();

        assertFalse(provider.needsReload()); // Revision exists and timestamp didn't change

        File file = new File(getClass().getResource("/" + filename).toURI());
        assertTrue("not exists: " + file.toString(), file.exists());
        changeFileTime(filename, file);

        assertFalse(provider.needsReload());
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
        XmlConfigurationProvider prov = new StrutsXmlConfigurationProvider("xwork.xml");

        assertEquals(null, prov.guessResultType(null));
        assertEquals("foo", prov.guessResultType("foo"));
        assertEquals("foo", prov.guessResultType("foo-"));
        assertEquals("fooBar", prov.guessResultType("foo-bar"));
        assertEquals("fooBarBaz", prov.guessResultType("foo-bar-baz"));
    }

    public void testEmptySpaces() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork- test.xml";
        ConfigurationProvider provider = new StrutsXmlConfigurationProvider(filename);
        container.inject(provider);
        provider.init(configuration);
        provider.loadPackages();
        container.getInstance(FileManagerFactory.class).getFileManager().setReloadingConfigs(true);

        assertFalse(provider.needsReload());

        URI uri = ClassLoaderUtil.getResource(filename, ConfigurationProvider.class).toURI();

        File file = new File(uri);

        assertTrue(file.exists());
        changeFileTime(filename, file);

        assertTrue(provider.needsReload());
    }

    public void testEmptySpacesNotReloadingConfigs() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork- test.xml";
        buildConfigurationProvider(filename);
        ConfigurationProvider provider = new StrutsXmlConfigurationProvider(filename);
        container.getInstance(FileManagerFactory.class).getFileManager().setReloadingConfigs(false);
        container.inject(provider);
        provider.init(configuration);
        provider.loadPackages();

        assertFalse(provider.needsReload());

        URI uri = ClassLoaderUtil.getResource(filename, ConfigurationProvider.class).toURI();

        File file = new File(uri);

        assertTrue(file.exists());
        changeFileTime(filename, file);

        assertFalse(provider.needsReload());
    }

    public void testConfigsInJarFiles() throws Exception {
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
        container.getInstance(FileManagerFactory.class).getFileManager().setReloadingConfigs(true);
        assertFalse(provider.needsReload());

        String fullPath = ClassLoaderUtil.getResource(configFile, ConfigurationProvider.class).toString();

        int startIndex = fullPath.indexOf(":file:/");
        int endIndex = fullPath.indexOf("!/");

        String jar = fullPath.substring(startIndex + (":file:/".length() - 1), endIndex).replaceAll("%20", " ");

        File file = new File(jar);

        assertTrue("File [" + file + "] doesn't exist!", file.exists());
        changeFileTime(jar, file);

        assertFalse(provider.needsReload());
    }

    public void testIncludeWithWildcard() throws Exception {
        String configFile = "com/opensymphony/xwork2/config/providers/xwork-test-include-wildcard.xml";
        buildConfigurationProvider(configFile);

        Set<String> loadedFileNames = configuration.getLoadedFileNames();
        assertEquals(8, loadedFileNames.size());
        assertTrue(loadedFileNames.contains("com/opensymphony/xwork2/config/providers/xwork-include-after-package.xml"));
        assertTrue(loadedFileNames.contains("com/opensymphony/xwork2/config/providers/xwork-include-after-package-2.xml"));
        assertTrue(loadedFileNames.contains("com/opensymphony/xwork2/config/providers/xwork-include-before-package.xml"));
        assertTrue(loadedFileNames.contains("com/opensymphony/xwork2/config/providers/xwork-include-before-package-2.xml"));
        assertTrue(loadedFileNames.contains("com/opensymphony/xwork2/config/providers/xwork-include-parent.xml"));
        assertTrue(loadedFileNames.contains("com/opensymphony/xwork2/config/providers/xwork-test-include-wildcard.xml"));
        assertTrue(loadedFileNames.contains("xwork-test-beans.xml"));
        assertTrue(loadedFileNames.contains("xwork-test-default.xml"));
    }

    /**
     * Test buildAllowedMethods() to ensure consistent results for processing
     * <allowed-methods/> in <action/> XML configuration elements.
     *
     * @throws Exception
     */
    public void testBuildAllowedMethods() throws Exception {
        // Test introduced with WW-5029 fix.
        // Set up test using two mock DOM Elements:
        //   1) A mock "action" Element with a single "allowed-methods" child Element that contains a single
        //      TEXT_NODE Node.  This simulates a typical result from a SAX parser parsing the allowed-methods
        //      tag body.
        //   2) A mock "action" Element with a single "allowed-methods" child Element that contains multiple
        //      TEXT_NODE Nodes.  This simulates an unusal result from a SAX parser parsing the allowed-methods
        //      tag body (as reported with WW-5029).
        final String fakeBodyString = "allowedMethod1,allowedMethod2,allowedMethod3";
        PackageConfig.Builder testPackageConfigBuilder = new PackageConfig.Builder("allowedMethodsPackage");
        List<String> singleStringList = new ArrayList<>(1);
        List<String> multipleStringList = new ArrayList<>(4);
        singleStringList.add(fakeBodyString);
        multipleStringList.add("allowedMethod1,");
        multipleStringList.add("allowed");
        multipleStringList.add("Method2,");
        multipleStringList.add("allowedMethod3");
        NodeList mockNodeListSingleChild = new MockNodeList(singleStringList);
        NodeList mockNodeListMultipleChild = new MockNodeList(multipleStringList);
        Element mockSingleChildAllowedMethodsElement = new MockElement("allowed-methods", fakeBodyString,
            "allowed-methods", fakeBodyString, Node.TEXT_NODE, mockNodeListSingleChild, null);
        Element mockMultipleChildAllowedMethodsElement = new MockElement("allowed-methods", fakeBodyString,
            "allowed-methods", fakeBodyString, Node.TEXT_NODE, mockNodeListMultipleChild, null);
        MockNodeList mockActionElementChildrenSingle = new MockNodeList();
        mockActionElementChildrenSingle.addToNodeList(mockSingleChildAllowedMethodsElement);
        MockNodeList mockActionElementChildrenMultiple = new MockNodeList();
        mockActionElementChildrenMultiple.addToNodeList(mockMultipleChildAllowedMethodsElement);
        Element mockActionElementSingle = new MockElement("action", "fakeBody", "action", "fakeValue",
            Node.TEXT_NODE, mockActionElementChildrenSingle, null);
        Element mockActionElementMultiple = new MockElement("action", "fakeBody", "action", "fakeValue",
            Node.TEXT_NODE, mockActionElementChildrenMultiple, null);
        // Attempt the method using both types of Elements (single child and multiple child) and confirm
        // the result is the same for both.  Also confirm the results are as expected.
        XmlConfigurationProvider prov = new StrutsXmlConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork- test.xml");
        Set<String> singleChildResult = prov.buildAllowedMethods(mockActionElementSingle, testPackageConfigBuilder);
        Set<String> multipleChildResult = prov.buildAllowedMethods(mockActionElementMultiple, testPackageConfigBuilder);
        assertNotNull("singleChildResult is null ?", singleChildResult);
        assertNotNull("multipleChildResult is null ?", multipleChildResult);
        assertEquals("singleChildResult not equal to multipleChildResult ?", singleChildResult, multipleChildResult);
        // Since both Sets are equal, only need to test one to confirm contents are correct
        assertEquals("result Sets not of length 3 ?", 3, multipleChildResult.size());
        assertTrue("allowedMethod1 not present ?", multipleChildResult.contains("allowedMethod1"));
        assertTrue("allowedMethod2 not present ?", multipleChildResult.contains("allowedMethod2"));
        assertTrue("allowedMethod3 not present ?", multipleChildResult.contains("allowedMethod3"));
        assertFalse("noSuchMethod is present ?", multipleChildResult.contains("noSuchMethod"));
    }

    /**
     * Test loadGlobalAllowedMethods() to ensure consistent results for processing
     * <global-allowed-methods/> in <package/> XML configuration elements.
     *
     * @throws Exception
     */
    public void testLoadGlobalAllowedMethods() throws Exception {
        // Test introduced with WW-5029 fix.
        // Set up test using two mock DOM Elements:
        //   1) A mock "package" Element with a single "global-allowed-methods" child Element that contains a single
        //      TEXT_NODE Node.  This simulates a typical result from a SAX parser parsing the global-allowed-methods
        //      tag body.
        //   2) A mock "package" Element with a single "global-allowed-methods" child Element that contains multiple
        //      TEXT_NODE Nodes.  This simulates an unusal result from a SAX parser parsing the global-allowed-methods
        //      tag body (as reported with WW-5029).
        final String fakeBodyString = "allowedMethod1,allowedMethod2,allowedMethod3";
        PackageConfig.Builder testPackageConfigBuilder = new PackageConfig.Builder("globalAllowedMethodsPackage");
        List<String> singleStringList = new ArrayList<>(1);
        List<String> multipleStringList = new ArrayList<>(4);
        singleStringList.add(fakeBodyString);
        multipleStringList.add("allowedMethod4,");
        multipleStringList.add("allowed");
        multipleStringList.add("Method5,");
        multipleStringList.add("allowedMethod6");
        NodeList mockNodeListSingleChild = new MockNodeList(singleStringList);
        NodeList mockNodeListMultipleChild = new MockNodeList(multipleStringList);
        Element mockSingleChildAllowedMethodsElement = new MockElement("global-allowed-methods", fakeBodyString,
            "global-allowed-methods", fakeBodyString, Node.TEXT_NODE, mockNodeListSingleChild, null);
        Element mockMultipleChildAllowedMethodsElement = new MockElement("global-allowed-methods", fakeBodyString,
            "global-allowed-methods", fakeBodyString, Node.TEXT_NODE, mockNodeListMultipleChild, null);
        MockNodeList mockPackageElementChildrenSingle = new MockNodeList();
        mockPackageElementChildrenSingle.addToNodeList(mockSingleChildAllowedMethodsElement);
        MockNodeList mockPackageElementChildrenMultiple = new MockNodeList();
        mockPackageElementChildrenMultiple.addToNodeList(mockMultipleChildAllowedMethodsElement);
        Element mockPackageElementSingle = new MockElement("package", "fakeBody", "package", "fakeValue",
            Node.TEXT_NODE, mockPackageElementChildrenSingle, null);
        Element mockPackageElementMultiple = new MockElement("package", "fakeBody", "package", "fakeValue",
            Node.TEXT_NODE, mockPackageElementChildrenMultiple, null);
        // Attempt the method using the single child Element and confirm the result is as expected.
        XmlConfigurationProvider prov = new StrutsXmlConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork- test.xml");
        prov.loadGlobalAllowedMethods(testPackageConfigBuilder, mockPackageElementSingle);
        Set<String> currentGlobalResult = testPackageConfigBuilder.getGlobalAllowedMethods();
        assertNotNull("currentGlobalResult is null ?", currentGlobalResult);
        assertEquals("currentGlobalResult Sets not of length 3 ?", 3, currentGlobalResult.size());
        assertTrue("allowedMethod1 not present ?", currentGlobalResult.contains("allowedMethod1"));
        assertTrue("allowedMethod2 not present ?", currentGlobalResult.contains("allowedMethod2"));
        assertTrue("allowedMethod3 not present ?", currentGlobalResult.contains("allowedMethod3"));
        assertFalse("noSuchMethod is present ?", currentGlobalResult.contains("noSuchMethod"));
        // Attempt the method using the multiple child Element and confirm the result is as expected.
        prov.loadGlobalAllowedMethods(testPackageConfigBuilder, mockPackageElementMultiple);
        currentGlobalResult = testPackageConfigBuilder.getGlobalAllowedMethods();
        assertNotNull("currentGlobalResult is null ?", currentGlobalResult);
        assertEquals("currentGlobalResult Sets not of length 6 ?", 6, currentGlobalResult.size());
        assertTrue("allowedMethod4 not present ?", currentGlobalResult.contains("allowedMethod4"));
        assertTrue("allowedMethod5 not present ?", currentGlobalResult.contains("allowedMethod5"));
        assertTrue("allowedMethod6 not present ?", currentGlobalResult.contains("allowedMethod6"));
        assertFalse("noSuchMethod is present ?", currentGlobalResult.contains("snoSUchMethod"));
        // Confirm the previously added elements are still present
        assertTrue("allowedMethod1 not present ?", currentGlobalResult.contains("allowedMethod1"));
        assertTrue("allowedMethod2 not present ?", currentGlobalResult.contains("allowedMethod2"));
        assertTrue("allowedMethod3 not present ?", currentGlobalResult.contains("allowedMethod3"));
    }

    /**
     * Test buildResults() to ensure consistent results for processing
     * <result/> in <action/> XML configuration elements.
     *
     * @throws Exception
     */
    public void testBuildResults() throws Exception {
        // Set up test using two mock DOM Elements:
        //   1) A mock "action" Element with a two "result" child Elements that each contains a single
        //      TEXT_NODE Node.  This simulates a typical result from a SAX parser parsing the result
        //      tag body.
        //   2) A mock "action" Element with two "result" child Elements that each contain multiple
        //      TEXT_NODE Nodes.  This simulates an unusal result from a SAX parser parsing the result
        //      tag body.
        final String fakeBodyString = "/SomePath/SomePath/SomePath/SomeJSP.jsp";
        final String fakeBodyString2 = "/SomePath2/SomePath2/SomePath2/SomeJSP2.jsp";
        final String resultParam = "nonNullDefaultParam";
        PackageConfig.Builder testPackageConfigBuilder = new PackageConfig.Builder("resultsPackage");
        ResultTypeConfig.Builder resultTypeConfigBuilder = new ResultTypeConfig.Builder("dispatcher", ServletDispatcherResult.class.getName());
        resultTypeConfigBuilder.defaultResultParam(resultParam);
        ResultTypeConfig resultTypeConfig = resultTypeConfigBuilder.build();
        testPackageConfigBuilder.addResultTypeConfig(resultTypeConfig);
        List<String> singleStringList = new ArrayList<>(1);
        List<String> singleStringList2 = new ArrayList<>(1);
        List<String> multipleStringList = new ArrayList<>(4);
        List<String> multipleStringList2 = new ArrayList<>(4);
        singleStringList.add(fakeBodyString);
        singleStringList2.add(fakeBodyString2);
        multipleStringList.add("/SomePath");
        multipleStringList.add("/SomePath/");
        multipleStringList.add("SomePath/");
        multipleStringList.add("SomeJSP.jsp");
        multipleStringList2.add("/SomePath2");
        multipleStringList2.add("/SomePath2/");
        multipleStringList2.add("SomePath2/");
        multipleStringList2.add("SomeJSP2.jsp");
        NodeList mockNodeListSingleChild = new MockNodeList(singleStringList);
        NodeList mockNodeListSingleChild2 = new MockNodeList(singleStringList2);
        NodeList mockNodeListMultipleChild = new MockNodeList(multipleStringList);
        NodeList mockNodeListMultipleChild2 = new MockNodeList(multipleStringList2);
        Element mockSingleChildResultElement = new MockElement("result", fakeBodyString,
            "result", fakeBodyString, Node.TEXT_NODE, mockNodeListSingleChild, null);
        mockSingleChildResultElement.setAttribute("name", "input");
        mockSingleChildResultElement.setAttribute("type", "dispatcher");
        Element mockSingleChildResultElement2 = new MockElement("result", fakeBodyString2,
            "result", fakeBodyString2, Node.TEXT_NODE, mockNodeListSingleChild2, null);
        mockSingleChildResultElement2.setAttribute("name", "success");
        mockSingleChildResultElement2.setAttribute("type", "dispatcher");
        Element mockMultipleChildAllowedMethodsElement = new MockElement("result", fakeBodyString,
            "result", fakeBodyString, Node.TEXT_NODE, mockNodeListMultipleChild, null);
        mockMultipleChildAllowedMethodsElement.setAttribute("name", "input");
        mockMultipleChildAllowedMethodsElement.setAttribute("type", "dispatcher");
        Element mockMultipleChildAllowedMethodsElement2 = new MockElement("result", fakeBodyString2,
            "result", fakeBodyString2, Node.TEXT_NODE, mockNodeListMultipleChild2, null);
        mockMultipleChildAllowedMethodsElement2.setAttribute("name", "success");
        mockMultipleChildAllowedMethodsElement2.setAttribute("type", "dispatcher");
        MockNodeList mockActionElementChildrenSingle = new MockNodeList();
        mockActionElementChildrenSingle.addToNodeList(mockSingleChildResultElement);
        mockActionElementChildrenSingle.addToNodeList(mockSingleChildResultElement2);
        MockNodeList mockActionElementChildrenMultiple = new MockNodeList();
        mockActionElementChildrenMultiple.addToNodeList(mockMultipleChildAllowedMethodsElement);
        mockActionElementChildrenMultiple.addToNodeList(mockMultipleChildAllowedMethodsElement2);
        Element mockActionElementSingle = new MockElement("action", "fakeBody", "action", "fakeValue",
            Node.TEXT_NODE, mockActionElementChildrenSingle, null);
        Element mockActionElementMultiple = new MockElement("action", "fakeBody", "action", "fakeValue",
            Node.TEXT_NODE, mockActionElementChildrenMultiple, null);
        // Attempt the method using both types of Elements (single child and multiple child) and confirm
        // the result is the same for both.  Also confirm the results are as expected.
        XmlConfigurationProvider prov = new StrutsXmlConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork- test.xml");
        Map<String, ResultConfig> singleChildResult = prov.buildResults(mockActionElementSingle, testPackageConfigBuilder);
        Map<String, ResultConfig> multipleChildResult = prov.buildResults(mockActionElementMultiple, testPackageConfigBuilder);
        assertNotNull("singleChildResult is null ?", singleChildResult);
        assertNotNull("multipleChildResult is null ?", multipleChildResult);
        assertEquals("singleChildResult not equal to multipleChildResult ?", singleChildResult, multipleChildResult);
        // Since both Maps are equal, only need to test one to confirm the contents of ONE are correct.contents are correct
        // The multipleChildResult (constructed from the multiple child TEXT_NODES) will be checked for correctness.
        assertEquals("result Maps not of size 2 ?", 2, multipleChildResult.size());
        ResultConfig inputResult = multipleChildResult.get("input");
        ResultConfig successResult = multipleChildResult.get("success");
        assertNotNull("inputResult (multipleChildResult) is null ?", inputResult);
        assertNotNull("successResult (multipleChildResult) is null ?", successResult);
        Map<String, String> inputResultParams = inputResult.getParams();
        Map<String, String> successResultParams = successResult.getParams();
        assertNotNull("inputResultParams (multipleChildResult) is null ?", inputResultParams);
        assertNotNull("successResultParams (multipleChildResult) is null ?", successResultParams);
        assertEquals("inputResult (multipleChildResult) resultParam value not equal to fakeBodyString ?",
            fakeBodyString, inputResultParams.get(resultParam));
        assertEquals("successResult (multipleChildResult) resultParam value not equal to fakeBodyString2 ?",
            fakeBodyString2, successResultParams.get(resultParam));
    }

    /**
     * Test loadGlobalResults() to ensure consistent results for processing
     * <global-results/> in <package/> XML configuration elements.
     *
     * @throws Exception
     */
    public void testLoadGlobalResults() throws Exception {
        // Set up test using two mock DOM Elements:
        //   1) A mock "package" Element containing a mock "global-results" Element with a two "result"
        //      child Elements that each contains a single TEXT_NODE Node.  This simulates a typical result
        //      from a SAX parser parsing the result tag body.
        //   2) A mock "package" Element containing a mock "global-results" Element with two "result"
        //      child Elements that each contain multiple TEXT_NODE Nodes.  This simulates an unusal result
        //      from a SAX parser parsing the result tag body.
        final String fakeBodyString = "/SomePath/SomePath/SomePath/SomeJSP.jsp";
        final String fakeBodyString2 = "/SomePath2/SomePath2/SomePath2/SomeJSP2.jsp";
        final String resultParam = "nonNullDefaultParam";
        PackageConfig.Builder testPackageConfigBuilder = new PackageConfig.Builder("resultsPackage");
        ResultTypeConfig.Builder resultTypeConfigBuilder = new ResultTypeConfig.Builder("dispatcher", ServletDispatcherResult.class.getName());
        resultTypeConfigBuilder.defaultResultParam(resultParam);
        ResultTypeConfig resultTypeConfig = resultTypeConfigBuilder.build();
        testPackageConfigBuilder.addResultTypeConfig(resultTypeConfig);
        List<String> singleStringList = new ArrayList<>(1);
        List<String> singleStringList2 = new ArrayList<>(1);
        List<String> multipleStringList = new ArrayList<>(4);
        List<String> multipleStringList2 = new ArrayList<>(4);
        singleStringList.add(fakeBodyString);
        singleStringList2.add(fakeBodyString2);
        multipleStringList.add("/SomePath");
        multipleStringList.add("/SomePath/");
        multipleStringList.add("SomePath/");
        multipleStringList.add("SomeJSP.jsp");
        multipleStringList2.add("/SomePath2");
        multipleStringList2.add("/SomePath2/");
        multipleStringList2.add("SomePath2/");
        multipleStringList2.add("SomeJSP2.jsp");
        NodeList mockNodeListSingleChild = new MockNodeList(singleStringList);
        NodeList mockNodeListSingleChild2 = new MockNodeList(singleStringList2);
        NodeList mockNodeListMultipleChild = new MockNodeList(multipleStringList);
        NodeList mockNodeListMultipleChild2 = new MockNodeList(multipleStringList2);
        Element mockSingleChildResultElement = new MockElement("result", fakeBodyString,
            "result", fakeBodyString, Node.TEXT_NODE, mockNodeListSingleChild, null);
        mockSingleChildResultElement.setAttribute("name", "input");
        mockSingleChildResultElement.setAttribute("type", "dispatcher");
        Element mockSingleChildResultElement2 = new MockElement("result", fakeBodyString2,
            "result", fakeBodyString2, Node.TEXT_NODE, mockNodeListSingleChild2, null);
        mockSingleChildResultElement2.setAttribute("name", "success");
        mockSingleChildResultElement2.setAttribute("type", "dispatcher");
        Element mockMultipleChildAllowedMethodsElement = new MockElement("result", fakeBodyString,
            "result", fakeBodyString, Node.TEXT_NODE, mockNodeListMultipleChild, null);
        mockMultipleChildAllowedMethodsElement.setAttribute("name", "input2");
        mockMultipleChildAllowedMethodsElement.setAttribute("type", "dispatcher");
        Element mockMultipleChildAllowedMethodsElement2 = new MockElement("result", fakeBodyString,
            "result", fakeBodyString2, Node.TEXT_NODE, mockNodeListMultipleChild2, null);
        mockMultipleChildAllowedMethodsElement2.setAttribute("name", "success2");
        mockMultipleChildAllowedMethodsElement2.setAttribute("type", "dispatcher");
        MockNodeList mockGlobalResultsElementChildrenSingle = new MockNodeList();
        mockGlobalResultsElementChildrenSingle.addToNodeList(mockSingleChildResultElement);
        mockGlobalResultsElementChildrenSingle.addToNodeList(mockSingleChildResultElement2);
        MockNodeList mockGlobalResultsElementChildrenMultiple = new MockNodeList();
        mockGlobalResultsElementChildrenMultiple.addToNodeList(mockMultipleChildAllowedMethodsElement);
        mockGlobalResultsElementChildrenMultiple.addToNodeList(mockMultipleChildAllowedMethodsElement2);
        Element mockGlobalResultsElementSingle = new MockElement("global-results", "fakeBody", "global-results", "fakeValue",
            Node.TEXT_NODE, mockGlobalResultsElementChildrenSingle, null);
        Element mockGlobalResultsGlobalResultsElementMultiple = new MockElement("global-results", "fakeBody", "global-results", "fakeValue",
            Node.TEXT_NODE, mockGlobalResultsElementChildrenMultiple, null);
        MockNodeList mockPackageElementChildrenSingle = new MockNodeList();
        mockPackageElementChildrenSingle.addToNodeList(mockGlobalResultsElementSingle);
        MockNodeList mockPackageElementChildrenMultiple = new MockNodeList();
        mockPackageElementChildrenMultiple.addToNodeList(mockGlobalResultsGlobalResultsElementMultiple);
        Element mockPackageElementSingle = new MockElement("package", "fakeBody", "package", "fakeValue",
            Node.TEXT_NODE, mockPackageElementChildrenSingle, null);
        Element mockPackageElementMultiple = new MockElement("package", "fakeBody", "package", "fakeValue",
            Node.TEXT_NODE, mockPackageElementChildrenMultiple, null);
        // Attempt the global laod method using single child Elements first, and confirm the results are as expected.
        XmlConfigurationProvider prov = new StrutsXmlConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork- test.xml");
        prov.loadGlobalResults(testPackageConfigBuilder, mockPackageElementSingle);
        PackageConfig testPackageConfig = testPackageConfigBuilder.build();
        Map<String, ResultConfig> currentGlobalResults = testPackageConfig.getAllGlobalResults();
        assertNotNull("currentGlobalResults is null ?", currentGlobalResults);
        assertEquals("currentGlobalResults size not 2 ?", 2, currentGlobalResults.size());
        ResultConfig inputResult = currentGlobalResults.get("input");
        ResultConfig successResult = currentGlobalResults.get("success");
        assertNotNull("inputResult (currentGlobalResults - single) is null ?", inputResult);
        assertNotNull("successResult (currentGlobalResults - single) is null ?", successResult);
        Map<String, String> inputResultParams = inputResult.getParams();
        Map<String, String> successResultParams = successResult.getParams();
        assertNotNull("inputResultParams (currentGlobalResults - single) is null ?", inputResultParams);
        assertNotNull("successResultParams (currentGlobalResults - single) is null ?", successResultParams);
        assertEquals("inputResult (currentGlobalResults - single) resultParam value not equal to fakeBodyString ?",
            fakeBodyString, inputResultParams.get(resultParam));
        assertEquals("successResult (currentGlobalResults - single) resultParam value not equal to fakeBodyString2 ?",
            fakeBodyString2, successResultParams.get(resultParam));
        // Attempt the global laod method using mutliple child Elements next, and confirm the results are as expected.
        prov.loadGlobalResults(testPackageConfigBuilder, mockPackageElementMultiple);
        testPackageConfig = testPackageConfigBuilder.build();
        currentGlobalResults = testPackageConfig.getAllGlobalResults();
        assertNotNull("currentGlobalResults is null ?", currentGlobalResults);
        assertEquals("currentGlobalResults size not 4 ?", 4, currentGlobalResults.size());
        ResultConfig inputResult2 = currentGlobalResults.get("input2");
        ResultConfig successResult2 = currentGlobalResults.get("success2");
        assertNotNull("inputResult2 (currentGlobalResults - multiple) is null ?", inputResult2);
        assertNotNull("successResult2 (currentGlobalResults - multiple) is null ?", successResult2);
        Map<String, String> inputResultParams2 = inputResult2.getParams();
        Map<String, String> successResultParams2 = successResult2.getParams();
        assertNotNull("inputResultParams2 (currentGlobalResults - multiple) is null ?", inputResultParams2);
        assertNotNull("successResultParams2 (currentGlobalResults - multiple) is null ?", successResultParams2);
        assertEquals("inputResult2 (currentGlobalResults - multiple) resultParam value not equal to fakeBodyString ?",
            fakeBodyString, inputResultParams2.get(resultParam));
        assertEquals("successResult2 (currentGlobalResults - multiple) resultParam value not equal to fakeBodyString2 ?",
            fakeBodyString2, successResultParams2.get(resultParam));
        // Confirm the previous global results are still present
        inputResult = currentGlobalResults.get("input");
        successResult = currentGlobalResults.get("success");
        assertNotNull("inputResult (currentGlobalResults - single) is null ?", inputResult);
        assertNotNull("successResult (currentGlobalResults - single) is null ?", successResult);
        inputResultParams = inputResult.getParams();
        successResultParams = successResult.getParams();
        assertNotNull("inputResultParams (currentGlobalResults - single) is null ?", inputResultParams);
        assertNotNull("successResultParams (currentGlobalResults - single) is null ?", successResultParams);
        assertEquals("inputResult (currentGlobalResults - single) resultParam value not equal to fakeBodyString ?",
            fakeBodyString, inputResultParams.get(resultParam));
        assertEquals("successResult (currentGlobalResults - single) resultParam value not equal to fakeBodyString2 ?",
            fakeBodyString2, successResultParams.get(resultParam));
        inputResult = currentGlobalResults.get("input");
        successResult = currentGlobalResults.get("success");
        assertNotNull("inputResult (currentGlobalResults - single) is null ?", inputResult);
        assertNotNull("successResult (currentGlobalResults - single) is null ?", successResult);
        inputResultParams = inputResult.getParams();
        successResultParams = successResult.getParams();
        assertNotNull("inputResultParams (currentGlobalResults - single) is null ?", inputResultParams);
        assertNotNull("successResultParams (currentGlobalResults - single) is null ?", successResultParams);
        assertEquals("inputResult (currentGlobalResults - single) resultParam value not equal to fakeBodyString ?",
            fakeBodyString, inputResultParams.get(resultParam));
        assertEquals("successResult (currentGlobalResults - single) resultParam value not equal to fakeBodyString2 ?",
            fakeBodyString2, successResultParams.get(resultParam));
    }

    /**
     * Mock NodeList.
     * <p>
     * Provides minimal functionality to permit limited mock DOM testing.
     */
    protected class MockNodeList implements org.w3c.dom.NodeList {
        List<Node> nodeList;

        public MockNodeList() {
            this.nodeList = new ArrayList<>(0);
        }

        /**
         * Produces TEXT_NODE Nodes based on the input List of Strings.  Node names
         * follow a simple pattern "nodeX" where X is the index.
         *
         * @param stringList
         */
        public MockNodeList(List<String> stringList) {
            if (stringList != null) {
                final int nodeListLength = stringList.size();
                this.nodeList = new ArrayList<>(nodeListLength);
                for (int index = 0; index < nodeListLength; index++) {
                    this.nodeList.add(new MockNode("node" + index, stringList.get(index), Node.TEXT_NODE, null, null));
                }
            } else {
                this.nodeList = new ArrayList<>(0);
            }
        }

        public MockNodeList(NodeList nodeList) {
            if (nodeList != null) {
                final int nodeListLength = nodeList.getLength();
                this.nodeList = new ArrayList<>(nodeListLength);
                for (int index = 0; index < nodeListLength; index++) {
                    this.nodeList.add(nodeList.item(index));
                }
            } else {
                this.nodeList = new ArrayList<>(0);
            }
        }

        public void addToNodeList(List<Node> nodeList) {
            if (nodeList != null) {
                final int nodeListLength = nodeList.size();
                for (int index = 0; index < nodeListLength; index++) {
                    this.nodeList.add(nodeList.get(index));
                }
            } else {
                this.nodeList = new ArrayList<>(0);
            }
        }

        public void addToNodeList(Node node) {
            nodeList.add(node);
        }

        public void setParentForListNodes(Node parentNode) {
            if (nodeList != null) {
                final int nodeListLength = nodeList.size();
                for (int index = 0; index < nodeListLength; index++) {
                    Node node = nodeList.get(index);
                    if (node instanceof MockNode) {
                        MockNode mockNode = (MockNode) node;
                        mockNode.setParentNode(parentNode);
                    }
                }
            }
        }

        @Override
        public Node item(int index) {
            return (nodeList != null ? nodeList.get(index) : null);
        }

        @Override
        public int getLength() {
            return (nodeList != null ? nodeList.size() : 0);
        }
    }

    /**
     * MockNode
     * <p>
     * Provides minimal functionality to permit limited mock DOM testing.
     */
    protected class MockNode implements org.w3c.dom.Node {
        final private String nodeName;
        private String nodeValue;
        final private short nodeType;
        private Node parentNode;
        final private NodeList childNodes;

        public MockNode(String nodeName, String nodeValue, short nodeType, NodeList childNodes, Node parentNode) {
            this.nodeName = nodeName;
            this.nodeValue = nodeValue;
            this.nodeType = nodeType;
            this.childNodes = childNodes;
            this.parentNode = parentNode;

            if (childNodes instanceof MockNodeList) {
                MockNodeList mockNodeList = (MockNodeList) childNodes;
                mockNodeList.setParentForListNodes(this);
            }
        }

        @Override
        public String getNodeName() {
            return nodeName;
        }

        @Override
        public String getNodeValue() throws DOMException {
            return nodeValue;
        }

        @Override
        public void setNodeValue(String nodeValue) throws DOMException {
            this.nodeValue = nodeValue;
        }

        @Override
        public short getNodeType() {
            return nodeType;
        }

        public void setParentNode(Node parentNode) {
            this.parentNode = parentNode;
        }

        @Override
        public Node getParentNode() {
            return parentNode;
        }

        @Override
        public NodeList getChildNodes() {
            return childNodes;
        }

        @Override
        public Node getFirstChild() {
            if (childNodes != null) {
                return childNodes.item(0);
            } else {
                return null;
            }
        }

        @Override
        public Node getLastChild() {
            if (childNodes != null) {
                return childNodes.item(childNodes.getLength());
            } else {
                return null;
            }
        }

        @Override
        public Node getPreviousSibling() {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Node getNextSibling() {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public NamedNodeMap getAttributes() {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Document getOwnerDocument() {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Node insertBefore(Node newChild, Node refChild) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Node removeChild(Node oldChild) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Node appendChild(Node newChild) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public boolean hasChildNodes() {
            return (childNodes != null && childNodes.getLength() > 0);
        }

        @Override
        public Node cloneNode(boolean deep) {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public void normalize() {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public boolean isSupported(String feature, String version) {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public String getNamespaceURI() {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public String getPrefix() {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public void setPrefix(String prefix) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public String getLocalName() {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public boolean hasAttributes() {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public String getBaseURI() {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public short compareDocumentPosition(Node other) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public String getTextContent() throws DOMException {
            return nodeValue;
        }

        @Override
        public void setTextContent(String textContent) throws DOMException {
            setNodeValue(textContent);
        }

        @Override
        public boolean isSameNode(Node other) {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public String lookupPrefix(String namespaceURI) {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public boolean isDefaultNamespace(String namespaceURI) {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public String lookupNamespaceURI(String prefix) {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public boolean isEqualNode(Node arg) {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Object getFeature(String feature, String version) {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Object setUserData(String key, Object data, UserDataHandler handler) {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Object getUserData(String key) {
            throw new UnsupportedOperationException("No mock support.");
        }
    }

    /**
     * Mock Element.
     * <p>
     * Provides minimal functionality to permit limited mock DOM testing.
     */
    protected class MockElement extends MockNode implements org.w3c.dom.Element {
        final private String tagName;
        final private String tagBody;
        final private Map<String, String> attributes;

        public MockElement(String tagName, String tagBody,
                           String nodeName, String nodeValue, short nodeType, NodeList childNodes, Node parentNode) {
            super(nodeName, nodeValue, nodeType, childNodes, parentNode);
            this.tagName = nodeName;
            this.tagBody = nodeValue;
            attributes = new HashMap<>();
        }

        @Override
        public String getTagName() {
            return tagName;
        }

        @Override
        public String getAttribute(String name) {
            return attributes.get(name);
        }

        @Override
        public void setAttribute(String name, String value) throws DOMException {
            attributes.put(name, value);
        }

        @Override
        public void removeAttribute(String name) throws DOMException {
            attributes.remove(name);
        }

        @Override
        public Attr getAttributeNode(String name) {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Attr setAttributeNode(Attr newAttr) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public NodeList getElementsByTagName(String name) {
            if (name != null) {
                final NodeList tempChildren = getChildNodes();
                final MockNodeList result = new MockNodeList();
                if (tempChildren != null) {
                    final int nodeListLength = tempChildren.getLength();
                    for (int index = 0; index < nodeListLength; index++) {
                        Node node = tempChildren.item(index);
                        if (node instanceof Element) {
                            Element element = (Element) node;
                            if (name.equals(element.getTagName())) {
                                result.addToNodeList(element);
                            }
                        }
                    }
                }
                return result;
            } else {
                return new MockNodeList((NodeList) null);
            }
        }

        @Override
        public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
            return null;  // Sufficient for Result processing
        }

        @Override
        public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public boolean hasAttribute(String name) {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public TypeInfo getSchemaTypeInfo() {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public void setIdAttribute(String name, boolean isId) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }

        @Override
        public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
            throw new UnsupportedOperationException("No mock support.");
        }
    }

}
