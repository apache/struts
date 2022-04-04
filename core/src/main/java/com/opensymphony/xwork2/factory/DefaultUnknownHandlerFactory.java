package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

import java.util.Map;

/**
 * Default implementation
 */
public class DefaultUnknownHandlerFactory implements UnknownHandlerFactory {

    private Container container;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public UnknownHandler buildUnknownHandler(String unknownHandlerName, Map<String, Object> extraContext) throws Exception {
        return container.getInstance(UnknownHandler.class, unknownHandlerName);
    }

}
