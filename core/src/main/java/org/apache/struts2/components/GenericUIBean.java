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

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.util.ContainUtil;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Renders an custom UI widget using the specified templates. Additional objects can be passed in to the template
 * using the param tags.<p/>
 *
 * <b>Freemarker:</b><p/>
 * Objects provided can be retrieve from within the template via $parameters._paramname_.<p/>
 *
 * <b>Jsp:</b><p/>
 * Objects provided can be retrieve from within the template via &lt;s:property value="%{parameters._paramname_}" /&gt;<p/>
 *
 *
 * In the bottom JSP and Velocity samples, two parameters are being passed in to the component. From within the
 * component, they can be accessed as:- <p/>
 *
 * <b>Freemarker:</b><p/>
 * $parameters.get('key1') and $parameters.get('key2') or $parameters.key1 and $parameters.key2<p/>
 *
 * <b>Jsp:</b><p/>
 * &lt;s:property value="%{parameters.key1}" /&gt; and &lt;s:property value="%{'parameters.key2'}" /&gt; or
 * &lt;s:property value="%{parameters.get('key1')}" /&gt; and &lt;s:property value="%{parameters.get('key2')}" /&gt;<p/>
 *
 * Currently, your custom UI components can be written in Velocity, JSP, or Freemarker, and the correct rendering
 * engine will be found based on file extension.<p/>
 *
 * <b>Remember:</b> the value params will always be resolved against the ValueStack so if you mean to pass a
 * string literal to your component, make sure to wrap it in single quotes i.e. value="'value1'" (note the opening "' and closing '" otherwise, the the value
 * stack will search for an Object on the stack with a method of getValue1().<p/>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * JSP
 *     &lt;s:component template="/my/custom/component.vm"/&gt;
 *
 *       or
 *
 *     &lt;s:component template="/my/custom/component.vm"&gt;
 *       &lt;s:param name="key1" value="value1"/&gt;
 *       &lt;s:param name="key2" value="value2"/&gt;
 *     &lt;/s:component&gt;
 *
 * Velocity
 *     #s-component( "template=/my/custom/component.vm" )
 *
 *       or
 *
 *     #s-component( "template=/my/custom/component.vm" )
 *       #s-param( "name=key1" "value=value1" )
 *       #s-param( "name=key2" "value=value2" )
 *     #end
 *
 * Freemarker
 *    &lt;@s..component template="/my/custom/component.ftl" />
 *
 *      or
 *
 *    &lt;@s..component template="/my/custom/component.ftl"&gt;
 *       &lt;@s..param name="key1" value="%{'value1'}" /&gt;
 *       &lt;@s..param name="key2" value="%{'value2'}" /&gt;
 *    &lt;/@s..component&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <p/>
 *
 * <b>NOTE:</b>
 * <!-- START SNIPPET: note -->
 *
 * If Jsp is used as the template, the jsp template itself must lie within the
 * webapp itself and not the classpath. Unlike Freemarker or Velocity, JSP template
 * could not be picked up from the classpath.
 *
 * <!-- END SNIPPET: note -->
 *
 */
@StrutsTag(name="component", tldTagClass="org.apache.struts2.views.jsp.ui.ComponentTag", description="Render a custom ui widget")
public class GenericUIBean extends UIBean {
    private final static String TEMPLATE = "empty";

    public GenericUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public boolean contains(Object obj1, Object obj2) {
        return ContainUtil.contains(obj1, obj2);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
}
