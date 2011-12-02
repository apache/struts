package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;
import ognl.Ognl;

import java.util.Map;

public class OgnlReflectionContextFactory implements ReflectionContextFactory {

    public Map createDefaultContext(Object root) {
        return Ognl.createDefaultContext(root);
    }

}
