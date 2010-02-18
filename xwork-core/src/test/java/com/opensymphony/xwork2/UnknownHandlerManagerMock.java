package com.opensymphony.xwork2;

import com.opensymphony.xwork2.DefaultUnknownHandlerManager;

import java.util.ArrayList;

/*
 * Utility class for testing DefaultUnknownHandlerManager, which does not allow to add
 * UnknownHandlers directly
 */
public class UnknownHandlerManagerMock extends DefaultUnknownHandlerManager {
    public void addUnknownHandler(UnknownHandler uh) {
        if (this.unknownHandlers == null)
            this.unknownHandlers = new ArrayList<UnknownHandler>();
        this.unknownHandlers.add(uh);
    }
}
