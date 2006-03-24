/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.config;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A Configuration implementation which stores an internal list of configuration objects. Each time
 * a config method is called (get, set, list, etc..) this class will go through the list of configurations
 * and call the method until successful.
 *
 * @author Rickard Öberg
 * @author Jason Carreira
 * @author Bill Lynch (docs)
 */
public class DelegatingConfiguration extends Configuration {

    Configuration[] configList;


    /**
     * Creates a new DelegatingConfiguration object given a list of {@link Configuration} implementations.
     *
     * @param aConfigList a list of Configuration implementations.
     */
    public DelegatingConfiguration(Configuration[] aConfigList) {
        configList = aConfigList;
    }


    /**
     * Sets the given property - calls setImpl(String, Object) method on config objects in the config
     * list until successful.
     *
     * @see #set(String, Object)
     */
    public void setImpl(String name, Object value) throws IllegalArgumentException, UnsupportedOperationException {
        // Determine which config to use by using get
        // Delegate to the other configurations
        IllegalArgumentException e = null;

        for (int i = 0; i < configList.length; i++) {
            try {
                configList[i].getImpl(name);

                // Found it, now try setting
                configList[i].setImpl(name, value);

                // Worked, now return
                return;
            } catch (IllegalArgumentException ex) {
                e = ex;

                // Try next config
            }
        }

        throw e;
    }

    /**
     * Gets the specified property - calls getImpl(String) method on config objects in config list
     * until successful.
     *
     * @see #get(String)
     */
    public Object getImpl(String name) throws IllegalArgumentException {
        // Delegate to the other configurations
        IllegalArgumentException e = null;

        for (int i = 0; i < configList.length; i++) {
            try {
                return configList[i].getImpl(name);
            } catch (IllegalArgumentException ex) {
                e = ex;

                // Try next config
            }
        }

        throw e;
    }

    /**
     * Determines if a paramter has been set - calls the isSetImpl(String) method on each config object
     * in config list. Returns <tt>true</tt> when one of the config implementations returns true. Returns
     * <tt>false</tt> otherwise.
     *
     * @see #isSet(String)
     */
    public boolean isSetImpl(String aName) {
        for (int i = 0; i < configList.length; i++) {
            if (configList[i].isSetImpl(aName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a list of all property names - returns a list of all property names in all config
     * objects in config list.
     *
     * @see #list()
     */
    public Iterator listImpl() {
        boolean workedAtAll = false;

        Set settingList = new HashSet();
        UnsupportedOperationException e = null;

        for (int i = 0; i < configList.length; i++) {
            try {
                Iterator list = configList[i].listImpl();

                while (list.hasNext()) {
                    settingList.add(list.next());
                }

                workedAtAll = true;
            } catch (UnsupportedOperationException ex) {
                e = ex;

                // Try next config
            }
        }

        if (!workedAtAll) {
            throw (e == null) ? new UnsupportedOperationException() : e;
        } else {
            return settingList.iterator();
        }
    }
}
