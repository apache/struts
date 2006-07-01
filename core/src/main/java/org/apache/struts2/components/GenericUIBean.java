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

import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.struts2.util.ContainUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * Objects provided can be retrieve from within the template via &lt;saf:property value="%{parameters._paramname_}" /&gt;<p/>
 *
 *
 * In the bottom JSP and Velocity samples, two parameters are being passed in to the component. From within the
 * component, they can be accessed as:- <p/>
 * 
 * <b>Freemarker:</b><p/>
 * $parameters.get('key1') and $parameters.get('key2') or $parameters.key1 and $parameters.key2<p/>
 * 
 * <b>Jsp:</b><p/>
 * &lt;saf:property value="%{parameters.key1}" /&gt; and &lt;saf:property value="%{'parameters.key2'}" /&gt; or
 * &lt;saf:property value="%{parameters.get('key1')}" /&gt; and &lt;saf:property value="%{parameters.get('key2')}" /&gt;<p/>
 *
 * Currently, your custom UI components can be written in Velocity, JSP, or Freemarker, and the correct rendering
 * engine will be found based on file extension.<p/>
 *
 * <b>Remember:</b> the value params will always be resolved against the OgnlValueStack so if you mean to pass a
 * string literal to your component, make sure to wrap it in quotes i.e. value="'value1'" otherwise, the the value
 * stack will search for an Object on the stack with a method of getValue1(). (now that i've written this, i'm not
 * entirely sure this is the case. i should verify this manana)<p/>
 * 
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * JSP
 *     &lt;a:component template="/my/custom/component.vm"/&gt;
 *     
 *       or
 *
 *     &lt;a:component template="/my/custom/component.vm"&gt;
 *       &lt;a:param name="key1" value="value1"/&gt;
 *       &lt;a:param name="key2" value="value2"/&gt;
 *     &lt;/a:component&gt;
 *
 * Velocity
 *     #safcomponent( "template=/my/custom/component.vm" )
 *
 *       or
 *
 *     #safcomponent( "template=/my/custom/component.vm" )
 *       #safparam( "name=key1" "value=value1" )
 *       #safparam( "name=key2" "value=value2" )
 *     #end
 *     
 * Freemarker
 *    &lt;@saf.component template="/my/custom/component.ftl" />
 *    
 *      or
 *      
 *    &lt;@saf.component template="/my/custom/component.ftl"&gt;
 *       &lt;@saf.param name="key1" value="%{'value1'}" /&gt;
 *       &lt;@saf.param name="key2" value="%{'value2'}" /&gt;
 *    &lt;/@saf.component&gt;
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
 * @a2.tag name="component" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.ComponentTag"
 * description="Render a custom ui widget"
 */
public class GenericUIBean extends UIBean {
    private final static String TEMPLATE = "empty";

    public GenericUIBean(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public boolean contains(Object obj1, Object obj2) {
        return ContainUtil.contains(obj1, obj2);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
}
