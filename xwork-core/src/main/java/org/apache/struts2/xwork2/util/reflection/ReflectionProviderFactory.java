package org.apache.struts2.xwork2.util.reflection;

import org.apache.struts2.xwork2.ActionContext;

public class ReflectionProviderFactory {

    public static ReflectionProvider getInstance() {
        return ActionContext.getContext().getContainer().getInstance(ReflectionProvider.class);
    }
}
