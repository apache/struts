/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.xslt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:meier@meisterbohne.de">Philipp Meier</a>
 * @author Mike Mosiewicz
 * @author Rainer Hermanns
 *         Date: 14.10.2003
 *         Time: 18:59:07
 */
public class ArrayAdapter extends DefaultElementAdapter {

    private Log log = LogFactory.getLog(this.getClass());


    public ArrayAdapter(DOMAdapter rootAdapter, AdapterNode parent, String propertyName, Object value) {
        super(rootAdapter, parent, propertyName, value);
    }


    protected List buildChildrenAdapters() {
        List children = new ArrayList();
        Object[] values = (Object[]) getValue();

        for (int i = 0; i < values.length; i++) {
            AdapterNode childAdapter = getRootAdapter().adapt(getRootAdapter(), this, "item", values[i]);
            if( childAdapter != null)
                children.add(childAdapter);

            if (log.isDebugEnabled()) {
                log.debug(this + " adding adapter: " + childAdapter);
            }
        }

        return children;
    }
}
