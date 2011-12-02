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

package org.apache.struts2.views.velocity.components;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.components.Component;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;

public abstract class AbstractDirective extends Directive {
    public String getName() {
        return "s" + getBeanName();
    }

    public abstract String getBeanName();

    /**
     * All components, unless otherwise stated, are LINE-level directives.
     */
    public int getType() {
        return LINE;
    }

    protected abstract Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res);

    public boolean render(InternalContextAdapter ctx, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        // get the bean
        ValueStack stack = (ValueStack) ctx.get("stack");
        HttpServletRequest req = (HttpServletRequest) stack.getContext().get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse res = (HttpServletResponse) stack.getContext().get(ServletActionContext.HTTP_RESPONSE);
        Component bean = getBean(stack, req, res);
        Container container = (Container) stack.getContext().get(ActionContext.CONTAINER);
        container.inject(bean);
        // get the parameters
        Map params = createPropertyMap(ctx, node);
        bean.copyParams(params);
        //bean.addAllParameters(params);
        bean.start(writer);

        if (getType() == BLOCK) {
            Node body = node.jjtGetChild(node.jjtGetNumChildren() - 1);
            body.render(ctx, writer);
        }

        bean.end(writer, "");
        return true;
    }

    /**
     * create a Map of properties that the user has passed in.  for example,
     * <pre>
     * #xxx("name=hello" "value=world" "template=foo")
     * </pre>
     * would yield a params that contains {["name", "hello"], ["value", "world"], ["template", "foo"]}
     *
     * @param node the Node passed in to the render method
     * @return a Map of the user specified properties
     * @throws org.apache.velocity.exception.ParseErrorException
     *          if the was an error in the format of the property
     */
    protected Map createPropertyMap(InternalContextAdapter contextAdapter, Node node) throws ParseErrorException, MethodInvocationException {
        Map propertyMap;

        int children = node.jjtGetNumChildren();
        if (getType() == BLOCK) {
            children--;
        }

        // Velocity supports an on-the-fly Map-definition syntax that leads
        // to more readable and faster code:
        //
        //    #url({'id':'url', 'action':'MyAction'})
        //
        // We support this syntax by checking for a single Map argument
        // to any directive and using that as the property map instead
        // of building one from individual name-value pair strings.
        Node firstChild = null;
        Object firstValue = null;
        if(children == 1
           && null != (firstChild = node.jjtGetChild(0))
           && null != (firstValue = firstChild.value(contextAdapter))
           && firstValue instanceof Map) {
            propertyMap = (Map)firstValue;
        } else {
            propertyMap = new HashMap();

            for (int index = 0, length = children; index < length; index++) {
                this.putProperty(propertyMap, contextAdapter, node.jjtGetChild(index));
            }
        }

        return propertyMap;
    }

    /**
     * adds a given Node's key/value pair to the propertyMap.  For example, if this Node contained the value "rows=20",
     * then the key, rows, would be added to the propertyMap with the String value, 20.
     *
     * @param propertyMap a params containing all the properties that we wish to set
     * @param node        the parameter to set expressed in "name=value" format
     */
    protected void putProperty(Map propertyMap, InternalContextAdapter contextAdapter, Node node) throws ParseErrorException, MethodInvocationException {
        // node.value uses the StrutsValueStack to evaluate the directive's value parameter
        String param = node.value(contextAdapter).toString();

        int idx = param.indexOf("=");

        if (idx != -1) {
            String property = param.substring(0, idx);

            String value = param.substring(idx + 1);
            propertyMap.put(property, value);
        } else {
            throw new ParseErrorException("#" + this.getName() + " arguments must include an assignment operator!  For example #tag( Component \"template=mytemplate\" ).  #tag( TextField \"mytemplate\" ) is illegal!");
        }
    }
}
