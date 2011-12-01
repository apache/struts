package org.apache.struts2.xwork2.ognl;

import org.apache.struts2.xwork2.util.reflection.ReflectionContextFactory;
import ognl.Ognl;

import java.util.Map;

public class OgnlReflectionContextFactory implements ReflectionContextFactory {

    public Map createDefaultContext(Object root) {
        return Ognl.createDefaultContext(root);
    }

}
