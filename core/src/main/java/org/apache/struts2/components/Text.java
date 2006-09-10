/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts2.components;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.util.TextUtils;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.OgnlValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render a I18n text message. 
 * 
 * <p/>
 *
 * The message must be in a resource bundle
 * with the same name as the action that it is associated with. In practice
 * this means that you should create a properties file in the same package
 * as your Java class with the same name as your class, but with .properties
 * extension.
 *
 * <p/>
 *
 * If the named message is not found, then the body of the tag will be used as default message.
 * If no body is used, then the name of the message will be used.
 * 
 * <!-- END SNIPPET: javadoc -->
 *
 *
 *
 * <!-- START SNIPPET: params -->
 * 
 * <ul>
 *      <li>name* (String) - the i18n message key</li>
 * </ul>
 * 
 * <!-- END SNIPPET: params -->
 *
 * <p/>
 *
 * Example:
 * <pre>
 * <!-- START SNIPPET: exdescription -->
 * 
 * Accessing messages from a given bundle (the i18n Shop example bundle in the first example) and using bundle defined through the framework in the second example.</p>
 * 
 * <!-- END SNIPPET: exdescription -->
 * </pre>
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
 * @s.tag name="text" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.TextTag"
 * description="Render a I18n text message."
 */
public class Text extends Component implements Param.UnnamedParametric {
    private static final Log LOG = LogFactory.getLog(Text.class);

    protected List values = Collections.EMPTY_LIST;
    protected String actualName;
    protected String name;

    public Text(OgnlValueStack stack) {
        super(stack);
    }

    /**
     *  Name of resource property to fetch
     * @s.tagattribute required="true"
     */
    public void setName(String name) {
        this.name = name;
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
        if (TextUtils.stringSet(body)) {
            defaultMessage = body;
        } else {
            defaultMessage = actualName;
        }
        String msg = null;
        OgnlValueStack stack = getStack();

        for (Iterator iterator = getStack().getRoot().iterator();
             iterator.hasNext();) {
            Object o = iterator.next();

            if (o instanceof TextProvider) {
                TextProvider tp = (TextProvider) o;
                msg = tp.getText(actualName, defaultMessage, values, stack);

                break;
            }
        }

        if (msg != null) {
            try {
                if (getId() == null) {
                    writer.write(msg);
                } else {
                    stack.getContext().put(getId(), msg);
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
            values = new ArrayList(4);
        }

        values.add(value);
    }
}
