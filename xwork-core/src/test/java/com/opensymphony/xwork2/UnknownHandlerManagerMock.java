package org.apache.struts2.xwork2;

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
