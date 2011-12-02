package com.opensymphony.xwork2.util.reflection;

import com.opensymphony.xwork2.ActionContext;

public class ReflectionProviderFactory {

    public static ReflectionProvider getInstance() {
        return ActionContext.getContext().getContainer().getInstance(ReflectionProvider.class);
    }
}
