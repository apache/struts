package com.opensymphony.webwork.views.velocity.components;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDirective extends Directive {
    public String getName() {
        return "ww" + getBeanName();
    }

    public abstract String getBeanName();

    /**
     * All components, unless otherwise stated, are LINE-level directives.
     */
    public int getType() {
        return LINE;
    }

    protected abstract Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res);

    public boolean render(InternalContextAdapter ctx, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        // get the bean
        OgnlValueStack stack = (OgnlValueStack) ctx.get("stack");
        HttpServletRequest req = (HttpServletRequest) stack.getContext().get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse res = (HttpServletResponse) stack.getContext().get(ServletActionContext.HTTP_RESPONSE);
        Component bean = getBean(stack, req, res);

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
        Map propertyMap = new HashMap();

        int children = node.jjtGetNumChildren();
        if (getType() == BLOCK) {
            children--;
        }

        for (int index = 0, length = children; index < length; index++) {
            this.putProperty(propertyMap, contextAdapter, node.jjtGetChild(index));
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
        // node.value uses the WebWorkValueStack to evaluate the directive's value parameter
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
