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

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.Writer;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>Instantiates a class that conforms to the JavaBeans specification. This tag has a body which can contain
 * a number of {@link Param} elements to set any mutator methods on that class.</p>
 * <p/>
 * <p>If the var attribute is set on the BeanTag, it will place the instantiated bean into the
 * stack's Context.</p>
 * <p/>
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: params -->
 * <ul>
 *      <li>var - the stack's context name (if supplied) that the created bean will be store under</li>
 *      <li>name* - the class name of the bean to be instantiated (must respect JavaBean specification)</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <p>Examples:</p>
 * <p/>
 * <pre>
 * <!-- START SNIPPET: examples -->
 * &lt;-- in freemarker form --&gt;
 * [@s.bean name="org.apache.struts2.example.counter.SimpleCounter" var="counter"]
 *   [s:param name="foo" value="BAR"/]
 *   The value of foo is : [s:property value="foo"/], when inside the bean tag.<br />
 * [/s:bean]
 *
 * &lt;-- in jsp form --&gt;
 * &lt;s:bean name="org.apache.struts2.example.counter.SimpleCounter" var="counter"&gt;
 *   &lt;s:param name="foo" value="BAR" /&gt;
 *   The value of foot is : &lt;s:property value="foo"/&gt;, when inside the bean tag &lt;br /&gt;
 * &lt;/s:bean&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 * <p/>
 *
 * <!-- START SNIPPET: examplesdescription -->
 * <p>This example instantiates a bean called SimpleCounter and sets the foo property (setFoo('BAR')). The
 * SimpleCounter object is then pushed onto the Valuestack, which means that we can call its accessor methods (getFoo())
 * with the Property tag and get their values.</p>
 * <p/>
 * <p>In the above example, the id has been set to a value of <i>counter</i>. This means that the SimpleCounter class
 * will be placed into the stack's context. You can access the SimpleCounter class using a Struts tag:</p>
 * <p/>
 * <pre>
 * &lt;-- jsp form --&gt;
 * &lt;s:property value="#counter" /&gt;
 *
 * &lt;-- freemarker form --&gt;
 * [s:property value="#counter.foo"/]
 * </pre>
 * <p/>
 * <p>In the property tag example, the <i>#</i> tells Ognl to search the context for the SimpleCounter class which has
 * an id(key) of <i>counter</i></p>
 * <!-- END SNIPPET: examplesdescription -->
 *
 * @see Param
 */
@StrutsTag(name = "bean", tldTagClass = "org.apache.struts2.views.jsp.BeanTag",
        description = "Instantiate a JavaBean and place it in the context")
public class Bean extends ContextBean {
    protected static final Logger LOG = LoggerFactory.getLogger(Bean.class);

    protected Object bean;
    protected String name;
    protected ObjectFactory objectFactory;
    protected ReflectionProvider reflectionProvider;

    public Bean(ValueStack stack) {
        super(stack);
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        ValueStack stack = getStack();

        try {
            String beanName = findString(name, "name", "Bean name is required. Example: com.acme.FooBean or proper Spring bean ID");
            bean = objectFactory.buildBean(beanName, stack.getContext(), false);
        } catch (Exception e) {
            LOG.error("Could not instantiate bean", e);
            return false;
        }

        // push bean on stack
        stack.push(bean);

        // store for reference later
        putInContext(bean);

        return result;
    }

    public boolean end(Writer writer, String body) {
        ValueStack stack = getStack();
        stack.pop();

        return super.end(writer, body);
    }

    public void addParameter(String key, Object value) {
        reflectionProvider.setProperty(key, value, bean, getStack().getContext());
    }

    @StrutsTagAttribute(description = "The class name of the bean to be instantiated (must respect JavaBean specification)", required = true)
    public void setName(String name) {
        this.name = name;
    }

}
