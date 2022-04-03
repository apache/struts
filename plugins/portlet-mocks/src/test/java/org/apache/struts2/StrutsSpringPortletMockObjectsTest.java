/*
 * Copyright 2020 Apache Software Foundation.
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
package org.apache.struts2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.portlet.ActionRequest;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.PreferencesValidator;
import javax.portlet.ReadOnlyException;
import javax.portlet.ResourceURL;
import javax.portlet.WindowState;
import javax.security.auth.Subject;
import javax.servlet.http.Cookie;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.apache.struts2.mock.web.portlet.MockActionRequest;
import org.apache.struts2.mock.web.portlet.MockActionResponse;
import org.apache.struts2.mock.web.portlet.MockCacheControl;
import org.apache.struts2.mock.web.portlet.MockClientDataRequest;
import org.apache.struts2.mock.web.portlet.MockEvent;
import org.apache.struts2.mock.web.portlet.MockEventRequest;
import org.apache.struts2.mock.web.portlet.MockEventResponse;
import org.apache.struts2.mock.web.portlet.MockMimeResponse;
import org.apache.struts2.mock.web.portlet.MockMultipartActionRequest;
import org.apache.struts2.mock.web.portlet.MockPortalContext;
import org.apache.struts2.mock.web.portlet.MockPortletConfig;
import org.apache.struts2.mock.web.portlet.MockPortletContext;
import org.apache.struts2.mock.web.portlet.MockPortletPreferences;
import org.apache.struts2.mock.web.portlet.MockPortletRequest;
import org.apache.struts2.mock.web.portlet.MockPortletRequestDispatcher;
import org.apache.struts2.mock.web.portlet.MockPortletResponse;
import org.apache.struts2.mock.web.portlet.MockPortletSession;
import org.apache.struts2.mock.web.portlet.MockPortletURL;
import org.apache.struts2.mock.web.portlet.MockRenderRequest;
import org.apache.struts2.mock.web.portlet.MockRenderResponse;
import org.apache.struts2.mock.web.portlet.MockResourceRequest;
import org.apache.struts2.mock.web.portlet.MockResourceResponse;
import org.apache.struts2.mock.web.portlet.MockResourceURL;
import org.apache.struts2.mock.web.portlet.MockStateAwareResponse;
import org.apache.struts2.mock.web.portlet.ServletWrappingPortletContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;
import org.w3c.dom.Element;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Basic test class for Portlet Mock Object testing
 * 
 */
public class StrutsSpringPortletMockObjectsTest extends TestCase {

    /**
     * An empty preferences validator for code coverage only
     */
    private static class BasicPreferencesValidator implements PreferencesValidator {

        @Override
        public void validate(PortletPreferences preferences) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    public void testMockActionRequest() {
        final String TEST_ACTIONNAME = "TEST_ACTIONNAME";

        // Call each constructor in sequence, do some basic checks
        new MockActionRequest();  // Only test is to confirm constructor completes without exception
        MockActionRequest mockActionRequest;
        mockActionRequest = new MockActionRequest(TEST_ACTIONNAME);
        assertEquals("Action name (ActionRequest.ACTION_NAME) " + TEST_ACTIONNAME + " not set ?", TEST_ACTIONNAME, mockActionRequest.getParameter(ActionRequest.ACTION_NAME));
        mockActionRequest = new MockActionRequest(PortletMode.VIEW);
        assertEquals("PortletMode.VIEW not set ?", PortletMode.VIEW, mockActionRequest.getPortletMode());

        final MockPortletContext mockPortletContext = new MockPortletContext();
        final MockPortalContext mockPortalContext = new MockPortalContext();
        new MockActionRequest(mockPortletContext);  // Only test is to confirm constructor completes without exception
        mockActionRequest = new MockActionRequest(mockPortalContext, mockPortletContext);
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockActionRequest.getPortalContext());
    }

    public void testMockActionResponse() {
        final String TEST_PARAM = "TEST_PARAM";
        final String TEST_PARAM2 = "TEST_PARAM2";
        final String TEST_PARAM3 = "TEST_PARAM3";
        final String TEST_PARAM_VALUE = "NORMALVALUE";
        final String[] TEST_PARAM_ARRAYVALUE = { "ARRAYVALUE1", "ARRAYVALUE2" };
        final String[] TEST_PARAM_ARRAYVALUE2 = { "ARRAYVALUE3", "ARRAYVALUE4" };
        final String TEST_REDIRECT_URL = "localhost:8080/fakeurl";
        final String TEST_RENDER_URLPARAM_NAME = "fake_render_url_param_name";

        // Call each constructor in sequence, do some basic checks
        MockActionResponse mockActionResponse = new MockActionResponse();
        try {
            mockActionResponse.setPortletMode(PortletMode.VIEW);
        } catch (Exception ex) {
            fail("MockActionResponse portlet mode set failed.  Exception: " + ex);
        }
        assertEquals("PortletMode.VIEW not set ?", PortletMode.VIEW, mockActionResponse.getPortletMode());

        final MockPortalContext mockPortalContext = new MockPortalContext();
        mockActionResponse = new MockActionResponse(mockPortalContext);  // Only test is to confirm constructor completes without exception
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockActionResponse.getPortalContext());
        mockActionResponse.setRenderParameter(TEST_PARAM, TEST_PARAM_VALUE);
        mockActionResponse.setRenderParameter(TEST_PARAM2, TEST_PARAM_ARRAYVALUE);
        assertEquals("Portal test render parameter value not as expected ?", TEST_PARAM_VALUE, mockActionResponse.getRenderParameter(TEST_PARAM));
        assertEquals("Portal test render parameter value not as expected ?", TEST_PARAM_ARRAYVALUE[0], mockActionResponse.getRenderParameter(TEST_PARAM2));
        final Map<String, String[]> renderParametersMap = new LinkedHashMap<>();
        renderParametersMap.put(TEST_PARAM3, TEST_PARAM_ARRAYVALUE2);
        mockActionResponse.setRenderParameters(renderParametersMap);
        assertNull("Portal test render parameter value not cleared as expected ?", mockActionResponse.getRenderParameter(TEST_PARAM));
        assertNull("Portal test render parameter value not cleared as expected ?", mockActionResponse.getRenderParameter(TEST_PARAM2));
        assertEquals("Portal test render parameter map value not as expected ?", renderParametersMap, mockActionResponse.getRenderParameterMap());

