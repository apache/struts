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
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.util.TextProviderHelper;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * Render a I18n text message.
 * </p>
 *
 * <p>
 * The message must be in a resource bundle
 * with the same name as the action that it is associated with. In practice
 * this means that you should create a properties file in the same package
 * as your Java class with the same name as your class, but with .properties
 * extension.
 * </p>
 *
 * <p>
 * If the named message is not found in a property file, then the body of the
 * tag will be used as default message. If no body is used, then the stack can
 * be searched, and if a value is returned, it will written to the output.
 * If no value is found on the stack, the key of the message will be written out.
 * </p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *      <li>name* (String) - the i18n message key</li>
 *      <li>escapeHtml (Boolean) - Escape HTML. Defaults to false</li>
 *      <li>escapeJavaScript (Boolean) - Escape JavaScript. Defaults to false</li>
 *      <li>escapeXml (Boolean) - Escape XML. Defaults to false</li>
 *      <li>escapeCsv (Boolean) - Escape CSV. Defaults to false</li>
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <p>
 * Example:
 * </p>
 *
 * <!-- START SNIPPET: exdescription -->
 * <p>Accessing messages from a given bundle (the i18n Shop example bundle in the first example) and using bundle defined through the framework in the second example.</p>
 * <!-- END SNIPPET: exdescription -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;!-- First Example --&gt;
 * &lt;s:i18n name="struts.action.test.i18n.Shop"&gt;
 *     &lt;s:text name="main.title"/&gt;
 * &lt;/s:i18n&gt;
 *
 * &lt;!-- Second Example --&gt;
 * &lt;s:text name="main.title" /&gt;
 *
 * &lt;!-- Third Examlpe --&gt;
 * &lt;s:text name="i18n.label.greetings"&gt;
 *    &lt;s:param &gt;Mr Smith&lt;/s:param&gt;
 * &lt;/s:text&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 *
 * <pre>
 * <!-- START SNIPPET: i18nExample -->
 *
 * &lt;-- Fourth Example --&gt;
 * &lt;s:text name="some.key" /&gt;
 *
 * &lt;-- Fifth Example --&gt;
 * &lt;s:text name="some.invalid.key" &gt;
 *    The Default Message That Will Be Displayed
 * &lt;/s:text&gt;
 *
 * <!-- END SNIPPET: i18nExample -->
 * </pre>
 *
 * @see Param
 *
 */
@StrutsTag(
    name="text",
    tldTagClass="org.apache.struts2.views.jsp.TextTag",
    description="Render a I18n text message")
public class Text extends ContextBean implements Param.UnnamedParametric {

    private static final Logger LOG = LogManager.getLogger(Text.class);

    protected List<Object> values = Collections.emptyList();
    protected String actualName;
    protected String name;
    @Deprecated
    protected String searchStack;
    private boolean escapeHtml = false;
    private boolean escapeJavaScript = false;
    private boolean escapeXml = false;
    private boolean escapeCsv = false;

    public Text(ValueStack stack) {
        super(stack);
    }

    @StrutsTagAttribute(description = "Name of resource property to fetch", required = true)
    public void setName(String name) {
        this.name = name;
    }

    @Deprecated
    @StrutsTagAttribute(description="Search the stack if property is not found on resources", type = "Boolean", defaultValue = "false")
    public void setSearchValueStack(String searchStack) {
        this.searchStack = searchStack;
    }

    @StrutsTagAttribute(description="Whether to escape HTML", type="Boolean", defaultValue="false")
    public void setEscapeHtml(boolean escape) {
        this.escapeHtml = escape;
    }
    
    @StrutsTagAttribute(description="Whether to escape Javascript", type="Boolean", defaultValue="false")
    public void setEscapeJavaScript(boolean escapeJavaScript) {
        this.escapeJavaScript = escapeJavaScript;
    }

    @StrutsTagAttribute(description="Whether to escape XML", type="Boolean", defaultValue="false")
    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

    @StrutsTagAttribute(description="Whether to escape CSV (useful to escape a value for a column)", type="Boolean", defaultValue="false")
    public void setEscapeCsv(boolean escapeCsv) {
        this.escapeCsv = escapeCsv;
    }

    public boolean usesBody() {
        // overriding this to true such that EVAL_BODY_BUFFERED is return and
        // bodyContent will be valid hence, text between start & end tag will
        // be honoured as default message (WW-1268)
        return true;
    }

    public boolean end(Writer writer, String body) {
        actualName = findString(name, "name", "You must specify the i18n key. Example: welcome.header");
        String defaultMessage;
        if (StringUtils.isNotEmpty(body)) {
            defaultMessage = body;
        } else {
            defaultMessage = actualName;
        }

        Boolean doSearchStack = false;
        if (searchStack != null) {
            Object value = findValue(searchStack, Boolean.class);
            doSearchStack = value != null ? (Boolean) value : false;
        }

        String msg = TextProviderHelper.getText(actualName, defaultMessage, values, getStack(), doSearchStack);

        if (msg != null) {
            try {
                if (getVar() == null) {
                    writer.write(prepare(msg));
                } else {
                    putInContext(msg);
                }
            } catch (IOException e) {
                LOG.error("Could not write out Text tag", e);
            }
        }

        return super.end(writer, "");
    }

    public void addParameter(String key, Object value) {
        addParameter(value);
    }

    public void addParameter(Object value) {
        if (values.isEmpty()) {
            values = new ArrayList<>(4);
        }

        values.add(value);
    }

    private String prepare(String value) {
        String result = value;
        if (escapeHtml) {
            result = StringEscapeUtils.escapeHtml4(result);
        }
        if (escapeJavaScript) {
            result = StringEscapeUtils.escapeEcmaScript(result);
        }
        if (escapeXml) {
            result = StringEscapeUtils.escapeXml(result);
        }
        if (escapeCsv) {
            result = StringEscapeUtils.escapeCsv(result);
        }

        return result;
    }
}
