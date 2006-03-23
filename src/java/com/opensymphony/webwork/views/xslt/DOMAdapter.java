/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.xslt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * @author <a href="mailto:meier@meisterbohne.de">Philipp Meier</a>
 * @author <a href="mailto:mimo@REMOVEIT.proinet.pl">Mike Mosiewicz</a>
 *         Anti-infinite-loops code. Performance enhancements.
 * @author Rainer Hermanns
 *         Date: 10.10.2003
 *         Time: 19:39:13
 */
public class DOMAdapter {

    public short getNodeType() {
        return Node.PROCESSING_INSTRUCTION_NODE; // What to return, is DOMAdapter a Node at last?
    }

    public String getPropertyName() {
        return "[Root]";
    }

    private Map index = new HashMap();

    private Pattern matchingPattern;
    private Pattern excludingPattern;

    static transient final Log LOG = LogFactory
            .getLog(DOMAdapter.class);


    /**
     * RelationKey indexes Parent-childsRole relations
     * between each two objects. It allows us to find out if
     * some object was already used in exactly the same relation to other object.
     * That means that we have loop, i.e. we walk second time through the
     * same path between two objects. Note that objects identities are compared, not values.
     * It means that two objects are the same if they occupy the same memory. Becouse of
     * that we have a good chance to only cut-off loops and not limit the graph too much.
     */
    private static class RelationKey {
        /**
         * @param parent
         * @param role
         */
        public RelationKey(Object parent, Object role) {
            super();
            this.parent = parent;
            this.role = role;
        }

        Object parent;
        Object role;

        /* (non-Javadoc)
        * @see java.lang.Object#equals(java.lang.Object)
        */
        public boolean equals(Object arg0) {
            if (arg0 instanceof RelationKey) {
                RelationKey other = (RelationKey) arg0;
                return parent == other.parent &&
                        role.equals(other.role);
            }
            return false;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return System.identityHashCode(parent) ^ role.hashCode();
        }
    }

    public boolean isAdaptable(DOMAdapter rootAdapter, Node parent, String propertyName) {
        if (this.matchingPattern == null && this.excludingPattern == null) return true;
        DefaultAdapterNode p = (DefaultAdapterNode) parent;
        StringBuffer buf = new StringBuffer();
        p.getPath(buf);
        buf.append('/').append(propertyName);
        boolean ret = (! (excludingPattern != null && excludingPattern.matcher(buf).matches())) &&
                (matchingPattern == null || matchingPattern.matcher(buf).matches());
        if (LOG.isDebugEnabled()) {
            LOG.debug("path adapt: " + buf + "=" + ret);
        }
        return ret;
    }

    public AdapterNode adapt(DOMAdapter rootAdapter, Node parent, String propertyName, Object value) {
        if (value == null) {
            LOG.warn("Property " + propertyName + " of " + parent + " is null");
            return null;
        }
        Class klass = value.getClass();
        Class adapterClass = findAdapterForClass(klass);

        try {
            if (LOG.isDebugEnabled())
                LOG.debug("creating adapter for: " + ((AdapterNode) parent).getValue() + "," + propertyName + "," + value);
            RelationKey rk = new RelationKey(((AdapterNode) parent).getValue(), propertyName);
            Integer period = (Integer) index.get(rk);
            if (period != null && ((AdapterNode) parent).getDepth() > period.intValue())
                return null;
            if (adapterClass == null) {
                if (klass.isArray()) {
                    adapterClass = ArrayAdapter.class;
                } else
                if (String.class.isAssignableFrom(klass) || klass.isPrimitive() || Number.class.isAssignableFrom(klass))
                {
                    adapterClass = ToStringAdapter.class;
                } else if (Collection.class.isAssignableFrom(klass)) {
                    adapterClass = CollectionAdapter.class;
                } else {
                    adapterClass = BeanAdapter.class;
                }
            }

            Constructor c = adapterClass.getConstructor(new Class[]{
                    DOMAdapter.class, AdapterNode.class, String.class,
                    Object.class
            });
            AdapterNode adapter = ((AdapterNode) c.newInstance(new Object[]{
                    this, parent, propertyName, value
            }));

            index.put(rk, new Integer(((AdapterNode) parent).getDepth()));
            return adapter;
        } catch (Exception e) {
            LOG.debug("Cannot adapt ", e);
            throw new RuntimeException("Cannot adapt " + parent + " (" + propertyName + ") ", e);
        }
    }

    public AdapterNode adapt(Object value) throws IllegalAccessException, InstantiationException {
        return new DocumentAdapter(this, null, "result", value);
    }

    public AdapterNode adaptNullValue(DOMAdapter rootAdapter, BeanAdapter parent, String propertyName) {
        return new ToStringAdapter(rootAdapter, parent, propertyName, "null");
    }

    //TODO: implement Configuration option to provide additional adapter classes
    private Class findAdapterForClass(Class klass) {
        return null;
    }

    public Pattern getMatchingPattern() {
        return matchingPattern;
    }

    public void setPattern(Pattern pattern, Pattern excludingPattern) {
        this.matchingPattern = pattern;
        this.excludingPattern = excludingPattern;
    }
}