        try {
            mockActionResponse.sendRedirect(TEST_REDIRECT_URL);
            fail("Able to redirect after windowState/portletMode/renderParameters set ?");
        } catch (IllegalStateException ise) {
            // Expected failure
        } catch (Exception ex) {
            fail("MockActionResponse sendredirect failed.  Exception: " + ex);
        }
        mockActionResponse = new MockActionResponse();  // Clean mock response
        try {
            mockActionResponse.sendRedirect(TEST_REDIRECT_URL);
        } catch (Exception ex) {
            fail("MockActionResponse sendredirect failed.  Exception: " + ex);
        }
        assertEquals("Redirect url not as expected ?", TEST_REDIRECT_URL, mockActionResponse.getRedirectedUrl());
        mockActionResponse = new MockActionResponse();  // Clean mock response
        try {
            // Note: The original Spring 4.3.x mock implementation always threw an IllegalStateException when calling the method below.
            mockActionResponse.sendRedirect(TEST_REDIRECT_URL, TEST_RENDER_URLPARAM_NAME);
        } catch (Exception ex) {
            fail("MockActionResponse sendredirect failed.  Exception: " + ex);
        }
        assertEquals("Redirect url not as expected ?", TEST_REDIRECT_URL, mockActionResponse.getRedirectedUrl());
        assertEquals("Redirect url render parameter not as expected ?", TEST_REDIRECT_URL, mockActionResponse.getRenderParameter(TEST_RENDER_URLPARAM_NAME));
    }

    public void testMockCacheControl() {
        final int TEST_EXPIRATION_TIME = 30;
        final String TEST_ETAG = "TEST_ETAG";

        // Call each constructor in sequence, do some basic checks
        MockCacheControl mockCacheControl = new MockCacheControl();
        assertEquals("Initial expiry not as expected ?", 0, mockCacheControl.getExpirationTime());
        assertFalse("Initial scope public ?", mockCacheControl.isPublicScope());
        assertFalse("Initial use cache true ?", mockCacheControl.useCachedContent());
        mockCacheControl.setExpirationTime(TEST_EXPIRATION_TIME);
        assertEquals("Initial expiry not as expected ?", TEST_EXPIRATION_TIME, mockCacheControl.getExpirationTime());
        mockCacheControl.setPublicScope(true);
        assertTrue("Updated scope not public ?", mockCacheControl.isPublicScope());
        mockCacheControl.setPublicScope(false);
        assertFalse("Updated scope still public ?", mockCacheControl.isPublicScope());
        mockCacheControl.setETag(TEST_ETAG);
        assertEquals("ETag not not as expected ?", TEST_ETAG, mockCacheControl.getETag());
        mockCacheControl.setUseCachedContent(true);
        assertTrue("Updated use cache not true ?", mockCacheControl.useCachedContent());
        mockCacheControl.setUseCachedContent(false);
        assertFalse("Updated use cache still true ?", mockCacheControl.useCachedContent());
    }

    public void testMockClientDataRequest() {
        final String TEST_FAKECONTENT = "Some fake content for the test.";
        final byte[] TEST_FAKECONTENT_ASBYTES = TEST_FAKECONTENT.getBytes(StandardCharsets.UTF_8);
        final String TEST_CONTENT_TYPE = "text/html";
        final String TEST_METHOD = "POST";

        // Call each constructor in sequence, do some basic checks
        new MockClientDataRequest();  // Only test is to confirm constructor completes without exception
        final MockPortletContext mockPortletContext = new MockPortletContext();
        final MockPortalContext mockPortalContext = new MockPortalContext();
        new MockClientDataRequest(mockPortletContext);  // Only test is to confirm constructor completes without exception
        MockClientDataRequest mockClientDataRequest = new MockClientDataRequest(mockPortalContext, mockPortletContext);
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockClientDataRequest.getPortalContext());
        mockClientDataRequest.setContent(TEST_FAKECONTENT_ASBYTES);
        assertEquals("Content length not as expected ?", TEST_FAKECONTENT_ASBYTES.length, mockClientDataRequest.getContentLength());
        mockClientDataRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());
        assertEquals("Charset not as expected ?", StandardCharsets.UTF_8.name(), mockClientDataRequest.getCharacterEncoding());
        mockClientDataRequest.setContentType(TEST_CONTENT_TYPE);
        assertEquals("Content-type not as expected ?", TEST_CONTENT_TYPE, mockClientDataRequest.getContentType());
        mockClientDataRequest.setMethod(TEST_METHOD);
        assertEquals("Method not as expected ?", TEST_METHOD, mockClientDataRequest.getMethod());
        try ( InputStream inputStream = mockClientDataRequest.getPortletInputStream() ) {
            final byte[] readContent = new byte[mockClientDataRequest.getContentLength()];
            inputStream.read(readContent, 0, readContent.length);
            assertArrayEquals("Read byte array does not match original ?", TEST_FAKECONTENT_ASBYTES, readContent);
        } catch (Exception ex) {
            fail("MockClientDataRequest read inputstream failed.  Exception: " + ex);
        }
        try ( BufferedReader bufferedReader = mockClientDataRequest.getReader() ) {
            final String theOnlyLine = bufferedReader.readLine();
            assertEquals("Read line does not match original ?", TEST_FAKECONTENT, theOnlyLine);
        } catch (Exception ex) {
            fail("MockClientDataRequest read inputstream failed.  Exception: " + ex);
        }
    }

    public void testMockEvent() {
        final String TEST_EVENT = "TEST_EVENT";
        final String TEST_EVENT_VALUE = "TEST_EVENT_VALUE";
        final QName TEST_QNAME = new QName(TEST_EVENT);

        // Call each constructor in sequence, do some basic checks
        MockEvent mockClientDataRequest = new MockEvent(TEST_EVENT);
        assertNull("Initial value not null ?", mockClientDataRequest.getValue());
        assertEquals("Event name does not match ?", TEST_EVENT, mockClientDataRequest.getName());
        assertEquals("Event QName does not match ?", new QName(TEST_EVENT), mockClientDataRequest.getQName());
        mockClientDataRequest = new MockEvent(TEST_EVENT, TEST_EVENT_VALUE);
        assertEquals("Initial value not as expected ?", TEST_EVENT_VALUE, mockClientDataRequest.getValue());
        assertEquals("Event name does not match ?", TEST_EVENT, mockClientDataRequest.getName());
        assertEquals("Event QName does not match ?", new QName(TEST_EVENT), mockClientDataRequest.getQName());
        mockClientDataRequest = new MockEvent(TEST_QNAME);
        assertNull("Initial value not null ?", mockClientDataRequest.getValue());
        assertEquals("Event name does not match ?", TEST_EVENT, mockClientDataRequest.getName());
        assertEquals("Event QName does not match ?", TEST_QNAME, mockClientDataRequest.getQName());
        mockClientDataRequest = new MockEvent(TEST_QNAME, TEST_EVENT_VALUE);
        assertEquals("Initial value not as expected ?", TEST_EVENT_VALUE, mockClientDataRequest.getValue());
        assertEquals("Event name does not match ?", TEST_EVENT, mockClientDataRequest.getName());
        assertEquals("Event QName does not match ?", TEST_QNAME, mockClientDataRequest.getQName());
    }

    public void testMockEventRequest() {
        final MockEvent TEST_MOCKEVENT = new MockEvent("MockEventName", "MockEventValue");
        final String TEST_METHOD = "POST";

        // Call each constructor in sequence, do some basic checks
        MockEventRequest mockEventRequest = new MockEventRequest(TEST_MOCKEVENT);
        assertEquals("Event not the event set ?", TEST_MOCKEVENT, mockEventRequest.getEvent());
        mockEventRequest.setMethod(TEST_METHOD);
        assertEquals("Method not the method set ?", TEST_METHOD, mockEventRequest.getMethod());
        final MockPortletContext mockPortletContext = new MockPortletContext();
        final MockPortalContext mockPortalContext = new MockPortalContext();
        new MockEventRequest(TEST_MOCKEVENT, mockPortletContext);  // Only test is to confirm constructor completes without exception
        mockEventRequest = new MockEventRequest(TEST_MOCKEVENT, mockPortalContext, mockPortletContext);  // Only test is to confirm constructor completes without exception
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockEventRequest.getPortalContext());
    }

    public void testMockEventResponse() {
        // Call each constructor in sequence, do some basic checks
        try {
            new MockEventResponse();  // Only test is to confirm constructor completes without exception
            assertTrue(true);
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }

    public void testMockMimeResponse() {
        final String TEST_CONTENT_TYPE = "text/html";
        final int TEST_BUFFERSIZE = 8192;
        final String TEST_INCLUDED_URL = "localhost:8080/fakeincludedurl";
        final String TEST_FORWARDED_URL = "localhost:8080/fakeforwardedurl";
        final String TEST_FAKECONTENT = "Some fake content for the test.";
        final byte[] TEST_FAKECONTENT_ASBYTES = TEST_FAKECONTENT.getBytes(StandardCharsets.UTF_8);

        // Call each constructor in sequence, do some basic checks
        MockMimeResponse mockMimeResponse = new MockMimeResponse();
        assertEquals("Default character encoding not as expected ?", WebUtils.DEFAULT_CHARACTER_ENCODING, mockMimeResponse.getCharacterEncoding());
        assertEquals("Default locale not as expected ?", Locale.getDefault(), mockMimeResponse.getLocale());
        assertTrue("Default buffersize not as expected ?", mockMimeResponse.getBufferSize() >= 4096);
        assertFalse("Mock response already committed ?", mockMimeResponse.isCommitted());
        final MockPortalContext mockPortalContext = new MockPortalContext();
        final MockPortletContext mockPortletContext = new MockPortletContext();
        final MockPortletRequest mockPortletRequest = new MockPortletRequest(mockPortalContext, mockPortletContext);
        mockMimeResponse = new MockMimeResponse(mockPortalContext);
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockMimeResponse.getPortalContext());
        mockMimeResponse = new MockMimeResponse(mockPortalContext, mockPortletRequest);
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockMimeResponse.getPortalContext());
        mockMimeResponse.setContentType(TEST_CONTENT_TYPE);
        assertEquals("Content-type not as expected ?", TEST_CONTENT_TYPE, mockMimeResponse.getContentType());
        mockMimeResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        assertEquals("Charset not as expected ?", StandardCharsets.UTF_8.name(), mockMimeResponse.getCharacterEncoding());
        mockMimeResponse.setBufferSize(TEST_BUFFERSIZE);
        assertEquals("Buffersize not as expected ?", TEST_BUFFERSIZE, mockMimeResponse.getBufferSize());
        mockMimeResponse.setCommitted(true);
        assertTrue("Response not committed ?", mockMimeResponse.isCommitted());
        mockMimeResponse.setCommitted(false);
        assertFalse("Response still committed ?", mockMimeResponse.isCommitted());
        mockMimeResponse.setIncludedUrl(TEST_INCLUDED_URL);
        assertEquals("Included URL not as expected ?", TEST_INCLUDED_URL, mockMimeResponse.getIncludedUrl());
        mockMimeResponse.setForwardedUrl(TEST_FORWARDED_URL);
        assertEquals("Forwarded URL not as expected ?", TEST_FORWARDED_URL, mockMimeResponse.getForwardedUrl());
        try {
            assertNotNull("OutputStream null ?", mockMimeResponse.getPortletOutputStream());
        } catch (Exception ex) {
            fail("MockMimeResponse get outputstream failed.  Exception: " + ex);
        }
        PortletURL portletUrl = mockMimeResponse.createRenderURL();
        assertNotNull("RenderURL null ?", portletUrl);
        portletUrl = mockMimeResponse.createActionURL();
        assertNotNull("ActionURL null ?", portletUrl);
        ResourceURL resourceURL = mockMimeResponse.createResourceURL();
        assertNotNull("ResourceURL null ?", resourceURL);
        assertNotNull("CacheControl null ?", mockMimeResponse.getCacheControl());

        try {
            OutputStream outputStream = mockMimeResponse.getPortletOutputStream();
            assertNotNull("OutputStream null ?", outputStream);
            outputStream.write(TEST_FAKECONTENT_ASBYTES);
            final byte[] writeResult = mockMimeResponse.getContentAsByteArray();
            assertArrayEquals("Written byte array does not match original ?", TEST_FAKECONTENT_ASBYTES, writeResult);
            assertTrue("Buffer not committed after processing ?", mockMimeResponse.isCommitted());
        } catch (Exception ex) {
            fail("MockMimeResponse get/process outputstream failed.  Exception: " + ex);
        }
        try {
            mockMimeResponse.resetBuffer();
        } catch (IllegalStateException ise) {
            // Expected failure
        }

        mockMimeResponse.setCommitted(false);
        mockMimeResponse.resetBuffer();
        try {
            PrintWriter printWriter = mockMimeResponse.getWriter();
            assertNotNull("PrintWriter null ?", printWriter);
            printWriter.print(TEST_FAKECONTENT);
            mockMimeResponse.flushBuffer();
            final byte[] writeResult = mockMimeResponse.getContentAsByteArray();
            assertArrayEquals("Written byte array does not match original ?", TEST_FAKECONTENT_ASBYTES, writeResult);
            assertTrue("Buffer not committed after processing ?", mockMimeResponse.isCommitted());
            final String writeResultAsString = mockMimeResponse.getContentAsString();
            assertEquals("Written result does not match original ?", TEST_FAKECONTENT, writeResultAsString);
        } catch (Exception ex) {
            fail("MockMimeResponse get/process printwriter failed.  Exception: " + ex);
        }
        try {
            mockMimeResponse.reset();
        } catch (IllegalStateException ise) {
            // Expected failure
        }

        mockMimeResponse.setCommitted(false);
        mockMimeResponse.reset();
        assertNull("After reset, character encoding not null ?", mockMimeResponse.getCharacterEncoding());
        assertNull("After reset, content-type not null ?", mockMimeResponse.getContentType());
        assertNull("After reset, locale not null ?", mockMimeResponse.getLocale());
    }

    public void testMockMultipartActionRequest() {
        final String TEST_FILENAME = "TEST_TEMPFILE";
        final String TEST_FILENAME2 = "TEST_TEMPFILE2";
        final String TEST_CONTENT_TYPE = "text/html";
        final String TEST_FAKECONTENT = "Some fake content for the test.";
        final byte[] TEST_FAKECONTENT_ASBYTES = TEST_FAKECONTENT.getBytes(StandardCharsets.UTF_8);

        // Call each constructor in sequence, do some basic checks
        MockMultipartActionRequest mockMultipartActionRequest = new MockMultipartActionRequest();
        assertFalse("Initial multipartrequest has file names ?", mockMultipartActionRequest.getFileNames().hasNext());
        assertTrue("Initial multipartrequest has nonempty map ?", mockMultipartActionRequest.getFileMap().isEmpty());
        assertTrue("Initial multipartrequest has nonempty multivalue map ?", mockMultipartActionRequest.getMultiFileMap().isEmpty());
        final MultipartFile multipartFile = new TestMockMultipartFile(TEST_FILENAME, TEST_FILENAME, TEST_CONTENT_TYPE, TEST_FAKECONTENT_ASBYTES);
        final MultipartFile multipartFile2 = new TestMockMultipartFile(TEST_FILENAME2, TEST_FILENAME2, TEST_CONTENT_TYPE, TEST_FAKECONTENT_ASBYTES);
        mockMultipartActionRequest.addFile(multipartFile);
        assertTrue("Multipartrequest does not have file names ?", mockMultipartActionRequest.getFileNames().hasNext());
        assertFalse("Multipartrequest does not have nonempty map ?", mockMultipartActionRequest.getFileMap().isEmpty());
        assertFalse("Multipartrequest does not have nonempty multivalue map ?", mockMultipartActionRequest.getMultiFileMap().isEmpty());
        mockMultipartActionRequest.addFile(multipartFile2);
        assertTrue("Multipartrequest does not have file names ?", mockMultipartActionRequest.getFileNames().hasNext());
        assertFalse("Multipartrequest does not have nonempty map ?", mockMultipartActionRequest.getFileMap().isEmpty());
        assertFalse("Multipartrequest does not have nonempty multivalue map ?", mockMultipartActionRequest.getMultiFileMap().isEmpty());
        assertEquals("Retrieved file 1 not as expected ?", multipartFile, mockMultipartActionRequest.getFile(TEST_FILENAME));
        assertEquals("Retrieved file 1 content-type not as expected ?", TEST_CONTENT_TYPE, mockMultipartActionRequest.getMultipartContentType(TEST_FILENAME));
        assertEquals("Retrieved file 2 not as expected ?", multipartFile2, mockMultipartActionRequest.getFile(TEST_FILENAME2));
        assertEquals("Retrieved file 2 content-type not as expected ?", TEST_CONTENT_TYPE, mockMultipartActionRequest.getMultipartContentType(TEST_FILENAME2));
        final Map<String, MultipartFile> fileMap = mockMultipartActionRequest.getFileMap();
        assertNotNull("File map is null ?", fileMap);
        assertTrue("File 1 name not in map ?", fileMap.containsKey(TEST_FILENAME));
        assertTrue("File 2 name not in map ?", fileMap.containsKey(TEST_FILENAME2));
        assertTrue("File 1 not in map ?", fileMap.containsValue(multipartFile));
        assertTrue("File 2 not in map ?", fileMap.containsValue(multipartFile2));
        MultiValueMap<String, MultipartFile> fileMultiValueMap = mockMultipartActionRequest.getMultiFileMap();
        assertNotNull("MultiValue file map is null ?", fileMultiValueMap);
        MultipartFile retrievedFile = fileMultiValueMap.getFirst(TEST_FILENAME);
        assertNotNull("File 1 1st value is null ?", retrievedFile);
        assertEquals("File 1 not in multivalue map ?", multipartFile, retrievedFile);
        retrievedFile = fileMultiValueMap.getFirst(TEST_FILENAME2);
        assertNotNull("File 2 1st value is null ?", retrievedFile);
        assertEquals("File 2 not in multivalue map ?", multipartFile2, retrievedFile);
    }

    public void testMockPortalContext() {
        final String TEST_PROPERTY = "TEST_PROPERTY";
        final String TEST_PROPERTY_VALUE = "Property_Value_1";
        final String TEST_PROPERTY2 = "TEST_PROPERTY2";
        final String TEST_PROPERTY2_VALUE = "Property_Value_2";

        // Call each constructor in sequence, do some basic checks
        final MockPortalContext mockPortalContext = new MockPortalContext();
        assertNotNull("MockPortalContext portalInfo null ?", mockPortalContext.getPortalInfo());
        assertNotNull("MockPortalContext property names null ?", mockPortalContext.getPropertyNames());
        assertFalse("MockPortalConext initial propertyNames not empty ?", mockPortalContext.getPropertyNames().hasMoreElements());
        final Enumeration<PortletMode> supportedPortletModes = mockPortalContext.getSupportedPortletModes();
        assertNotNull("MockPortalContext supported portlet modes null ?", supportedPortletModes);
        final ArrayList<PortletMode> portletModeList = new ArrayList<>();
        while (supportedPortletModes.hasMoreElements()) {
            portletModeList.add(supportedPortletModes.nextElement());
        }
        assertEquals("MockPortalContext does not support three modes ?", 3, portletModeList.size());
        assertTrue("MockPortalContext does not contain PortletMode.VIEW ?", portletModeList.contains(PortletMode.VIEW));
        assertTrue("MockPortalContext does not contain PortletMode.EDIT ?", portletModeList.contains(PortletMode.EDIT));
        assertTrue("MockPortalContext does not contain PortletMode.HELP ?", portletModeList.contains(PortletMode.HELP));

        final Enumeration<WindowState> supportedWindowStates = mockPortalContext.getSupportedWindowStates();
        assertNotNull("MockPortalContext supported window modes null ?", supportedWindowStates);
        final ArrayList<WindowState> windowStateList = new ArrayList<>();
        while (supportedWindowStates.hasMoreElements()) {
            windowStateList.add(supportedWindowStates.nextElement());
        }
        assertEquals("MockPortalContext does not support three states ?", 3, windowStateList.size());
        assertTrue("MockPortalContext does not contain WindowState.NORMAL ?", windowStateList.contains(WindowState.NORMAL));
        assertTrue("MockPortalContext does not contain WindowState.MAXIMIZED ?", windowStateList.contains(WindowState.MAXIMIZED));
        assertTrue("MockPortalContext does not contain WindowState.MINIMIZED ?", windowStateList.contains(WindowState.MINIMIZED));

        mockPortalContext.setProperty(TEST_PROPERTY, TEST_PROPERTY_VALUE);
        mockPortalContext.setProperty(TEST_PROPERTY2, TEST_PROPERTY2_VALUE);
        final Enumeration<String> propertyNames = mockPortalContext.getPropertyNames();
        assertNotNull("MockPortalContext property names null ?", propertyNames);
        final ArrayList<String> propertyNamesList = new ArrayList<>();
        while (propertyNames.hasMoreElements()) {
            propertyNamesList.add(propertyNames.nextElement());
        }
        assertEquals("MockPortalContext does not contain two properties ?", 2, propertyNamesList.size());
        assertTrue("MockPortalContext does not contain property name " + TEST_PROPERTY + " ?", propertyNamesList.contains(TEST_PROPERTY));
        assertTrue("MockPortalContext does not contain property name " + TEST_PROPERTY2 + " ?", propertyNamesList.contains(TEST_PROPERTY2));
        assertEquals("MockPortalContext value of property name " + TEST_PROPERTY + " not as expected ?", TEST_PROPERTY_VALUE, mockPortalContext.getProperty(TEST_PROPERTY));
        assertEquals("MockPortalContext value of property name " + TEST_PROPERTY2 + " not as expected ?", TEST_PROPERTY2_VALUE, mockPortalContext.getProperty(TEST_PROPERTY2));
    }

    public void testMockPortletConfig() {
        final String TEST_PORTLETNAME = "TestPortletName";
        final String TEST_INITPARAM = "TEST_INITPARAM";
        final String TEST_INITPARAM_VALUE = "Init_Value_1";
        final String TEST_INITPARAM2= "TEST_INITPARAM2";
        final String TEST_INITPARAM_VALUE2 = "Init_Value_2";
        final String TEST_RENDERPARAM_NAME = "Test_RenderParam_1";
        final String TEST_RENDERPARAM_NAME2 = "Test_RenderParam_2";
        final String TEST_DEFAULTNAMESPACE = "Test_Default_Namespace";
        final QName TEST_QNAME = new QName(TEST_PORTLETNAME);
        final QName TEST_QNAME2 = new QName(TEST_DEFAULTNAMESPACE);
        final String TEST_RUNTIME_OPTION = "TEST_RUNTIME_OPTION";
        final String TEST_RUNTIME_OPTION2 = "TEST_RUNTIME_OPTION2";
        final String TEST_RUNTIME_VALUE1 = "Test_Runtime_Option1";
        final String TEST_RUNTIME_VALUE2 = "Test_Runtime_Option2";

        // Call each constructor in sequence, do some basic checks
        MockPortletConfig mockPortletConfig = new MockPortletConfig();
        assertNotNull("MockPortletConfig default PortletContext is null ?", mockPortletConfig.getPortletContext());
        assertEquals("MockPortletConfig default name not empty string ?", "", mockPortletConfig.getPortletName());
        mockPortletConfig = new MockPortletConfig(TEST_PORTLETNAME);
        assertNotNull("MockPortletConfig default PortletContext is null ?", mockPortletConfig.getPortletContext());
        assertEquals("MockPortletConfig name not constructor set value ?", TEST_PORTLETNAME, mockPortletConfig.getPortletName());
        final MockPortletContext mockPortletContext = new MockPortletContext();
        mockPortletConfig = new MockPortletConfig(mockPortletContext, TEST_PORTLETNAME);
        assertEquals("MockPortletConfig PortletContext constructor set value ?", mockPortletContext, mockPortletConfig.getPortletContext());
        assertEquals("MockPortletConfig name not constructor set value ?", TEST_PORTLETNAME, mockPortletConfig.getPortletName());
        assertNull("MockPortletConfig default locale resource bundle not null ?", mockPortletConfig.getResourceBundle(Locale.getDefault()));
        final ResourceBundle resourceBundle = new TetMockResourceBundle();
        mockPortletConfig.setResourceBundle(Locale.getDefault(), resourceBundle);
        assertEquals("Default locale resource bundle not set value ?", resourceBundle, mockPortletConfig.getResourceBundle(Locale.getDefault()));

        assertNull(TEST_INITPARAM + " present before set ?", mockPortletConfig.getInitParameter(TEST_INITPARAM));
        assertNull(TEST_INITPARAM2 + " present before set ?", mockPortletConfig.getInitParameter(TEST_INITPARAM));
        mockPortletConfig.addInitParameter(TEST_INITPARAM, TEST_INITPARAM_VALUE);
        mockPortletConfig.addInitParameter(TEST_INITPARAM2, TEST_INITPARAM_VALUE2);
        assertEquals("Retrieved init parameter 1 does not match set value ?", TEST_INITPARAM_VALUE, mockPortletConfig.getInitParameter(TEST_INITPARAM));
        assertEquals("Retrieved init parameter 2 does not match set value ?", TEST_INITPARAM_VALUE2, mockPortletConfig.getInitParameter(TEST_INITPARAM2));
        final Enumeration<String> initParameterNames = mockPortletConfig.getInitParameterNames();
        int initParameterNameCount = 0;
        assertNotNull("InitParameter names null after additions ?", initParameterNames);
        assertTrue("InitParameter names empty after additions ?", initParameterNames.hasMoreElements());
        while (initParameterNames.hasMoreElements()) {
            initParameterNameCount++;
            String currentInitParameterName = initParameterNames.nextElement();
            assertTrue("Initparam name not one of two expected matches ?", TEST_INITPARAM.equals(currentInitParameterName) || 
                    TEST_INITPARAM2.equals(currentInitParameterName));
        }
        assertEquals("InitParameter names size not 2 ?", 2, initParameterNameCount);

        assertFalse("Initial renderparam names not empty ?", mockPortletConfig.getPublicRenderParameterNames().hasMoreElements());
        mockPortletConfig.addPublicRenderParameterName(TEST_RENDERPARAM_NAME);
        mockPortletConfig.addPublicRenderParameterName(TEST_RENDERPARAM_NAME2);
        final Enumeration<String> publicRenderParameterNames = mockPortletConfig.getPublicRenderParameterNames();
        int publicRenderParameterNameCount = 0;
        assertNotNull("PublicRenderParameter names null after additions ?", publicRenderParameterNames);
        assertTrue("PublicRenderParameter names empty after additions ?", publicRenderParameterNames.hasMoreElements());
        while (publicRenderParameterNames.hasMoreElements()) {
            publicRenderParameterNameCount++;
            String currentPublicRenderParameterName = publicRenderParameterNames.nextElement();
            assertTrue("PublicRenderParameter name not one of two expected matches ?", TEST_RENDERPARAM_NAME.equals(currentPublicRenderParameterName) || 
                    TEST_RENDERPARAM_NAME2.equals(currentPublicRenderParameterName));
        }
        assertEquals("PublicRenderParameter names size not 2 ?", 2, publicRenderParameterNameCount);
        mockPortletConfig.setDefaultNamespace(TEST_DEFAULTNAMESPACE);
        assertEquals("Default namespace not set value ?", TEST_DEFAULTNAMESPACE, mockPortletConfig.getDefaultNamespace());

        assertFalse("Initial publishing event QNames not empty ?", mockPortletConfig.getPublishingEventQNames().hasMoreElements());
        assertFalse("Initial processing event QNames not empty ?", mockPortletConfig.getProcessingEventQNames().hasMoreElements());
        mockPortletConfig.addPublishingEventQName(TEST_QNAME);
        mockPortletConfig.addProcessingEventQName(TEST_QNAME2);
        final Enumeration<QName> publishingEventQNames = mockPortletConfig.getPublishingEventQNames();
        assertNotNull("PublishingEventQNames names null after additions ?", publishingEventQNames);
        assertTrue("PublishingEventQNames names empty after additions ?", publishingEventQNames.hasMoreElements());
        final QName firstPublishingEventQName = publishingEventQNames.nextElement();
        assertEquals("First publishing event QName not set value ?", TEST_QNAME, firstPublishingEventQName);
        final Enumeration<QName> processingEventQNames = mockPortletConfig.getProcessingEventQNames();
        assertNotNull("ProcessingEventQNames names null after additions ?", processingEventQNames);
        assertTrue("ProcessingEventQNames names empty after additions ?", processingEventQNames.hasMoreElements());
        final QName firstProcessingEventQName = processingEventQNames.nextElement();
        assertEquals("First processing event QName not set value ?", TEST_QNAME2, firstProcessingEventQName);

        assertFalse("Initial supported locales not empty ?", mockPortletConfig.getSupportedLocales().hasMoreElements());
        mockPortletConfig.addSupportedLocale(Locale.getDefault());
        final Enumeration<Locale> supportedLocales = mockPortletConfig.getSupportedLocales();
        assertNotNull("Supported locales null after additions ?", supportedLocales);
        assertTrue("Supported locales names empty after additions ?", supportedLocales.hasMoreElements());
        final Locale firstSupportedLocale = supportedLocales.nextElement();
        assertEquals("First supported locale not set value ?", Locale.getDefault(), firstSupportedLocale);

        assertTrue("Portlet runtime options not empty before set ?", mockPortletConfig.getContainerRuntimeOptions().isEmpty());
        mockPortletConfig.addContainerRuntimeOption(TEST_RUNTIME_OPTION, TEST_RUNTIME_VALUE1);
        mockPortletConfig.addContainerRuntimeOption(TEST_RUNTIME_OPTION2, TEST_RUNTIME_VALUE2);
        final Map<String, String[]>  containerRuntimeOptions = mockPortletConfig.getContainerRuntimeOptions();
        assertNotNull("Runtime options null after additions ?", containerRuntimeOptions);
        assertFalse("Runtime options empty after additions ?", containerRuntimeOptions.isEmpty());
        assertTrue("Runtime option 1 not present after setting ?", containerRuntimeOptions.containsKey(TEST_RUNTIME_OPTION));
        assertTrue("Runtime option 2 not present after setting ?", containerRuntimeOptions.containsKey(TEST_RUNTIME_OPTION2));
        assertEquals("Runtime option 1 not equal to set value ?", TEST_RUNTIME_VALUE1, containerRuntimeOptions.get(TEST_RUNTIME_OPTION)[0]);
        assertEquals("Runtime option 2 not equal to set value ?", TEST_RUNTIME_VALUE2, containerRuntimeOptions.get(TEST_RUNTIME_OPTION2)[0]);
    }

    public void testMockPortletContext() {
        final String TEST_ATTRIBUTE = "TEST_ATTRIBUTE";
        final String TEST_ATTRIBUTE_VALUE = "Attribute_Value_1";
        final String TEST_ATTRIBUTE2 = "TEST_ATTRIBUTE2";
        final String TEST_ATTRIBUTE_VALUE2 = "Attribute_Value_2";
        final String TEST_INITPARAM = "TEST_INITPARAM";
        final String TEST_INITPARAM_VALUE = "Init_Value_1";
        final String TEST_INITPARAM2 = "TEST_INITPARAM2";
        final String TEST_INITPARAM_VALUE2 = "Init_Value_2";
        final String TEST_CONTEXT_NAME = "TEST_CONTEXT_NAME";
        final String TEST_RUNTIME_OPTION = "TEST_RUNTIME_OPTION";
        final String TEST_RUNTIME_OPTION2 = "TEST_RUNTIME_OPTION2";

        // Call each constructor in sequence, do some basic checks
        final MockPortletContext mockPortletContext = new MockPortletContext();
        assertNotNull("MockPortletContext serverInfo null ?", mockPortletContext.getServerInfo());
        try {
            assertNotNull("MockPortletContext PortletRequestDispatcher null ?", mockPortletContext.getRequestDispatcher("IllegalPrefix"));
            fail("PortletRequestDispatcher path must start with /");
        } catch (IllegalArgumentException iae) {
            // Expected exception
        }
        assertNotNull("MockPortletContext PortletRequestDispatcher for / null ?", mockPortletContext.getRequestDispatcher("/"));
        assertNull("MockPortletContext named PortletRequestDispatcher not null ?", mockPortletContext.getNamedDispatcher("SomeName"));
        assertNotNull("MockPortletContext resource stream for / null ?", mockPortletContext.getResourceAsStream("/"));
        assertNull("MockPortletContext resource stream for /ThisDoesNotExist not null ?", mockPortletContext.getResourceAsStream("/ThisDoesNotExist"));
        assertTrue("MockPortletContext major version not >= 2 ?", mockPortletContext.getMajorVersion() >= 2);
        assertTrue("MockPortletContext minor version not >= 0 ?", mockPortletContext.getMajorVersion() >= 0);
        assertNull("MockPortletContext MIME type for / not null ?", mockPortletContext.getMimeType("/"));
        assertNotNull("MockPortletContext real path for / null ?", mockPortletContext.getRealPath("/"));
        assertNull("MockPortletContext real path for /ThisDoesNotExist not null ?", mockPortletContext.getRealPath("/ThisDoesNotExist"));
        assertNotNull("MockPortletContext resource paths for / null ?", mockPortletContext.getResourcePaths("/"));
        assertNull("MockPortletContext resource paths for / null ?", mockPortletContext.getResourcePaths("/ThisDoesNotExist"));
        try {
            assertNotNull("MockPortletContext resource URL for / null ?", mockPortletContext.getResource("/"));
        } catch (MalformedURLException mue) {
            fail("MockPortletContext resource URL for / failed.  Exception: " + mue);
        }
        try {
            assertNull("MockPortletContext resource URL for /ThisDoesNotExist not null ?", mockPortletContext.getResource("/ThisDoesNotExist"));
        } catch (MalformedURLException mue) {
            fail("MockPortletContext resource URL for /ThisDoesNotExist failed.  Exception: " + mue);
        }

        assertNull(TEST_ATTRIBUTE + " present before set ?", mockPortletContext.getAttribute(TEST_ATTRIBUTE));
        assertNull(TEST_ATTRIBUTE2 + " present before set ?", mockPortletContext.getAttribute(TEST_ATTRIBUTE2));
        mockPortletContext.setAttribute(TEST_ATTRIBUTE, TEST_ATTRIBUTE_VALUE);
        mockPortletContext.setAttribute(TEST_ATTRIBUTE2, TEST_ATTRIBUTE_VALUE2);
        assertEquals("Retrieved atribute 1 does not match set value ?", TEST_ATTRIBUTE_VALUE, mockPortletContext.getAttribute(TEST_ATTRIBUTE));
        assertEquals("Retrieved atribute 2 does not match set value ?", TEST_ATTRIBUTE_VALUE2, mockPortletContext.getAttribute(TEST_ATTRIBUTE2));
        final Enumeration<String> attributeNames = mockPortletContext.getAttributeNames();
        int attributeNameCount = 0;
        assertNotNull("Attribute names null after additions ?", attributeNames);
        assertTrue("Attribute names empty after additions ?", attributeNames.hasMoreElements());
        while (attributeNames.hasMoreElements()) {
            attributeNameCount++;
            String currentAttibuteName = attributeNames.nextElement();
            assertTrue("Attribute name not one of two or three expected matches ?", TEST_ATTRIBUTE.equals(currentAttibuteName) || 
                    TEST_ATTRIBUTE2.equals(currentAttibuteName) ||
                    WebUtils.TEMP_DIR_CONTEXT_ATTRIBUTE.equals(currentAttibuteName));
        }
        assertTrue("Atribute names size not 2 or 3 ?", attributeNameCount >= 2 && attributeNameCount <= 3);

        assertNull(TEST_INITPARAM + " present before set ?", mockPortletContext.getInitParameter(TEST_INITPARAM));
        assertNull(TEST_INITPARAM2 + " present before set ?", mockPortletContext.getInitParameter(TEST_INITPARAM));
        mockPortletContext.addInitParameter(TEST_INITPARAM, TEST_INITPARAM_VALUE);
        mockPortletContext.addInitParameter(TEST_INITPARAM2, TEST_INITPARAM_VALUE2);
        assertEquals("Retrieved init parameter 1 does not match set value ?", TEST_INITPARAM_VALUE, mockPortletContext.getInitParameter(TEST_INITPARAM));
        assertEquals("Retrieved init parameter 2 does not match set value ?", TEST_INITPARAM_VALUE2, mockPortletContext.getInitParameter(TEST_INITPARAM2));
        final Enumeration<String> initParameterNames = mockPortletContext.getInitParameterNames();
        int initParameterNameCount = 0;
        assertNotNull("InitParameter names null after additions ?", initParameterNames);
        assertTrue("InitParameter names empty after additions ?", initParameterNames.hasMoreElements());
        while (initParameterNames.hasMoreElements()) {
            initParameterNameCount++;
            String currentInitParameterName = initParameterNames.nextElement();
            assertTrue("Initparam name not one of two expected matches ?", TEST_INITPARAM.equals(currentInitParameterName) || 
                    TEST_INITPARAM2.equals(currentInitParameterName));
        }
        assertEquals("InitParameter names size not 2 ?", 2, initParameterNameCount);

        mockPortletContext.log("Test logging call");
        mockPortletContext.log("Test logging call", new Exception("Fake Exception"));

        assertEquals("Default portlet context name not as expected ?", "MockPortletContext", mockPortletContext.getPortletContextName());
        mockPortletContext.setPortletContextName(TEST_CONTEXT_NAME);
        assertEquals("Portlet context name not equal to set value ?", TEST_CONTEXT_NAME, mockPortletContext.getPortletContextName());

        assertFalse("Portlet runtime options not empty before set ?", mockPortletContext.getContainerRuntimeOptions().hasMoreElements());
        mockPortletContext.addContainerRuntimeOption(TEST_RUNTIME_OPTION);
        mockPortletContext.addContainerRuntimeOption(TEST_RUNTIME_OPTION2);
        final Enumeration<String> containerRuntimeOptions = mockPortletContext.getContainerRuntimeOptions();
        int containerRuntimeOptionsCount = 0;
        assertNotNull("Runtime options null after additions ?", containerRuntimeOptions);
        assertTrue("Runtime options empty after additions ?", containerRuntimeOptions.hasMoreElements());
        while (containerRuntimeOptions.hasMoreElements()) {
            containerRuntimeOptionsCount++;
            String currentRuntimeOption = containerRuntimeOptions.nextElement();
            assertTrue("Runtime option not one of two expected matches ?", TEST_RUNTIME_OPTION.equals(currentRuntimeOption) || 
                    TEST_RUNTIME_OPTION2.equals(currentRuntimeOption));
        }
        assertEquals("Runtime options size not 2 ?", 2, containerRuntimeOptionsCount);
    }

    public void testMockPortletPreferences() {
        final String PORTLET_READONLY = "PORTLET_READONLY";
        final String PORTLET_READONLY_VALUE = "Readonly_Value_1";
        final String PORTLET_READONLY2 = "PORTLET_READONLY2";
        final String PORTLET_READONLY2_VALUE = "Readonly_Value_2";
        final String PORTLET_WRITEABLE = "PORTLET_WRITEABLE";
        final String PORTLET_WRITEABLE_VALUE = "Writeable_Value_1";
        final String PORTLET_WRITEABLE2 = "PORTLET_WRITEABLE2";
        final String PORTLET_WRITEABLE2_VALUE = "Writeable_Value_2";
        final String PORTLET_WRITEABLE_ARRAY = "PORTLET_WRITEABLE_ARRAY";
        final String[] PORTLET_WRITEABLE_ARRAY_VALUE = { "Writeable_Array_Value_1", "Writeable_Array_Value" };

        // Call each constructor in sequence, do some basic checks
        final MockPortletPreferences mockPortletPreferences = new MockPortletPreferences();
        assertFalse("MockPortletPreferences initial preferences names non-empty ?", mockPortletPreferences.getNames().hasMoreElements());
        assertTrue("MockPortletPreferences initial preferences non-empty ?", mockPortletPreferences.getMap().isEmpty());

        // Test set readonly permitted before key exists
        assertFalse(PORTLET_READONLY + " readonly before set ?", mockPortletPreferences.isReadOnly(PORTLET_READONLY));
        assertFalse(PORTLET_READONLY2 + " readonly before set ?", mockPortletPreferences.isReadOnly(PORTLET_READONLY2));
        mockPortletPreferences.setReadOnly(PORTLET_READONLY, true);
        mockPortletPreferences.setReadOnly(PORTLET_READONLY2, true);
        assertTrue(PORTLET_READONLY + " not readonly after set ?", mockPortletPreferences.isReadOnly(PORTLET_READONLY));
        assertTrue(PORTLET_READONLY2 + " not readonly after set ?", mockPortletPreferences.isReadOnly(PORTLET_READONLY2));
        mockPortletPreferences.setReadOnly(PORTLET_READONLY, false);
        mockPortletPreferences.setReadOnly(PORTLET_READONLY2, false);
        assertFalse(PORTLET_READONLY + " readonly after clear ?", mockPortletPreferences.isReadOnly(PORTLET_READONLY));
        assertFalse(PORTLET_READONLY2 + " readonly after clear ?", mockPortletPreferences.isReadOnly(PORTLET_READONLY2));

        try {
            // Test initial set and replace
            mockPortletPreferences.setValue(PORTLET_READONLY, PORTLET_READONLY_VALUE);
            assertEquals("Set value mismatch ?", PORTLET_READONLY_VALUE, mockPortletPreferences.getValue(PORTLET_READONLY, null));
            mockPortletPreferences.setValue(PORTLET_READONLY, PORTLET_READONLY_VALUE);
            assertEquals("Set value mismatch ?", PORTLET_READONLY_VALUE, mockPortletPreferences.getValue(PORTLET_READONLY, null));
        } catch (Exception ex) {
            fail("Setvalue failed unexpectedly: " + ex);
        }
        try {
            // Test initial set and replace
            mockPortletPreferences.setValue(PORTLET_READONLY2, PORTLET_READONLY2_VALUE);
            assertEquals("Set value mismatch ?", PORTLET_READONLY2_VALUE, mockPortletPreferences.getValue(PORTLET_READONLY2, null));
            mockPortletPreferences.setValue(PORTLET_READONLY2, PORTLET_READONLY2_VALUE);
            assertEquals("Set value mismatch ?", PORTLET_READONLY2_VALUE, mockPortletPreferences.getValue(PORTLET_READONLY2, null));
        } catch (Exception ex) {
            fail("Setvalue failed unexpectedly: " + ex);
        }

        mockPortletPreferences.setReadOnly(PORTLET_READONLY, true);
        mockPortletPreferences.setReadOnly(PORTLET_READONLY2, true);
        try {
            // Test set of readonly value fails
            mockPortletPreferences.setValue(PORTLET_READONLY, PORTLET_READONLY_VALUE);
            fail("Setvalue worked unexpectedly for readonly value");
        } catch (Exception ex) {
            assertTrue("Unexpected exception type failure ?  Ex: " + ex, ex instanceof ReadOnlyException);
            assertTrue(PORTLET_READONLY + " is not readonly ?", mockPortletPreferences.isReadOnly(PORTLET_READONLY));
        }
        try {
            // Test reset of readonly value fails
            mockPortletPreferences.reset(PORTLET_READONLY);
            fail("Reset worked unexpectedly for readonly value");
        } catch (Exception ex) {
            assertTrue("Unexpected exception type failure ?  Ex: " + ex, ex instanceof ReadOnlyException);
            assertTrue(PORTLET_READONLY + " is not readonly ?", mockPortletPreferences.isReadOnly(PORTLET_READONLY));
        }
        try {
            // Test set of readonly value fails
            mockPortletPreferences.setValue(PORTLET_READONLY2, PORTLET_READONLY2_VALUE);
            fail("Setvalue worked unexpectedly for readonly value");
        } catch (Exception ex) {
            assertTrue("Unexpected exception type failure ?  Ex: " + ex, ex instanceof ReadOnlyException);
            assertTrue(PORTLET_READONLY2 + " is not readonly ?", mockPortletPreferences.isReadOnly(PORTLET_READONLY2));
        }
        try {
            // Test reset of readonly value fails
            mockPortletPreferences.reset(PORTLET_READONLY2);
            fail("Reset worked unexpectedly for readonly value");
        } catch (Exception ex) {
            assertTrue("Unexpected exception type failure ?  Ex: " + ex, ex instanceof ReadOnlyException);
            assertTrue(PORTLET_READONLY2 + " is not readonly ?", mockPortletPreferences.isReadOnly(PORTLET_READONLY2));
        }

        try {
            // Test initial set, get and reset
            mockPortletPreferences.setValue(PORTLET_WRITEABLE, PORTLET_WRITEABLE_VALUE);
            mockPortletPreferences.setValue(PORTLET_WRITEABLE2, PORTLET_WRITEABLE2_VALUE);
            assertEquals("Set value mismatch ?", PORTLET_WRITEABLE_VALUE, mockPortletPreferences.getValue(PORTLET_WRITEABLE, null));
            assertEquals("Set value mismatch ?", PORTLET_WRITEABLE2_VALUE, mockPortletPreferences.getValue(PORTLET_WRITEABLE2, null));
            assertFalse(PORTLET_WRITEABLE + " is readonly ?", mockPortletPreferences.isReadOnly(PORTLET_WRITEABLE));
            assertFalse(PORTLET_WRITEABLE2 + " is readonly ?", mockPortletPreferences.isReadOnly(PORTLET_WRITEABLE2));
            mockPortletPreferences.reset(PORTLET_WRITEABLE);
            mockPortletPreferences.reset(PORTLET_WRITEABLE2);
            assertNull("After reset getValue with null not null ?", mockPortletPreferences.getValue(PORTLET_WRITEABLE, null));
            assertNull("After reset getValue with null not null ?", mockPortletPreferences.getValue(PORTLET_WRITEABLE2, null));
        } catch (Exception ex) {
            fail("Set/Get/Reset failed unexpectedly: " + ex);
        }

        try {
            // Test initial array set, get, names and map
            mockPortletPreferences.setValues(PORTLET_WRITEABLE_ARRAY, PORTLET_WRITEABLE_ARRAY_VALUE);

            assertEquals("Set values mismatch ?", PORTLET_WRITEABLE_ARRAY_VALUE, mockPortletPreferences.getValues(PORTLET_WRITEABLE_ARRAY, null));
            assertFalse(PORTLET_WRITEABLE_ARRAY + " is readonly ?", mockPortletPreferences.isReadOnly(PORTLET_WRITEABLE_ARRAY));

            // There should be two readonly attributes plus the array attribute present at this point
            final Enumeration<String> preferencesNames = mockPortletPreferences.getNames();
            assertNotNull("Preferences names null after additions ?", preferencesNames);
            assertTrue("Preferences names empty after additions ?", preferencesNames.hasMoreElements());
            int preferencesNameCount = 0;
            while (preferencesNames.hasMoreElements()) {
                preferencesNameCount++;
                String currentPreferencesName = preferencesNames.nextElement();
                assertTrue("Preferences name not one of three expected matches ?", PORTLET_WRITEABLE_ARRAY.equals(currentPreferencesName) || 
                        PORTLET_READONLY.equals(currentPreferencesName) ||
                        PORTLET_READONLY2.equals(currentPreferencesName));
            }
            assertEquals("Preferences names size not  3 ?", 3, preferencesNameCount);

            final Map<String, String[]> preferencesMap = mockPortletPreferences.getMap();
            assertNotNull("Preferences map null ?", preferencesMap);
            assertEquals("Preferences map size not  3 ?", 3, preferencesMap.size());
            assertTrue("Map does not contain expected key ?", preferencesMap.containsKey(PORTLET_WRITEABLE_ARRAY));
            assertTrue("Map does not contain expected key ?", preferencesMap.containsKey(PORTLET_READONLY));
            assertTrue("Map does not contain expected key ?", preferencesMap.containsKey(PORTLET_READONLY2));
            assertTrue("Map does not contain expected value ?", preferencesMap.containsValue(PORTLET_WRITEABLE_ARRAY_VALUE));
            assertTrue("Map does not contain expected value ?", preferencesMap.containsValue(mockPortletPreferences.getValues(PORTLET_READONLY, null)));
            assertTrue("Map does not contain expected value ?", preferencesMap.containsValue(mockPortletPreferences.getValues(PORTLET_READONLY2, null)));
        } catch (Exception ex) {
            fail("Set/Get/Names/Map failed unexpectedly: " + ex);
        }

        // Verify store behaviour without validator present
        try {
            mockPortletPreferences.store();
        } catch (Exception ex) {
            fail("Store without validator failed ?  Exception: " + ex);
        }
        // Verify store behaviour with validator present
        try {
            mockPortletPreferences.setPreferencesValidator(new BasicPreferencesValidator());
            mockPortletPreferences.store();
            fail("Basic validator did not prevent store as expected ?");
        } catch (Exception ex) {
            assertTrue("Basic validator did not produce UnsupportedOperationException ?", ex instanceof UnsupportedOperationException);
        }
        // Verify store behaviour after validator cleared
        try {
            mockPortletPreferences.setPreferencesValidator(null);
            mockPortletPreferences.store();
        } catch (Exception ex) {
            fail("Store after clearing validator failed ?  Exception: " + ex);
        }
    }

    public void testMockPortletRequest() {
        final String TEST_DEFAULT_CONTENT_TYPE = "text/html";
        final String TEST_CONTENT_TYPE = "text/plain";
        final String TEST_CONTENT_TYPE2 = "text/csv";
        final String TEST_PROPERTY = "TEST_PROPERTY";
        final String TEST_PROPERTY_VALUE = "Property_Value_1";
        final String TEST_PROPERTY2 = "TEST_PROPERTY2";
        final String TEST_PROPERTY2_VALUE = "Property_Value_2";
        final String TEST_AUTHTYPE = "Test_Auth_Type";
        final String TEST_FAKECONTEXTPATH = "/fakeContextPath";
        final String TEST_FAKEREMOTEUSER = "fakeRemoteUser";
        final String TEST_FAKEUSERROLE = "fakeUserRole";
        final String TEST_ATTRIBUTE = "TEST_ATTRIBUTE";
        final String TEST_ATTRIBUTE_VALUE = "Attribute_Value_1";
        final String TEST_ATTRIBUTE2 = "TEST_ATTRIBUTE2";
        final String TEST_ATTRIBUTE_VALUE2 = "Attribute_Value_2";
        final String TEST_PARAM = "TEST_PARAM";
        final String TEST_PARAM_VALUE = "Param_Value_1";
        final String TEST_PARAM2 = "TEST_PARAM2";
        final String TEST_PARAM_VALUE2 = "PARAM_Value_2";
        final String TEST_ARRAYPARAM = "TEST_ARRAYPARAM";
        final String[] TEST_PARAM_ARRAYVALUE = { "ARRAYVALUE1", "ARRAYVALUE2" };
        final String TEST_SCHEME = "file";
        final String TEST_HOST = "localhost2";
        final int TEST_PORT = 8080;
        final String TEST_WINDOWID = "TEST_WINDOWID";
        final String TEST_COOKIE_ID = "TEST_COOKIE_ID";
        final String TEST_COOKIE_ID2 = "TEST_COOKIE_ID2";
        final String TEST_COOKIE_VALUE = "Cookie_Value";
        final String TEST_COOKIE_VALUE2 = "Cookie_Value2";
        final Cookie PORLET_TEST_COOKIE = new Cookie(TEST_COOKIE_ID, TEST_COOKIE_VALUE);
        final Cookie PORLET_TEST_COOKIE2 = new Cookie(TEST_COOKIE_ID2, TEST_COOKIE_VALUE2);

        // Call each constructor in sequence, do some basic checks
        MockPortletRequest mockPortletRequest = new MockPortletRequest();
        assertTrue("Default active state not true ?", mockPortletRequest.isActive());
        assertEquals("Default portlet mode not as expected ?", PortletMode.VIEW, mockPortletRequest.getPortletMode());
        assertEquals("Default window state not as expected ?", WindowState.NORMAL, mockPortletRequest.getWindowState());
        assertNotNull("Default preferences is null ?", mockPortletRequest.getPreferences());
        assertNotNull("Default portlet session is null ?", mockPortletRequest.getPortletSession());
        assertNotNull("Default portal context is null ?", mockPortletRequest.getPortalContext());
        assertEquals("Default context path not empty string ?", "", mockPortletRequest.getContextPath());
        assertNull("Default remote user not null ?", mockPortletRequest.getRemoteUser());
        assertNull("Default user principal not null ?", mockPortletRequest.getUserPrincipal());
        assertFalse("Default secure state not false ?", mockPortletRequest.isSecure());
        assertTrue("Default request sessionid not valid ?", mockPortletRequest.isRequestedSessionIdValid());
        assertEquals("Default locale not as expected ?", Locale.ENGLISH, mockPortletRequest.getLocale());
        assertEquals("Default response content-type not as expected ?", TEST_DEFAULT_CONTENT_TYPE, mockPortletRequest.getResponseContentType());
        assertNull("Default lifecycle phase attribute not null ?", mockPortletRequest.getAttribute(PortletRequest.LIFECYCLE_PHASE));
        assertEquals("Default scheme not as expected ?", "http", mockPortletRequest.getScheme());
        assertEquals("Default server name not as expected ?", "localhost", mockPortletRequest.getServerName());
        assertEquals("Default server port not as expected ?", 80, mockPortletRequest.getServerPort());
        final MockPortalContext mockPortalContext = new MockPortalContext();
        final MockPortletContext mockPortletContext = new MockPortletContext();
        new MockPortletRequest(mockPortletContext);  // Only test is to confirm constructor completes without exception
        mockPortletRequest = new MockPortletRequest(mockPortalContext, mockPortletContext);
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockPortletRequest.getPortalContext());
        mockPortletRequest.addResponseContentType(TEST_CONTENT_TYPE);
        mockPortletRequest.addResponseContentType(TEST_CONTENT_TYPE2);
        Enumeration<String> contentTypes = mockPortletRequest.getResponseContentTypes();
        assertNotNull("MockPortletRequest content-types null ?", contentTypes);
        final ArrayList<String> contentTypesList = new ArrayList<>();
        while (contentTypes.hasMoreElements()) {
            contentTypesList.add(contentTypes.nextElement());
        }
        assertEquals("MockPortletRequest does not contain three content-types ?", 3, contentTypesList.size());
        assertTrue("MockPortletRequest does not contain content-type " + TEST_CONTENT_TYPE + " ?", contentTypesList.contains(TEST_CONTENT_TYPE));
        assertTrue("MockPortletRequest does not contain content-type " + TEST_CONTENT_TYPE2 + " ?", contentTypesList.contains(TEST_CONTENT_TYPE2));
        assertTrue("MockPortletRequest does not contain default content-type + " + TEST_DEFAULT_CONTENT_TYPE + " ?", contentTypesList.contains(TEST_DEFAULT_CONTENT_TYPE));
        assertTrue("View portlet mode not allowed ?", mockPortletRequest.isPortletModeAllowed(PortletMode.VIEW));
        assertTrue("Edit portlet mode not allowed ?", mockPortletRequest.isPortletModeAllowed(PortletMode.EDIT));
        assertTrue("Help portlet mode not allowed ?", mockPortletRequest.isPortletModeAllowed(PortletMode.HELP));
        assertTrue("Normal window state not allowed ?", mockPortletRequest.isWindowStateAllowed(WindowState.NORMAL));
        assertTrue("Maximized window state not allowed ?", mockPortletRequest.isWindowStateAllowed(WindowState.MAXIMIZED));
        assertTrue("Minimized window state not allowed ?", mockPortletRequest.isWindowStateAllowed(WindowState.MINIMIZED));
        mockPortletRequest.setPortletMode(PortletMode.EDIT);
        assertEquals("Portlet mode does not match set value ?", PortletMode.EDIT, mockPortletRequest.getPortletMode());
        mockPortletRequest.setWindowState(WindowState.MAXIMIZED);
        assertEquals("Window state does not match set value ?", WindowState.MAXIMIZED, mockPortletRequest.getWindowState());
        final MockPortletPreferences mockPortletPreferences = new MockPortletPreferences();
        mockPortletRequest.setPreferences(mockPortletPreferences);
        assertEquals("Portlet preferences does not match set value ?", mockPortletPreferences, mockPortletRequest.getPreferences());
        final MockPortletSession mockPortletSession = new MockPortletSession(mockPortletContext);
        mockPortletRequest.setSession(mockPortletSession);
        assertEquals("Portlet session does not match set value ?", mockPortletSession, mockPortletRequest.getPortletSession());
        assertTrue("Set request sessionid not valid ?", mockPortletRequest.isRequestedSessionIdValid());
        mockPortletRequest.setRequestedSessionIdValid(false);
        assertFalse("Set request sessionid still valid after set invalid ?", mockPortletRequest.isRequestedSessionIdValid());
        mockPortletRequest.setRequestedSessionIdValid(true);
        assertTrue("Set request sessionid not valid after set valid ?", mockPortletRequest.isRequestedSessionIdValid());

        mockPortletRequest.setProperty(TEST_PROPERTY, TEST_PROPERTY_VALUE);
        mockPortletRequest.setProperty(TEST_PROPERTY2, TEST_PROPERTY2_VALUE);
        final Enumeration<String> propertyNames = mockPortletRequest.getPropertyNames();
        assertNotNull("MockPortalContext property names null ?", propertyNames);
        final ArrayList<String> propertyNamesList = new ArrayList<>();
        while (propertyNames.hasMoreElements()) {
            propertyNamesList.add(propertyNames.nextElement());
        }
        assertEquals("MockPortletRequest does not contain two properties ?", 2, propertyNamesList.size());
        assertTrue("MockPortletRequest does not contain property name " + TEST_PROPERTY + " ?", propertyNamesList.contains(TEST_PROPERTY));
        assertTrue("MockPortletRequest does not contain property name " + TEST_PROPERTY2 + " ?", propertyNamesList.contains(TEST_PROPERTY2));
        assertEquals("MockPortletRequest value of property name " + TEST_PROPERTY + " not as expected ?", TEST_PROPERTY_VALUE, mockPortletRequest.getProperty(TEST_PROPERTY));
        assertEquals("MockPortletRequest value of property name " + TEST_PROPERTY2 + " not as expected ?", TEST_PROPERTY2_VALUE, mockPortletRequest.getProperty(TEST_PROPERTY2));
        mockPortletRequest.addProperty(TEST_PROPERTY, TEST_PROPERTY2_VALUE);
        final Enumeration<String> propertiesForName = mockPortletRequest.getProperties(TEST_PROPERTY);
        assertNotNull("MockPortletRequest properties for " +  TEST_PROPERTY + " is null ?", propertiesForName);
        final ArrayList<String> propertiesForNameList = new ArrayList<>();
        while (propertiesForName.hasMoreElements()) {
            propertiesForNameList.add(propertiesForName.nextElement());
        }
        assertEquals("MockPortletRequest does not contain two values for " +  TEST_PROPERTY + " ?", 2, propertiesForNameList.size());
        assertTrue("MockPortletRequest does not contain expected value for property name " + TEST_PROPERTY + " ?", propertiesForNameList.contains(TEST_PROPERTY_VALUE));
        assertTrue("MockPortletRequest does not contain expected value for propety name " + TEST_PROPERTY + " ?", propertiesForNameList.contains(TEST_PROPERTY2_VALUE));

        mockPortletRequest.setAuthType(TEST_AUTHTYPE);
        assertEquals("Authtype not equal to set value ?", TEST_AUTHTYPE, mockPortletRequest.getAuthType());
        mockPortletRequest.setContextPath(TEST_FAKECONTEXTPATH);
        assertEquals("Context path not equal to set value ?", TEST_FAKECONTEXTPATH, mockPortletRequest.getContextPath());
        mockPortletRequest.setRemoteUser(TEST_FAKEREMOTEUSER);
        assertEquals("Remote user not equal to set value ?", TEST_FAKEREMOTEUSER, mockPortletRequest.getRemoteUser());
        final TestMockPrincipal testMockPrincipal = new TestMockPrincipal();
        mockPortletRequest.setUserPrincipal(testMockPrincipal);
        assertEquals("User principal not equal to set value ?", testMockPrincipal, mockPortletRequest.getUserPrincipal());
        mockPortletRequest.addUserRole(TEST_FAKEUSERROLE);
        assertTrue("Usser not in added role ?", mockPortletRequest.isUserInRole(TEST_FAKEUSERROLE));

        assertNull(TEST_PARAM + " present before set ?", mockPortletRequest.getParameter(TEST_PARAM));
        assertNull(TEST_PARAM2 + " present before set ?", mockPortletRequest.getParameter(TEST_PARAM));
        mockPortletRequest.addParameter(TEST_PARAM, TEST_PARAM_VALUE);
        mockPortletRequest.addParameter(TEST_PARAM2, TEST_PARAM_VALUE2);
        assertEquals("Retrieved parameter 1 does not match set value ?", TEST_PARAM_VALUE, mockPortletRequest.getParameter(TEST_PARAM));
        assertEquals("Retrieved parameter 2 does not match set value ?", TEST_PARAM_VALUE2, mockPortletRequest.getParameter(TEST_PARAM2));
        final Enumeration<String> parameterNames = mockPortletRequest.getParameterNames();
        int parameterNameCount = 0;
        assertNotNull("Parameter names null after additions ?", parameterNames);
        assertTrue("Parameter names empty after additions ?", parameterNames.hasMoreElements());
        while (parameterNames.hasMoreElements()) {
            parameterNameCount++;
            String currentParameterName = parameterNames.nextElement();
            assertTrue("Param name not one of two expected matches ?", TEST_PARAM.equals(currentParameterName) || 
                    TEST_PARAM2.equals(currentParameterName));
        }
        assertEquals("Parameter names size not 2 ?", 2, parameterNameCount);
        mockPortletRequest.addParameter(TEST_ARRAYPARAM, TEST_PARAM_ARRAYVALUE);
        assertArrayEquals("Array parameter value not set as expected ?", TEST_PARAM_ARRAYVALUE, mockPortletRequest.getParameterValues(TEST_ARRAYPARAM));
        final Map<String, String[]> parameterMap = mockPortletRequest.getParameterMap();
        assertNotNull("Parameter map is null ?", parameterMap);
        assertTrue("Expected parameter not present ?", parameterMap.containsKey(TEST_PARAM));
        assertTrue("Expected parameter not present ?", parameterMap.containsKey(TEST_PARAM2));
        assertTrue("Expected parameter not present ?", parameterMap.containsKey(TEST_ARRAYPARAM));

        assertNull(TEST_ATTRIBUTE + " present before set ?", mockPortletRequest.getAttribute(TEST_ATTRIBUTE));
        assertNull(TEST_ATTRIBUTE2 + " present before set ?", mockPortletRequest.getAttribute(TEST_ATTRIBUTE2));
        mockPortletRequest.setAttribute(TEST_ATTRIBUTE, TEST_ATTRIBUTE_VALUE);
        mockPortletRequest.setAttribute(TEST_ATTRIBUTE2, TEST_ATTRIBUTE_VALUE2);
        assertEquals("Retrieved atribute 1 does not match set value ?", TEST_ATTRIBUTE_VALUE, mockPortletRequest.getAttribute(TEST_ATTRIBUTE));
        assertEquals("Retrieved atribute 2 does not match set value ?", TEST_ATTRIBUTE_VALUE2, mockPortletRequest.getAttribute(TEST_ATTRIBUTE2));
        final Enumeration<String> attributeNames = mockPortletRequest.getAttributeNames();
        int attributeNameCount = 0;
        assertNotNull("Attribute names null after additions ?", attributeNames);
        assertTrue("Attribute names empty after additions ?", attributeNames.hasMoreElements());
        while (attributeNames.hasMoreElements()) {
            attributeNameCount++;
            String currentAttibuteName = attributeNames.nextElement();
            assertTrue("Attribute name not one of two or three expected matches ?", TEST_ATTRIBUTE.equals(currentAttibuteName) || 
                    TEST_ATTRIBUTE2.equals(currentAttibuteName) ||
                    PortletRequest.LIFECYCLE_PHASE.equals(currentAttibuteName));
        }
        assertTrue("Atribute names size not 2 or 3 ?", attributeNameCount >= 2 && attributeNameCount <= 3);

        mockPortletRequest.addPreferredLocale(Locale.FRENCH);
        assertEquals("Preferred locale not as expected ?", Locale.FRENCH, mockPortletRequest.getLocale());
        final Enumeration<Locale> locales = mockPortletRequest.getLocales();
        assertNotNull("Locales null after additions ?", locales);
        assertTrue("Locales names empty after additions ?", locales.hasMoreElements());
        final Locale firstLocale = locales.nextElement();
        assertEquals("First locale not set value ?", Locale.FRENCH, firstLocale);
        final Locale secondLocale = locales.nextElement();
        assertEquals("Second locale not default value ?", Locale.ENGLISH, secondLocale);
        mockPortletRequest.setScheme(TEST_SCHEME);
        assertEquals("Scheme not equal to set value ?", TEST_SCHEME, mockPortletRequest.getScheme());
        mockPortletRequest.setServerName(TEST_HOST);
        assertEquals("Server name not equal to set value ?", TEST_HOST, mockPortletRequest.getServerName());
        mockPortletRequest.setServerPort(TEST_PORT);
        assertEquals("Server port not equal to set value ?", TEST_PORT, mockPortletRequest.getServerPort());
        mockPortletRequest.setWindowID(TEST_WINDOWID);
        assertEquals("Window id not equal to set value ?", TEST_WINDOWID, mockPortletRequest.getWindowID());

        mockPortletRequest.setCookies(PORLET_TEST_COOKIE, PORLET_TEST_COOKIE2);
        final Cookie[] cookies = mockPortletRequest.getCookies();
        assertNotNull("Cookies array null ?", cookies);
        assertEquals("Cookies array length not 2 ?", 2, cookies.length);
        for (Cookie cookie : cookies) {
            assertTrue("Cookie not one of set cookies ?", PORLET_TEST_COOKIE.equals(cookie) || PORLET_TEST_COOKIE2.equals(cookie));
        }

        final Map<String, String[]> originalPrivateParameters = mockPortletRequest.getPrivateParameterMap();
        final Map<String, String[]> originalPublicParameters = mockPortletRequest.getPublicParameterMap();
        assertNotNull("Original private parameters map is null ?", originalPrivateParameters);
        assertFalse("Original private parameters map is empty ?", originalPrivateParameters.isEmpty());
        assertTrue("Original private parameters map does not contain " + TEST_PARAM + " ?", originalPrivateParameters.containsKey(TEST_PARAM));
        assertTrue("Original private parameters map does not contain " + TEST_PARAM2 + " ?", originalPrivateParameters.containsKey(TEST_PARAM2));
        assertNotNull("Original public parameters map is null ?", originalPublicParameters);
        assertTrue("Original public parameters map is not empty ?", originalPublicParameters.isEmpty());
        mockPortletRequest.registerPublicParameter(TEST_PARAM2);
        final Map<String, String[]> updatedPrivateParameters = mockPortletRequest.getPrivateParameterMap();
        final Map<String, String[]> updatedPublicParameters = mockPortletRequest.getPublicParameterMap();
        assertNotNull("Updated private parameters map is null ?", updatedPrivateParameters);
        assertFalse("Updated private parameters map is empty ?", updatedPrivateParameters.isEmpty());
        assertFalse("Updated private parameters map contains " + TEST_PARAM2 + " ?", updatedPrivateParameters.containsKey(TEST_PARAM2));
        assertTrue("Updated private parameters map does not contain " + TEST_PARAM + " ?", updatedPrivateParameters.containsKey(TEST_PARAM));
        assertNotNull("Updated public parameters map is null ?", updatedPublicParameters);
        assertFalse("Updated public parameters map is empty ?", updatedPublicParameters.isEmpty());
        assertFalse("Updated public parameters map contains " + TEST_PARAM + " ?", updatedPublicParameters.containsKey(TEST_PARAM));
        assertTrue("Updated public parameters map does not contain " + TEST_PARAM2 + " ?", updatedPublicParameters.containsKey(TEST_PARAM2));
    }

    public void testMockPortletRequestDispatcher() {
        final String TEST_DISPATCH_URL = "localhost:8080/fakeurl";

        // Call each constructor in sequence, do some basic checks
        MockPortletRequestDispatcher mockPortletRequestDispatcher = new MockPortletRequestDispatcher(TEST_DISPATCH_URL);
        final MockRenderRequest mockRenderRequest = new MockRenderRequest();
        final MockRenderResponse mockRenderResponse = new MockRenderResponse();
        try {
            mockPortletRequestDispatcher.include(mockRenderRequest, mockRenderResponse);
        } catch (Exception ex) {
            fail("MockPortletRequestDispatcher include (render) failed.  Exception: " + ex);
        }
        final MockPortletRequest mockPortletRequest = new MockPortletRequest();
        final MockPortletResponse mockPortletResponse = new MockPortletResponse();
        final MockMimeResponse mockMimeResponse = new MockMimeResponse();
        try {
            mockPortletRequestDispatcher.include(mockPortletRequest, mockPortletResponse);
            fail("Include of non-MockMimeResponse did not throw an exception ?");
        } catch (IllegalArgumentException iae) {
             // Expected failure
        } catch (Exception ex) {
            fail("MockPortletRequestDispatcher include (portlet) failed.  Exception: " + ex);
        }
        try {
            mockPortletRequestDispatcher.include(mockPortletRequest, mockMimeResponse);
        } catch (Exception ex) {
            fail("MockPortletRequestDispatcher include (portlet) failed.  Exception: " + ex);
        }
        try {
            mockPortletRequestDispatcher.forward(mockPortletRequest, mockPortletResponse);
            fail("Forward of non-MockMimeResponse did not throw an exception ?");
        } catch (IllegalArgumentException iae) {
             // Expected failure
        } catch (Exception ex) {
            fail("MockPortletRequestDispatcher forward failed.  Exception: " + ex);
        }
        try {
            mockPortletRequestDispatcher.forward(mockPortletRequest, mockMimeResponse);
        } catch (Exception ex) {
            fail("MockPortletRequestDispatcher forward failed.  Exception: " + ex);
        }
    }

    public void testMockPortletResponse() {
        final String TEST_ENCODE_URL = "localhost:8080/fakeforwardedurl?param1=param with spaces";
        final String TEST_NAMESPACE = "Test_Namespace";
        final String TEST_PROPERTY = "TEST_PROPERTY";
        final String TEST_PROPERTY_VALUE = "Property_Value_1";
        final String TEST_PROPERTY2 = "TEST_PROPERTY2";
        final String TEST_PROPERTY2_VALUE = "Property_Value_2";
        final String TEST_XMLPROPERTY = "TEST_XMLPROPERTY";
        final String TEST_XML_TAGNAME = "TEST_XML_TAGNAME";
        final String TEST_COOKIE_ID = "TEST_COOKIE_ID";

        // Call each constructor in sequence, do some basic checks
        MockPortletResponse mockPortletResponse = new MockPortletResponse();
        assertEquals("Default namespace not as expected ?", "", mockPortletResponse.getNamespace());
        assertNotNull("Default portal context null ?", mockPortletResponse.getPortalContext());
        assertEquals("Encoded URL not as expected ?", TEST_ENCODE_URL, mockPortletResponse.encodeURL(TEST_ENCODE_URL));  // Mock URL encode does nothing
        mockPortletResponse.setNamespace(TEST_NAMESPACE);
        assertEquals("Set namespace not as expected ?", TEST_NAMESPACE, mockPortletResponse.getNamespace());
        final MockPortalContext mockPortalContext = new MockPortalContext();
        mockPortletResponse = new MockPortletResponse(mockPortalContext);
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockPortletResponse.getPortalContext());

        mockPortletResponse.setProperty(TEST_PROPERTY, TEST_PROPERTY_VALUE);
        mockPortletResponse.setProperty(TEST_PROPERTY2, TEST_PROPERTY2_VALUE);
        final Set<String> propertyNames = mockPortletResponse.getPropertyNames();
        assertNotNull("MockPortletResponse property names null ?", propertyNames);
        assertEquals("MockPortletResponse does not contain two properties ?", 2, propertyNames.size());
        assertTrue("MockPortletResponse does not contain property name " + TEST_PROPERTY + " ?", propertyNames.contains(TEST_PROPERTY));
        assertTrue("MockPortletResponse does not contain property name " + TEST_PROPERTY2 + " ?", propertyNames.contains(TEST_PROPERTY2));
        assertEquals("MockPortletResponse value of property name " + TEST_PROPERTY + " not as expected ?", TEST_PROPERTY_VALUE, mockPortletResponse.getProperty(TEST_PROPERTY));
        assertEquals("MockPortletResponse value of property name " + TEST_PROPERTY2 + " not as expected ?", TEST_PROPERTY2_VALUE, mockPortletResponse.getProperty(TEST_PROPERTY2));
        mockPortletResponse.addProperty(TEST_PROPERTY, TEST_PROPERTY2_VALUE);
        final String[] propertiesForName = mockPortletResponse.getProperties(TEST_PROPERTY);
        assertNotNull("MockPortletResponse properties for " +  TEST_PROPERTY + " is null ?", propertiesForName);
        final ArrayList<String> propertiesForNameList = new ArrayList<>();
        propertiesForNameList.addAll(Arrays.asList(propertiesForName));
        assertEquals("MockPortletResponse does not contain two values for " +  TEST_PROPERTY + " ?", 2, propertiesForNameList.size());
        assertTrue("MockPortletResponse does not contain expected value for property name " + TEST_PROPERTY + " ?", propertiesForNameList.contains(TEST_PROPERTY_VALUE));
        assertTrue("MockPortletResponse does not contain expected value for propety name " + TEST_PROPERTY + " ?", propertiesForNameList.contains(TEST_PROPERTY2_VALUE));

        final Cookie[] cookies = mockPortletResponse.getCookies();
        assertNotNull("Cookies array null ?", cookies);
        assertEquals("Cookies array length not 0 ?", 0, cookies.length);
        assertNull("Cookie found ?", mockPortletResponse.getCookie(TEST_COOKIE_ID));

        Set<String> xmlPropertyNames = mockPortletResponse.getXmlPropertyNames();
        assertNotNull("MockPortletResponse XML property names null ?", xmlPropertyNames);
        assertTrue("XML properites set not empty ?", xmlPropertyNames.isEmpty());
        assertNull("XML property found ?", mockPortletResponse.getXmlProperty(TEST_XMLPROPERTY));
        Element[] xmlProperties = mockPortletResponse.getXmlProperties(TEST_XMLPROPERTY);
        assertNull("XML properties found ?", xmlProperties);
        Element element = mockPortletResponse.createElement(TEST_XML_TAGNAME);
        assertNotNull("MockPortletResponse XML element null ?", element);
        mockPortletResponse.addProperty(TEST_XMLPROPERTY, element);
        xmlPropertyNames = mockPortletResponse.getXmlPropertyNames();
        assertNotNull("MockPortletResponse XML property names null ?", xmlPropertyNames);
        assertFalse("XML properites set empty ?", xmlPropertyNames.isEmpty());
        assertNotNull("XML property not found ?", mockPortletResponse.getXmlProperty(TEST_XMLPROPERTY));
        xmlProperties = mockPortletResponse.getXmlProperties(TEST_XMLPROPERTY);
        assertNotNull("XML properties not found ?", xmlProperties);
        assertEquals("XML properties not expected size ?", 1, xmlProperties.length);
        assertEquals("XML property at index 0 not element value ?", element, xmlProperties[0]);
    }

    public void testMockPortletSession() {
        final String TEST_ATTRIBUTE = "TEST_ATTRIBUTE";
        final String TEST_ATTRIBUTE_VALUE = "Attribute_Value_1";
        final String TEST_ATTRIBUTE2 = "TEST_ATTRIBUTE2";
        final String TEST_ATTRIBUTE_VALUE2 = "Attribute_Value_2";
        final long TEST_START_TIME = System.currentTimeMillis();
        final int TEST_MAXINACTIVE_INTERVAL = 300;

        // Call each constructor in sequence, do some basic checks
        MockPortletSession mockPortletSession = new MockPortletSession();
        final String INITIAL_SESSION_ID = mockPortletSession.getId();
        assertNotNull("MockPortletSession session id null ?", INITIAL_SESSION_ID);
        assertFalse("MockPortletSession session is invalid ?", mockPortletSession.isInvalid());
        assertTrue("MockPortletSession session is not new ?", mockPortletSession.isNew());
        mockPortletSession.setNew(false);
        assertFalse("MockPortletSession session is still new ?", mockPortletSession.isNew());
        mockPortletSession.setNew(true);
        assertTrue("MockPortletSession session is not new after set ?", mockPortletSession.isNew());
        assertEquals("MockPortletSession max inactive interval not 0 ?", 0, mockPortletSession.getMaxInactiveInterval());
        assertNotNull("Default portlet context null ?", mockPortletSession.getPortletContext());
        assertTrue("MockPortletSession session creation time in past ?", mockPortletSession.getCreationTime() >= TEST_START_TIME);
        assertTrue("MockPortletSession session last accessed time in past ?", mockPortletSession.getLastAccessedTime() >= TEST_START_TIME);
        assertTrue("MockPortletSession session last accessed time older than creation time ?", mockPortletSession.getLastAccessedTime() >= mockPortletSession.getCreationTime());
        assertNotNull("Default portlet attribute map null ?", mockPortletSession.getAttributeMap());
        assertNotNull("Default portlet portletscope attribute map null ?", mockPortletSession.getAttributeMap(PortletSession.PORTLET_SCOPE));
        assertNotNull("Default portlet applicationscope attribute map null ?", mockPortletSession.getAttributeMap(PortletSession.APPLICATION_SCOPE));
        final MockPortletContext mockPortletContext = new MockPortletContext();
        mockPortletSession = new MockPortletSession(mockPortletContext);
        assertEquals("MockPortletSession portletcontext not equal to set value ?", mockPortletContext, mockPortletSession.getPortletContext());
        final String NEW_SESSION_ID = mockPortletSession.getId();
        assertNotNull("MockPortletSession session id null ?", NEW_SESSION_ID);
        assertNotEquals("Original and new session id match ?", NEW_SESSION_ID, INITIAL_SESSION_ID);

        assertNull(TEST_ATTRIBUTE + " present before set ?", mockPortletSession.getAttribute(TEST_ATTRIBUTE));
        assertNull(TEST_ATTRIBUTE2 + " present before set ?", mockPortletSession.getAttribute(TEST_ATTRIBUTE2));
        mockPortletSession.setAttribute(TEST_ATTRIBUTE, TEST_ATTRIBUTE_VALUE);
        mockPortletSession.setAttribute(TEST_ATTRIBUTE2, TEST_ATTRIBUTE_VALUE2);
        assertEquals("Retrieved atribute 1 does not match set value ?", TEST_ATTRIBUTE_VALUE, mockPortletSession.getAttribute(TEST_ATTRIBUTE));
        assertEquals("Retrieved atribute 2 does not match set value ?", TEST_ATTRIBUTE_VALUE2, mockPortletSession.getAttribute(TEST_ATTRIBUTE2));
        final Enumeration<String> attributeNames = mockPortletSession.getAttributeNames();
        int attributeNameCount = 0;
        assertNotNull("Attribute names null after additions ?", attributeNames);
        assertTrue("Attribute names empty after additions ?", attributeNames.hasMoreElements());
        while (attributeNames.hasMoreElements()) {
            attributeNameCount++;
            String currentAttibuteName = attributeNames.nextElement();
            assertTrue("Attribute name not one of two expected matches ?", TEST_ATTRIBUTE.equals(currentAttibuteName) || 
                    TEST_ATTRIBUTE2.equals(currentAttibuteName));
        }
        assertEquals("Atribute names size not expected value ?", 2, attributeNameCount);
        assertNull(TEST_ATTRIBUTE + " present before set (application scope) ?", mockPortletSession.getAttribute(TEST_ATTRIBUTE, PortletSession.APPLICATION_SCOPE));
        assertNull(TEST_ATTRIBUTE2 + " present before set (application scope) ?", mockPortletSession.getAttribute(TEST_ATTRIBUTE2, PortletSession.APPLICATION_SCOPE));
        mockPortletSession.setAttribute(TEST_ATTRIBUTE, TEST_ATTRIBUTE_VALUE, PortletSession.APPLICATION_SCOPE);
        mockPortletSession.setAttribute(TEST_ATTRIBUTE2, TEST_ATTRIBUTE_VALUE2, PortletSession.APPLICATION_SCOPE);
        assertEquals("Retrieved atribute 1 does not match set value (application scope) ?", TEST_ATTRIBUTE_VALUE, mockPortletSession.getAttribute(TEST_ATTRIBUTE, PortletSession.APPLICATION_SCOPE));
        assertEquals("Retrieved atribute 2 does not match set value (application scope) ?", TEST_ATTRIBUTE_VALUE2, mockPortletSession.getAttribute(TEST_ATTRIBUTE2, PortletSession.APPLICATION_SCOPE));
        final Enumeration<String> attributeNamesApplication = mockPortletSession.getAttributeNames(PortletSession.APPLICATION_SCOPE);
        int attributeNameCountApplication = 0;
        assertNotNull("Attribute names null after additions (application scope) ?", attributeNamesApplication);
        assertTrue("Attribute names empty after additions (application scope) ?", attributeNamesApplication.hasMoreElements());
        while (attributeNamesApplication.hasMoreElements()) {
            attributeNameCountApplication++;
            String currentAttibuteName = attributeNamesApplication.nextElement();
            assertTrue("Attribute name not one of two expected matches (application scope) ?", TEST_ATTRIBUTE.equals(currentAttibuteName) || 
                    TEST_ATTRIBUTE2.equals(currentAttibuteName));
        }
        assertEquals("Atribute names size not expected value (application scope) ?", 2, attributeNameCountApplication);

        assertTrue("MockPortletSession session is not new ?", mockPortletSession.isNew());
        mockPortletSession.access();
        assertFalse("MockPortletSession session is still new after access ?", mockPortletSession.isNew());
        mockPortletSession.setMaxInactiveInterval(TEST_MAXINACTIVE_INTERVAL);
        assertEquals("MockPortletSession max inactive interval not set value ?", TEST_MAXINACTIVE_INTERVAL, mockPortletSession.getMaxInactiveInterval());
        mockPortletSession.removeAttribute(TEST_ATTRIBUTE2);
        assertNull(TEST_ATTRIBUTE2 + " still present after removal ?", mockPortletSession.getAttribute(TEST_ATTRIBUTE2));
        mockPortletSession.removeAttribute(TEST_ATTRIBUTE, PortletSession.APPLICATION_SCOPE);
        assertNull(TEST_ATTRIBUTE + " still present after removal (application scope) ?", mockPortletSession.getAttribute(TEST_ATTRIBUTE, PortletSession.APPLICATION_SCOPE));
        final long BEFORE_INVALIDATE_TIME = System.currentTimeMillis();
        mockPortletSession.invalidate();
        assertTrue("MockPortletSession session is still valid after invalidate ?", mockPortletSession.isInvalid());
        assertNull(TEST_ATTRIBUTE + " still present after invalidate ?", mockPortletSession.getAttribute(TEST_ATTRIBUTE));
        assertNull(TEST_ATTRIBUTE2 + " still present after removal (application scope) ?", mockPortletSession.getAttribute(TEST_ATTRIBUTE2, PortletSession.APPLICATION_SCOPE));
        assertTrue("MockPortletSession session last accessed time older than creation time ?", mockPortletSession.getLastAccessedTime() >= mockPortletSession.getCreationTime());
        assertTrue("MockPortletSession session last accessed time older than time before invalidation ?", mockPortletSession.getLastAccessedTime() >= BEFORE_INVALIDATE_TIME);
    }

    public void testMockPortletURL() {
        final String TEST_PARAMETER = "TEST_PARAMETER";

        // Call each constructor in sequence, do some basic checks
        final MockPortalContext mockPortalContext = new MockPortalContext();
        final MockPortletURL mockPortletURL = new MockPortletURL(mockPortalContext, MockPortletURL.URL_TYPE_RENDER);
        assertNull("MockPortletURL default portlet mode not null ?", mockPortletURL.getPortletMode());
        assertNull("MockPortletURL default window state not null ?", mockPortletURL.getWindowState());
        try {
            mockPortletURL.setWindowState(WindowState.MAXIMIZED);
        } catch (Exception ex) {
            fail("MockPortletURL window state set failed.  Exception: " + ex);
        }
        assertEquals("Window state does not match set value ?", WindowState.MAXIMIZED, mockPortletURL.getWindowState());
        try {
            mockPortletURL.setPortletMode(PortletMode.VIEW);
        } catch (Exception ex) {
            fail("MockPortletURL portlet mode set failed.  Exception: " + ex);
        }
        assertEquals("PortletMode.VIEW not set ?", PortletMode.VIEW, mockPortletURL.getPortletMode());
        final String portletURLAsString = mockPortletURL.toString();
        assertNotNull("MockPortletURL toString() result null ?", portletURLAsString);
        assertTrue("MockPortletURL toString() result is empty ?", portletURLAsString.length() > 0);
        mockPortletURL.removePublicRenderParameter(TEST_PARAMETER);  // Just confirm no exception
    }

    public void testMockRenderRequest() {
        final MockPortletContext mockPortletContext = new MockPortletContext();
        final MockPortalContext mockPortalContext = new MockPortalContext();

        // Call each constructor in sequence, do some basic checks
        MockRenderRequest mockRenderRequest = new MockRenderRequest();
        assertNull("MockRenderRequest ETag not null ?", mockRenderRequest.getETag());
        mockRenderRequest = new MockRenderRequest(PortletMode.HELP);
        assertEquals("MockRenderRequest portlet mode does not match constructor-set value ?", PortletMode.HELP, mockRenderRequest.getPortletMode());
        mockRenderRequest = new MockRenderRequest(PortletMode.HELP, WindowState.MAXIMIZED);
        assertEquals("MockRenderRequest portlet mode does not match constructor-set value ?", PortletMode.HELP, mockRenderRequest.getPortletMode());
        assertEquals("MockRenderRequest window state does not match constructor-set value ?", WindowState.MAXIMIZED, mockRenderRequest.getWindowState());
        new MockRenderRequest(mockPortletContext);  // Only test is to confirm constructor completes without exception
        mockRenderRequest = new MockRenderRequest(mockPortalContext, mockPortletContext);
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockRenderRequest.getPortalContext());
    }

    public void testMockRenderResponse() {
        final String TEST_TITLE = "TEST_TITLE";

        // Call each constructor in sequence, do some basic checks
        final MockPortalContext mockPortalContext = new MockPortalContext();
        final MockRenderRequest mockRenderRequest = new MockRenderRequest();
        MockRenderResponse mockRenderResponse = new MockRenderResponse();
        assertNull("MockRenderResponse title not null ?", mockRenderResponse.getTitle());
        mockRenderResponse = new MockRenderResponse(mockPortalContext);    // Only test is to confirm constructor completes without exception
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockRenderResponse.getPortalContext());
        mockRenderResponse = new MockRenderResponse(mockPortalContext, mockRenderRequest);
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockRenderResponse.getPortalContext());
        mockRenderResponse.setTitle(TEST_TITLE);
        assertEquals("Title does not match set value ?", TEST_TITLE, mockRenderResponse.getTitle());
        final Collection<PortletMode> portletModes = new ArrayList<>();
        portletModes.add(PortletMode.EDIT);
        portletModes.add(PortletMode.VIEW);
        mockRenderResponse.setNextPossiblePortletModes(portletModes);
        assertEquals("Next possible portlet modes not equal to set value ?", portletModes, mockRenderResponse.getNextPossiblePortletModes());
    }

    public void testMockResourceRequest() {
        final String TEST_RESOURCEID = "TEST_RESOURCEID";
        final String TEST_CACHEABILITY = "TEST_CACHEABILITY";
        final String TEST_PARAM = "TEST_PARAM";
        final String TEST_PARAM2 = "TEST_PARAM2";
        final String TEST_PARAM_VALUE = "NORMALVALUE";
        final String[] TEST_PARAM_ARRAYVALUE = { "ARRAYVALUE1", "ARRAYVALUE2" };

        // Call each constructor in sequence, do some basic checks
        MockResourceRequest mockResourceRequest = new MockResourceRequest();
        assertNull("Default resource id not null ?", mockResourceRequest.getResourceID());
        assertNull("Default cacheability not null ?", mockResourceRequest.getCacheability());
        assertNull("Default ETag not null ?", mockResourceRequest.getETag());
        mockResourceRequest = new MockResourceRequest(TEST_RESOURCEID);
        assertEquals("Resource id not set value ?", TEST_RESOURCEID, mockResourceRequest.getResourceID());
        final MockPortletContext mockPortletContext = new MockPortletContext();
        final MockPortalContext mockPortalContext = new MockPortalContext();
        final MockResourceURL mockResourceURL = new MockResourceURL();
        new MockResourceRequest(mockResourceURL);     // Only test is to confirm constructor completes without exception
        new MockResourceRequest(mockPortletContext);  // Only test is to confirm constructor completes without exception
        mockResourceRequest = new MockResourceRequest(mockPortalContext, mockPortletContext);
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockResourceRequest.getPortalContext());
        mockResourceRequest.setCacheability(TEST_CACHEABILITY);
        assertEquals("Cacheability does not match set value ?", TEST_CACHEABILITY, mockResourceRequest.getCacheability());
        mockResourceRequest.addPrivateRenderParameter(TEST_PARAM, TEST_PARAM_VALUE);
        mockResourceRequest.addPrivateRenderParameter(TEST_PARAM2, TEST_PARAM_ARRAYVALUE);
        final Map<String, String[]> privateRenderParameterMap = mockResourceRequest.getPrivateRenderParameterMap();
        assertNotNull("Parameter map is null ?", privateRenderParameterMap);
        assertTrue("Expected parameter not present ?", privateRenderParameterMap.containsKey(TEST_PARAM));
        assertTrue("Expected parameter not present ?", privateRenderParameterMap.containsKey(TEST_PARAM2));
    }

    public void testMockResourceResponse() {
        final int TEST_CONTENT_LENGTH = 4096;

        // Call each constructor in sequence, do some basic checks
        MockResourceResponse mockResourceRequest = new MockResourceResponse();
        assertEquals("MockResourceResponse initial content length not 0 ?", 0, mockResourceRequest.getContentLength());
        mockResourceRequest.setContentLength(TEST_CONTENT_LENGTH);
        assertEquals("Content length not equal to set value ?", TEST_CONTENT_LENGTH, mockResourceRequest.getContentLength());
    }

    public void testMockResourceURL() {
        final String TEST_RESOURCEID = "TEST_RESOURCEID";
        final String TEST_CACHEABILITY = "TEST_CACHEABILITY";

        // Call each constructor in sequence, do some basic checks
        MockResourceURL mockResourceURL = new MockResourceURL();
        assertNull("Default resource id not null ?", mockResourceURL.getResourceID());
        assertNull("Default cacheability not null ?", mockResourceURL.getCacheability());
        mockResourceURL.setResourceID(TEST_RESOURCEID);
        assertEquals("Resource id not set value ?", TEST_RESOURCEID, mockResourceURL.getResourceID());
        mockResourceURL.setCacheability(TEST_CACHEABILITY);
        assertEquals("Cacheability does not match set value ?", TEST_CACHEABILITY, mockResourceURL.getCacheability());
        final String resourceURLAsString = mockResourceURL.toString();
        assertNotNull("MockResourceURL toString() result null ?", resourceURLAsString);
        assertTrue("MockResourceURL toString() result is empty ?", resourceURLAsString.length() > 0);
    }

    public void testMockStateAwareResponse() {
        final String TEST_PORTLETNAME = "TestPortletName";
        final String TEST_DEFAULTNAMESPACE = "Test_Default_Namespace";
        final String TEST_QNAME3_STRING = "TestQname3String";
        final QName TEST_QNAME = new QName(TEST_PORTLETNAME);
        final QName TEST_QNAME2 = new QName(TEST_DEFAULTNAMESPACE);
        final String TEST_QNAME_VALUE = "Test_Qname_Value";
        final String TEST_QNAME2_VALUE = "Test_Qname2_Value";
        final String TEST_QNAME3_VALUE = "Test_Qname3_Value";
        final String TEST_PARAM = "TEST_PARAM";
        final String TEST_PARAM2 = "TEST_PARAM2";
        final String TEST_PARAM3 = "TEST_PARAM3";
        final String TEST_PARAM_VALUE = "NORMALVALUE";
        final String[] TEST_PARAM_ARRAYVALUE = { "ARRAYVALUE1", "ARRAYVALUE2" };
        final String[] TEST_PARAM_ARRAYVALUE2 = { "ARRAYVALUE3", "ARRAYVALUE4" };

        // Call each constructor in sequence, do some basic checks
        MockStateAwareResponse mockStateAwareResponse = new MockStateAwareResponse();
        try {
            mockStateAwareResponse.setPortletMode(PortletMode.VIEW);
        } catch (Exception ex) {
            fail("MockStateAwareResponse portlet mode set failed.  Exception: " + ex);
        }
        assertEquals("PortletMode.VIEW not set ?", PortletMode.VIEW, mockStateAwareResponse.getPortletMode());
        try {
            mockStateAwareResponse.setPortletMode(PortletMode.EDIT);
        } catch (Exception ex) {
            fail("MockStateAwareResponse portlet mode set failed.  Exception: " + ex);
        }
        assertEquals("Portlet mode does not match set value ?", PortletMode.EDIT, mockStateAwareResponse.getPortletMode());
        try {
            mockStateAwareResponse.setWindowState(WindowState.MAXIMIZED);
        } catch (Exception ex) {
            fail("MockStateAwareResponse portlet mode set failed.  Exception: " + ex);
        }
        assertEquals("Window state does not match set value ?", WindowState.MAXIMIZED, mockStateAwareResponse.getWindowState());
        final MockPortalContext mockPortalContext = new MockPortalContext();
        mockStateAwareResponse = new MockStateAwareResponse(mockPortalContext);
        assertEquals("Portal context does not match constructor-set value ?", mockPortalContext, mockStateAwareResponse.getPortalContext());

        assertNull("Initial event already present ?", mockStateAwareResponse.getEvent(TEST_PORTLETNAME));
        assertNull("Initial event already present ?", mockStateAwareResponse.getEvent(TEST_QNAME));
        assertNull("Initial event already present ?", mockStateAwareResponse.getEvent(TEST_DEFAULTNAMESPACE));
        assertNull("Initial event already present ?", mockStateAwareResponse.getEvent(TEST_QNAME2));
        mockStateAwareResponse.setEvent(TEST_QNAME, TEST_QNAME_VALUE);
        mockStateAwareResponse.setEvent(TEST_QNAME2, TEST_QNAME2_VALUE);
        mockStateAwareResponse.setEvent(TEST_QNAME3_STRING, TEST_QNAME3_VALUE);
        assertEquals("First event QName not set value ?", TEST_QNAME_VALUE, mockStateAwareResponse.getEvent(TEST_QNAME));
        assertEquals("Second event QName not set value ?", TEST_QNAME2_VALUE, mockStateAwareResponse.getEvent(TEST_QNAME2));
        assertEquals("Third event QName not set value ?", TEST_QNAME3_VALUE, mockStateAwareResponse.getEvent(TEST_QNAME3_STRING));
        final Iterator<QName> eventQNames = mockStateAwareResponse.getEventNames();
        assertNotNull("Event names iterator null ?", eventQNames);
        while (eventQNames.hasNext()) {
            QName currentQName = eventQNames.next();
            assertNotNull("Iterator QName entry null ?", currentQName);
            assertTrue("Iterator QName name not one of two or three expected matches ?", currentQName.getLocalPart().equals(TEST_QNAME.getLocalPart()) || 
                    currentQName.getLocalPart().equals(TEST_QNAME2.getLocalPart()) ||
                    currentQName.getLocalPart().equals(TEST_QNAME3_STRING));
        }

        mockStateAwareResponse.setRenderParameter(TEST_PARAM, TEST_PARAM_VALUE);
        mockStateAwareResponse.setRenderParameter(TEST_PARAM2, TEST_PARAM_ARRAYVALUE);
        assertEquals("Portal test render parameter value not as expected ?", TEST_PARAM_VALUE, mockStateAwareResponse.getRenderParameter(TEST_PARAM));
        assertEquals("Portal test render parameter value not as expected ?", TEST_PARAM_ARRAYVALUE[0], mockStateAwareResponse.getRenderParameter(TEST_PARAM2));
        final Iterator<String> renderParameterNames = mockStateAwareResponse.getRenderParameterNames();
        assertNotNull("Render parameter names iterator null ?", renderParameterNames);
        while (renderParameterNames.hasNext()) {
            String currentParameter = renderParameterNames.next();
            assertNotNull("Iterator parameter entry null ?", currentParameter);
            assertTrue("Iterator parameter name not one of two or three expected matches ?", currentParameter.equals(TEST_PARAM) || 
                    currentParameter.equals(TEST_PARAM2) ||
                    currentParameter.equals(TEST_PARAM3));
        }
        final String[] renderParameterValuesParam2 = mockStateAwareResponse.getRenderParameterValues(TEST_PARAM2);
        assertArrayEquals("Render parameter value2 not as expected ?", TEST_PARAM_ARRAYVALUE, renderParameterValuesParam2);
        final Map<String, String[]> renderParametersMap = new LinkedHashMap<>();
        renderParametersMap.put(TEST_PARAM3, TEST_PARAM_ARRAYVALUE2);
        mockStateAwareResponse.setRenderParameters(renderParametersMap);
        assertNull("Portal test render parameter value not cleared as expected ?", mockStateAwareResponse.getRenderParameter(TEST_PARAM));
        assertNull("Portal test render parameter value not cleared as expected ?", mockStateAwareResponse.getRenderParameter(TEST_PARAM2));
        assertEquals("Portal test render parameter map value not as expected ?", renderParametersMap, mockStateAwareResponse.getRenderParameterMap());
    }

    public void testServletWrappingPortletContext() {
        final String TEST_ATTRIBUTE = "TEST_ATTRIBUTE";
        final String TEST_ATTRIBUTE_VALUE = "Attribute_Value_1";
        final String TEST_ATTRIBUTE2 = "TEST_ATTRIBUTE2";
        final String TEST_ATTRIBUTE_VALUE2 = "Attribute_Value_2";
        final String TEST_INITPARAM = "TEST_INITPARAM";
        final String TEST_INITPARAM2 = "TEST_INITPARAM2";

        // Call each constructor in sequence, do some basic checks
        final MockServletContext mockServletContext = new MockServletContext();
        final ServletWrappingPortletContext servletWrappingPortletContext = new ServletWrappingPortletContext(mockServletContext);
        assertNotNull("ServletWrappingPortletContext serverInfo null ?", servletWrappingPortletContext.getServerInfo());
        assertNull("ServletWrappingPortletContext PortletRequestDispatcher not null (illegal prefix) ?", servletWrappingPortletContext.getRequestDispatcher("IllegalPrefix"));
        assertNull("ServletWrappingPortletContext PortletRequestDispatcher not null ?", servletWrappingPortletContext.getRequestDispatcher("/"));
        assertNotNull("ServletWrappingPortletContext resource stream for / null ?", servletWrappingPortletContext.getResourceAsStream("/"));
        assertNull("ServletWrappingPortletContext resource stream for /ThisDoesNotExist not null ?", servletWrappingPortletContext.getResourceAsStream("/ThisDoesNotExist"));
        assertTrue("ServletWrappingPortletContext major version not >= 2 ?", servletWrappingPortletContext.getMajorVersion() >= 2);
        assertTrue("ServletWrappingPortletContext minor version not >= 0 ?", servletWrappingPortletContext.getMajorVersion() >= 0);
        try {
            assertNull("ServletWrappingPortletContext MIME type for / not null ?", servletWrappingPortletContext.getMimeType("/"));
        } catch (NoClassDefFoundError ncdfe) {
            // If compiled with Spring 4.x the MockServletContext has a dependency on javax.activation.  This will cause a runtime failure running under JDK 9+ due to removal
            // of the javax.activation module.  For that reason we will ignore the NoClassDefFoundError failure provided it is for the known FileTypeMap dependency.
            if (!ncdfe.getMessage().contains("javax/activation/FileTypeMap")) {
                fail("Unexpected exception: " + ncdfe);
            }
        }
        assertNotNull("ServletWrappingPortletContext real path for / null ?", servletWrappingPortletContext.getRealPath("/"));
        assertNull("ServletWrappingPortletContext real path for /ThisDoesNotExist not null ?", servletWrappingPortletContext.getRealPath("/ThisDoesNotExist"));
        assertNull("ServletWrappingPortletContext resource paths for / not null ?", servletWrappingPortletContext.getResourcePaths("/"));
        assertNull("ServletWrappingPortletContext resource paths for / null ?", servletWrappingPortletContext.getResourcePaths("/ThisDoesNotExist"));
        try {
            assertNotNull("ServletWrappingPortletContext resource URL for / null ?", servletWrappingPortletContext.getResource("/"));
        } catch (MalformedURLException mue) {
            fail("ServletWrappingPortletContext resource URL for / failed.  Exception: " + mue);
        }
        try {
            assertNull("ServletWrappingPortletContext resource URL for /ThisDoesNotExist not null ?", servletWrappingPortletContext.getResource("/ThisDoesNotExist"));
        } catch (MalformedURLException mue) {
            fail("ServletWrappingPortletContext resource URL for /ThisDoesNotExist failed.  Exception: " + mue);
        }

        assertNull(TEST_ATTRIBUTE + " present before set ?", servletWrappingPortletContext.getAttribute(TEST_ATTRIBUTE));
        assertNull(TEST_ATTRIBUTE2 + " present before set ?", servletWrappingPortletContext.getAttribute(TEST_ATTRIBUTE2));
        servletWrappingPortletContext.setAttribute(TEST_ATTRIBUTE, TEST_ATTRIBUTE_VALUE);
        servletWrappingPortletContext.setAttribute(TEST_ATTRIBUTE2, TEST_ATTRIBUTE_VALUE2);
        assertEquals("Retrieved atribute 1 does not match set value ?", TEST_ATTRIBUTE_VALUE, servletWrappingPortletContext.getAttribute(TEST_ATTRIBUTE));
        assertEquals("Retrieved atribute 2 does not match set value ?", TEST_ATTRIBUTE_VALUE2, servletWrappingPortletContext.getAttribute(TEST_ATTRIBUTE2));
        final Enumeration<String> attributeNames = servletWrappingPortletContext.getAttributeNames();
        int attributeNameCount = 0;
        assertNotNull("Attribute names null after additions ?", attributeNames);
        assertTrue("Attribute names empty after additions ?", attributeNames.hasMoreElements());
        while (attributeNames.hasMoreElements()) {
            attributeNameCount++;
            String currentAttibuteName = attributeNames.nextElement();
            assertTrue("Attribute name not one of two or three expected matches ?", TEST_ATTRIBUTE.equals(currentAttibuteName) || 
                    TEST_ATTRIBUTE2.equals(currentAttibuteName) ||
                    WebUtils.TEMP_DIR_CONTEXT_ATTRIBUTE.equals(currentAttibuteName));
        }
        assertTrue("Atribute names size not 2 or 3 ?", attributeNameCount >= 2 && attributeNameCount <= 3);

        assertNull(TEST_INITPARAM + " present before set ?", servletWrappingPortletContext.getInitParameter(TEST_INITPARAM));
        assertNull(TEST_INITPARAM2 + " present before set ?", servletWrappingPortletContext.getInitParameter(TEST_INITPARAM));
        servletWrappingPortletContext.log("Test logging call");
        servletWrappingPortletContext.log("Test logging call", new Exception("Fake Exception"));
        assertEquals("Default portlet context name not as expected ?", "MockServletContext", servletWrappingPortletContext.getPortletContextName());
        assertFalse("Portlet runtime options not empty before set ?", servletWrappingPortletContext.getContainerRuntimeOptions().hasMoreElements());
    }
    // -------- Continue writing tests here --------------

    static class TestMockMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] bytes;

        public TestMockMultipartFile(String name, String originalFilename, String contentType, byte[] bytes) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            if (bytes != null) {
                this.bytes = Arrays.copyOf(bytes, bytes.length);
            } else {
                this.bytes = null;
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return this.originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return (bytes == null || bytes.length == 0);
        }

        @Override
        public long getSize() {
            return (bytes != null ? bytes.length : 0);
        }

        @Override
        public byte[] getBytes() throws IOException {
            return bytes;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(bytes);
        }

        @Override
        public void transferTo(File file) throws IOException, IllegalStateException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    static class TetMockResourceBundle extends ResourceBundle {

        @Override
        protected Object handleGetObject(String key) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Enumeration<String> getKeys() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class TestMockPrincipal implements Principal {

        @Override
        public String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean implies(Subject subject) {
            return Principal.super.implies(subject);
        }
    }
}
