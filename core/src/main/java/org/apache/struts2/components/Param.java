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
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsException;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.Writer;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>This tag can be used to parameterize other tags.<br>
 * The include tag and bean tag are examples of such tags.
 * </p>
 *
 * <p>
 * The parameters can be added with or without a name as key.
 * If the tag provides a name attribute the parameters are added using the
 * {@link Component#addParameter(String, Object) addParamter} method.
 * For unnamed parameters the Tag must implement the {@link UnnamedParametric} interface defined in
 * this class (e.g. The TextTag does this).
 * </p>
 *
 * <p>
 * This tag has the following two paramters.
 * </p>
 * <!-- START SNIPPET: params -->
 * <ul>
 *      <li>name (String) - the name of the parameter</li>
 *      <li>value (Object) - the value of the parameter</li>
 *      <li>suppressEmptyParameters (boolean) - whether to suppress this parameter if empty</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 * <p>
 * <b>Note:</b>
 * When you declare the param tag, the value can be defined in either a <tt>value</tt> attribute or
 * as text between the start and end tag. Struts behaves a bit different according to these two situations.
 * This is best illustrated using an example:
 * <br>&lt;param name="color"&gt;blue&lt;/param&gt; &lt;-- (A) --&gt;
 * <br>&lt;param name="color" value="blue"/&gt; &lt;-- (B) --&gt;
 * <br>In the first situation (A) the value would be evaluated to the stack as a <tt>java.lang.String</tt> object.
 * And in situation (B) the value would be evaluated to the stack as a <tt>java.lang.Object</tt> object.
 * <br>For more information see <a href="https://issues.apache.org/jira/browse/WW-808">WW-808</a>.
 * </p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p><b>Examples</b></p>
 * <!-- START SNIPPET: example -->
 * <pre>
 * &lt;ui:component&gt;
 *  &lt;ui:param name="key"     value="[0]"/&gt;
 *  &lt;ui:param name="value"   value="[1]"/&gt;
 *  &lt;ui:param name="context" value="[2]"/&gt;
 * &lt;/ui:component&gt;
 * </pre>
 *
 * <p>
 * Whether to suppress empty parameters:
 * </p>
 *
 * <pre>
 * &lt;s:url action="eventAdd"&gt;
 *   &lt;s:param name="bean.searchString" value="%{bean.searchString}" /&gt;
 *   &lt;s:param name="bean.filter" value="%{bean.filter}" /&gt;
 *   &lt;s:param name="bean.pageNum" value="%{pager.pageNumber}" suppressEmptyParameters="true" /&gt;
 * &lt;/s:url&gt;
 * </pre>
 * <!-- END SNIPPET: example -->
 * <p>
 * <!-- START SNIPPET: exampledescription -->
 * where the key will be the identifier and the value the result of an OGNL expression run against the current
 * ValueStack.
 * <!-- END SNIPPET: exampledescription -->
 * </p>
 * <p>
 * This second example demonstrates how the text tag can use parameters from this param tag.
 * </p>
 * <!-- START SNIPPET: example2 -->
 * <pre>
 * &lt;s:text name="cart.total.cost"&gt;
 *     &lt;s:param value="#session.cartTotal"/&gt;
 * &lt;/s:text&gt;
 * </pre>
 * <!-- END SNIPPET: example2 -->
 *
 *
 * @see Include
 * @see Bean
 * @see Text
 *
 */
@StrutsTag(name="param", tldTagClass="org.apache.struts2.views.jsp.ParamTag", description="Parametrize other tags")
public class Param extends Component {

    protected String name;
    protected String value;
    protected boolean suppressEmptyParameters;

    public Param(ValueStack stack) {
        super(stack);
    }

    public boolean end(Writer writer, String body) {
        Component component = findAncestor(Component.class);
        if (value != null) {
            if (component instanceof UnnamedParametric) {
                ((UnnamedParametric) component).addParameter(findValue(value));
            } else {
                String translatedName = findString(this.name);

                if (translatedName == null) {
                    throw new StrutsException("No name found for following expression: " + this.name);
                }

                boolean evaluated = !translatedName.equals(this.name);
                boolean reevaluate = !evaluated || isAcceptableExpression(translatedName);
                if (!reevaluate) {
                    throw new StrutsException("Excluded or not accepted name found: " + translatedName);
                }

                Object foundValue = findValue(this.value);
                if (suppressEmptyParameters) {
                    if (foundValue != null && StringUtils.isNotBlank(foundValue.toString())) {
                        component.addParameter(translatedName, foundValue);
                    } else {
                        component.addParameter(translatedName, null);
                    }
                } else if (foundValue == null || StringUtils.isBlank(foundValue.toString())) {
                    component.addParameter(translatedName, "");
                } else {
                    component.addParameter(translatedName, foundValue);
                }
            }
        } else {
            if (component instanceof UnnamedParametric) {
                ((UnnamedParametric) component).addParameter(body);
            } else {
                if (!(suppressEmptyParameters && StringUtils.isBlank(body))) {
                    component.addParameter(findString(name), body);
                } else {
                    component.addParameter(findString(name), null);
                }
            }
        }

        return super.end(writer, "");
    }

    @Override
    public boolean usesBody() {
        return true;
    }

    @StrutsTagAttribute(description="Name of Parameter to set")
    public void setName(String name) {
        this.name = name;
    }

    @StrutsTagAttribute(description="Value expression for Parameter to set", defaultValue="The value of evaluating provided name against stack")
    public void setValue(String value) {
        this.value = value;
    }
    
    @StrutsTagAttribute(description="Whether to suppress empty parameters", type="Boolean", defaultValue="false")
    public void setSuppressEmptyParameters(boolean suppressEmptyParameters) {
        this.suppressEmptyParameters = suppressEmptyParameters;
    }
    
    /**
     * <p>
     * Tags can implement this to support nested param tags without the <tt>name</tt> attribute.
     * </p>
     * <p>
     * The {@link Text TextTag} uses this approach. For unnamed parameters an example is given in the class
     * javadoc for {@link Param ParamTag}.
     * </p>
     */
    public interface UnnamedParametric {

        /**
         * Adds the given value as a parameter to the outer tag.
         * @param value  the value
         */
        void addParameter(Object value);
    }

}
