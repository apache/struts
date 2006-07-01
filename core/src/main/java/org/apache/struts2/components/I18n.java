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

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.LocaleProvider;
import com.opensymphony.xwork.TextProviderSupport;
import com.opensymphony.xwork.util.LocalizedTextUtil;
import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Writer;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * Gets a resource bundle and place it on the value stack. This allows
 * the text tag to access messages from any bundle, and not just the bundle
 * associated with the current action.
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * <p/>
 * 
 * <!-- START SNIPPET: params-->
 * 
 * <ul>
 * 		<li>name* - the resource bundle's name (eg foo/bar/customBundle)</li>
 * </ul>
 * 
 * <!-- END SNIPPET: params -->
 * 
 * <p/>
 * 
 * Example:
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 * &lt;a:i18n name="myCustomBundle"&gt;
 *    The i18n value for key aaa.bbb.ccc in myCustomBundle is &lt;a:property value="text('aaa.bbb.ccc')" /&gt;
 * &lt;/a:i18n&gt;
 * 
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * 
 * <pre>
 * <!-- START SNIPPET: i18nExample -->
 * 
 * &lt;a:i18n name="some.package.bundle" &gt;
 *      &lt;a:text name="some.key" /&gt;
 * &lt;/a:i18n&gt;
 * 
 * <!-- END SNIPPET: i18nExample -->
 * </pre>
 * 
 * @a2.tag name="i18n" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.I18nTag"
 * description="Get a resource bundle and place it on the value stack"
 */
public class I18n extends Component {
    private static final Log LOG = LogFactory.getLog(I18n.class);

    protected boolean pushed;
    protected String name;

    public I18n(OgnlValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        try {
            String name = this.findString(this.name, "name", "Resource bundle name is required. Example: foo or foo_en");
            ResourceBundle bundle = (ResourceBundle) findValue("texts('" + name + "')");

            if (bundle == null) {
                bundle = LocalizedTextUtil.findResourceBundle(name, (Locale) getStack().getContext().get(ActionContext.LOCALE));
            }

            if (bundle != null) {
                final Locale locale = (Locale) getStack().getContext().get(ActionContext.LOCALE);
                getStack().push(new TextProviderSupport(bundle, new LocaleProvider() {
                    public Locale getLocale() {
                        return locale;
                    }
                }));
                pushed = true;
            }
        } catch (Exception e) {
            String msg = "Could not find the bundle " + name;
            LOG.error(msg, e);
            throw new RuntimeException(msg);
        }

        return result;
    }

    public boolean end(Writer writer, String body) {
        if (pushed) {
            getStack().pop();
        }

        return super.end(writer, body);
    }

    /**
     * Name of ressource bundle to use (eg foo/bar/customBundle)
     * @a2.tagattribute required="true" default="String"
     */
    public void setName(String name) {
        this.name = name;
    }
}
