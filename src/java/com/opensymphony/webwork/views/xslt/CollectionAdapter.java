/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.xslt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @author <a href="mailto:meier@meisterbohne.de">Philipp Meier</a>
 * @author Mike Mosiewicz
 * @author Rainer Hermanns
 *         Date: 14.10.2003
 *         Time: 18:59:07
 */
public class CollectionAdapter extends DefaultElementAdapter {

    private Log log = LogFactory.getLog(this.getClass());


    public CollectionAdapter(DOMAdapter rootAdapter, AdapterNode parent, String propertyName, Object value) {
        super(rootAdapter, parent, propertyName, value);
    }


    protected List buildChildrenAdapters() {
        Collection values = (Collection) getValue();
        List children = new ArrayList(values.size());

        for (Iterator i = values.iterator(); i.hasNext();) {
            AdapterNode childAdapter = getRootAdapter().adapt(getRootAdapter(), this, "item", i.next());
            if( childAdapter != null)
                children.add(childAdapter);

            if (log.isDebugEnabled()) {
                log.debug(this + " adding adapter: " + childAdapter);
            }
        }

        return children;
    }
}
