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
package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * The combo box is basically an HTML INPUT of type text and HTML SELECT grouped together to give you a combo box
 * functionality. You can place text in the INPUT control by using the SELECT control or type it in directly in
 * the text field.<p/>
 *
 * In this example, the SELECT will be populated from id=year attribute. Counter is itself an Iterator. It will
 * span from first to last. The population is done via javascript, and requires that this tag be surrounded by a
 * &lt;form&gt;.<p/>
 *
 * Note that unlike the &lt;a:select/&gt; tag, there is no ability to define the individual &lt;option&gt; tags' id attribute
 * or content separately. Each of these is simply populated from the toString() method of the list item. Presumably
 * this is because the select box isn't intended to actually submit useful data, but to assist the user in filling
 * out the text field.<p/>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * JSP:
 * &lt;a:bean name="struts.util.Counter" id="year"&gt;
 *   &lt;a:param name="first" value="text('firstBirthYear')"/&gt;
 *   &lt;a:param name="last" value="2000"/&gt;
 *
 *   &lt;a:combobox label="Birth year" size="6" maxlength="4" name="birthYear" list="#year"/&gt;
 * &lt;/a:bean&gt;
 *
 * Velocity:
 * #tag( ComboBox "label=Birth year" "size=6" "maxlength=4" "name=birthYear" "list=#year" )
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @version $Revision$
 * @since 2.2
 *
 * @a2.tag name="combobox" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.ComboBoxTag"
 * description="Widget that fills a text box from a select"
  */
public class ComboBox extends TextField {
    final public static String TEMPLATE = "combobox";

    protected String list;

    public ComboBox(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (list != null) {
            addParameter("list", findValue(list));
        }
    }

    /**
     * Iteratable source to populate from. If this is missing, the select widget is simply not displayed.
     * @a2.tagattribute required="true"
      */
    public void setList(String list) {
        this.list = list;
    }
}
