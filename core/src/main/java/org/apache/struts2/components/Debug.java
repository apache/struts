package org.apache.struts2.components;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsException;

import com.opensymphony.xwork2.util.OgnlUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>Renders an debug tag.</P>
 *
 * The debug information contain mostly stack information:
 * <ul>
 *    <li>Value Stack Contents</li>
 *    <li>Stack Context</li>
 * </ul>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;ww:debug/&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @s.tag name="debug" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.DebugTag"
 * description="Render debug tag"
  */
public class Debug extends UIBean {
    public static final String TEMPLATE = "debug";

    public Debug(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        ValueStack stack = getStack();
        Iterator iter = stack.getRoot().iterator();
        List stackValues = new ArrayList(stack.getRoot().size());
        while (iter.hasNext()) {
            Object o = iter.next();
            Map values;
            try {
                values = OgnlUtil.getBeanMap(o);
            } catch (Exception e) {
                throw new StrutsException("Caught an exception while getting the property values of " + o, e);
            }
            stackValues.add(new DebugMapEntry(o.getClass().getName(), values));
        }

        addParameter("stackValues", stackValues);

        return result;
    }

    private class DebugMapEntry implements Map.Entry {
        private Object key;
        private Object value;

        DebugMapEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object newVal) {
            Object oldVal = value;
            value = newVal;
            return oldVal;
        }
    }

}
