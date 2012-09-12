/*
 * $Id$
 *
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

package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;


/**
 */
public abstract class AbstractUITagTest extends AbstractTagTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractUITagTest.class);

    static final String FREEMARKER_ERROR_EXPECTATION = "Java backtrace for programmers:";

    /**
     * Simple helper class for generic tag property testing mechanism. Basically it holds a property name, a property
     * value and an output to be expected in tag output when property was accordingly set.
     *
     * @author <a href="mailto:gielen@it-neering.net">Rene Gielen</a>
     */
    public class PropertyHolder {
        String name, value, expectation;

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public String getExpectation() {
            return expectation;
        }

        /**
         * Construct simple holder with default expectation.
         *
         * @param name  The property name to use.
         * @param value The property value to set.
         * @see #PropertyHolder(String, String, String)
         */
        public PropertyHolder(String name, String value) {
            this(name, value, null);
        }

        /**
         * Construct property holder.
         *
         * @param name        The property name to use.
         * @param value       The property value to set.
         * @param expectation The expected String to occur in tag output caused by setting given tag property. If
         *                    <tt>null</tt>, will be set to <pre>name + "=\"" + value + "\"</pre>.
         */
        public PropertyHolder(String name, String value, String expectation) {
            this.name = name;
            this.value = value;
            if (expectation != null) {
                this.expectation = expectation;
            } else {
                this.expectation = name + "=\"" + value + "\"";
            }
        }

        /**
         * Convenience method for easily adding anonymous constructed instance to a given map, with {@link #getName()}
         * as key.
         *
         * @param map The map to place this instance in.
         */
        public void addToMap(Map<String, PropertyHolder> map) {
            if (map != null) {
                map.put(this.name, this);
            }
        }
    }

    /**
     * Simple Helper for setting bean properties. Although BeanUtils from oscore should provide bean property setting
     * functionality, it does not work (at least with my JDK 1.5.0_05), failing in jdk's PropertyDescriptor constructor.
     * This implementation works safely in any case, and does not add dependency on commons-beanutils for building.
     * TODO: Check how we can remove this crap again.
     *
     * @author <a href="mailto:gielen@it-neering.net">Rene Gielen</a>
     * @deprecated use BeanUtils#setProperty
     */
    public class BeanHelper {
        Object bean;

        public BeanHelper(Object bean) {
            this.bean = bean;
        }

        public void set(String name, Object value) throws IllegalAccessException, InvocationTargetException {
            BeanUtils.setProperty(this.bean, name, value);
        }

    }

    /**
     * Initialize a map of {@link PropertyHolder} for generic tag property testing. Will be used when calling {@link
     * #verifyGenericProperties(org.apache.struts2.views.jsp.ui.AbstractUITag, String, String[])} as properties to
     * verify.<p/> This implementation defines testdata for all common AbstractUITag properties and may be overridden in
     * subclasses.
     *
     * @return A Map of PropertyHolders values bound to {@link org.apache.struts2.views.jsp.AbstractUITagTest.PropertyHolder#getName()}
     *         as key.
     */
    protected Map<String, PropertyHolder> initializedGenericTagTestProperties() {
        Map<String, PropertyHolder> result = new HashMap<String, PropertyHolder>();
        new PropertyHolder("name", "someName").addToMap(result);
        new PropertyHolder("id", "someId").addToMap(result);
        new PropertyHolder("cssClass", "cssClass1", "class=\"cssClass1\"").addToMap(result);
        new PropertyHolder("cssStyle", "cssStyle1", "style=\"cssStyle1\"").addToMap(result);
        new PropertyHolder("title", "someTitle").addToMap(result);
        new PropertyHolder("disabled", "true", "disabled=\"disabled\"").addToMap(result);
        //new PropertyHolder("label", "label", "label=\"label\"").addToMap(result);
        //new PropertyHolder("required", "someTitle").addToMap(result);
        new PropertyHolder("tabindex", "99").addToMap(result);
        new PropertyHolder("value", "someValue").addToMap(result);
        new PropertyHolder("onclick", "onclick1").addToMap(result);
        new PropertyHolder("ondblclick", "ondblclick1").addToMap(result);
        new PropertyHolder("onmousedown", "onmousedown1").addToMap(result);
        new PropertyHolder("onmouseup", "onmouseup1").addToMap(result);
        new PropertyHolder("onmouseover", "onmouseover1").addToMap(result);
        new PropertyHolder("onmousemove", "onmousemove1").addToMap(result);
        new PropertyHolder("onmouseout", "onmouseout1").addToMap(result);
        new PropertyHolder("onfocus", "onfocus1").addToMap(result);
        new PropertyHolder("onblur", "onblur1").addToMap(result);
        new PropertyHolder("onkeypress", "onkeypress1").addToMap(result);
        new PropertyHolder("onkeydown", "onkeydown1").addToMap(result);
        new PropertyHolder("onkeyup", "onkeyup1").addToMap(result);
        new PropertyHolder("onclick", "onclick1").addToMap(result);
        new PropertyHolder("onselect", "onchange").addToMap(result);
        return result;
    }

    /**
     * Do a generic verification that setting certain properties on a tag causes expected output regarding this
     * property. In most cases you would not call this directly, instead use {@link
     * #verifyGenericProperties(org.apache.struts2.views.jsp.ui.AbstractUITag, String, String[])}.
     *
     * @param tag              The fresh created tag instance to test.
     * @param theme            The theme to use. If <tt>null</tt>, use configured default theme.
     * @param propertiesToTest Map of {@link PropertyHolder}s, defining properties to test.
     * @param exclude          Names of properties to exclude from particular test.
     * @throws Exception
     */
    public void verifyGenericProperties(AbstractUITag tag, String theme, Map<String, PropertyHolder> propertiesToTest, String[] exclude) throws Exception {
        if (tag != null && propertiesToTest != null) {
            List excludeList;
            if (exclude != null) {
                excludeList = Arrays.asList(exclude);
            } else {
                excludeList = Collections.EMPTY_LIST;
            }

            tag.setPageContext(pageContext);
            if (theme != null) {
                tag.setTheme(theme);
            }

            for (PropertyHolder propertyHolder : propertiesToTest.values()) {
                if (!excludeList.contains(propertyHolder.getName())) {
                    BeanUtils.setProperty(tag, propertyHolder.getName(), propertyHolder.getValue());
                }
            }
            tag.doStartTag();
            tag.doEndTag();
            String writerString = normalize(writer.toString(), true);
            if (LOG.isInfoEnabled()) {
                LOG.info("AbstractUITagTest - [verifyGenericProperties]: Tag output is " + writerString);
            }

            assertFalse("Freemarker error detected in tag output: " + writerString, writerString.contains(normalize(FREEMARKER_ERROR_EXPECTATION)));

            for (PropertyHolder propertyHolder : propertiesToTest.values()) {
                if (!excludeList.contains(propertyHolder.getName())) {
                    assertTrue("Expected to find: " + propertyHolder.getExpectation() + " in resulting String: " + writerString, writerString.contains(propertyHolder.getExpectation()));
                }
            }
        }
    }

    /**
     * Do a generic verification that setting certain properties on a tag causes expected output regarding this
     * property. Which properties to test with which expectations will be determined by the Map retrieved by {@link #initializedGenericTagTestProperties()}.
     *
     * @param tag     The fresh created tag instance to test.
     * @param theme   The theme to use. If <tt>null</tt>, use configured default theme.
     * @param exclude Names of properties to exclude froObjectm particular test.
     * @throws Exception
     */
    public void verifyGenericProperties(AbstractUITag tag, String theme, String[] exclude) throws Exception {
        verifyGenericProperties(tag, theme, initializedGenericTagTestProperties(), exclude);
    }

    /**
     * Attempt to verify the contents of this.writer against the contents of the resource specified.  verify() performs a
     * trim on both ends
     *
     * @param resource the HTML snippet that we want to validate against
     * @throws Exception if the validation failed
     */
    public void verifyResource(String resource) throws Exception {
        verify(this.getClass().getResource(resource));
    }

    /**
     * Attempt to verify the contents of this.writer against the contents of the URL specified.  verify() performs a
     * trim on both ends
     *
     * @param url the HTML snippet that we want to validate against
     * @throws Exception if the validation failed
     */
    public void verify(URL url) throws Exception {
        if (url == null) {
            fail("unable to verify a null URL");
        } else if (this.writer == null) {
            fail("AbstractJspWriter.writer not initialized.  Unable to verify");
        }

        StringBuilder buffer = new StringBuilder(128);
        InputStream in = url.openStream();
        byte[] buf = new byte[4096];
        int nbytes;

        while ((nbytes = in.read(buf)) > 0) {
            buffer.append(new String(buf, 0, nbytes));
        }

        in.close();

        /**
         * compare the trimmed values of each buffer and make sure they're equivalent.  however, let's make sure to
         * normalize the strings first to account for line termination differences between platforms.
         */
        String writerString = normalize(writer.toString(), true);
        String bufferString = normalize(buffer.toString(), true);

        assertEquals(bufferString, writerString);
    }

    protected void setUp() throws Exception {
        super.setUp();

        ServletActionContext.setServletContext(pageContext.getServletContext());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.setContext(null);
    }

    /**
     * normalizes a string so that strings generated on different platforms can be compared.  any group of one or more
     * space, tab, \r, and \n characters are converted to a single space character
     *
     * @param obj the object to be normalized.  normalize will perform its operation on obj.toString().trim() ;
     * @return the normalized string
     */
    public static String normalize(Object obj) {
        return normalize(obj, false);
    }

    /**
     * normalizes a string so that strings generated on different platforms can be compared.  any group of one or more
     * space, tab, \r, and \n characters are converted to a single space character
     *
     * @param obj         the object to be normalized.  normalize will perform its operation on obj.toString().trim() ;
     * @param appendSpace
     * @return the normalized string
     */
    public static String normalize(Object obj, boolean appendSpace) {
        StringTokenizer st = new StringTokenizer(obj.toString().trim(), " \t\r\n");
        StringBuilder buffer = new StringBuilder(128);

        while (st.hasMoreTokens()) {
            buffer.append(st.nextToken());

            /*
            if (appendSpace && st.hasMoreTokens()) {
                buffer.append("");
            }
            */
        }

        return buffer.toString();
    }
}
