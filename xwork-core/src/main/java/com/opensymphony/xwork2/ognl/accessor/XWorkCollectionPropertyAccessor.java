/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.SetPropertyAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gabe
 */
public class XWorkCollectionPropertyAccessor extends SetPropertyAccessor {

    private static final Logger LOG = LoggerFactory.getLogger(XWorkCollectionPropertyAccessor.class);
    private static final String CONTEXT_COLLECTION_MAP = "xworkCollectionPropertyAccessorContextSetMap";

    public static final String KEY_PROPERTY_FOR_CREATION = "makeNew";

    //use a basic object Ognl property accessor here
    //to access properties of the objects in the Set
    //so that nothing is put in the context to screw things up
    private ObjectPropertyAccessor _accessor = new ObjectPropertyAccessor();
    
    private XWorkConverter xworkConverter;
    private ObjectFactory objectFactory;
    private ObjectTypeDeterminer objectTypeDeterminer;
    
    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }
    
    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }
    
    @Inject
    public void setObjectTypeDeterminer(ObjectTypeDeterminer ot) {
        this.objectTypeDeterminer = ot;
    }

    /**
     * Gets the property of a Collection by indexing the collection
     * based on a key property. For example, if the key property were
     * 'id', this method would convert the key Object to whatever
     * type the id property was, and then access the Set like it was
     * a Map returning a JavaBean with the value of id property matching
     * the input.
     *
     * @see ognl.PropertyAccessor#getProperty(java.util.Map, Object, Object)
     */
    @Override
    public Object getProperty(Map context, Object target, Object key)
            throws OgnlException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering getProperty()");
        }

        //check if it is a generic type property.
        //if so, return the value from the
        //superclass which will determine this.
        if (!ReflectionContextState.isGettingByKeyProperty(context)
                && !key.equals(KEY_PROPERTY_FOR_CREATION)) {
            return super.getProperty(context, target, key);
        }	else {
            //reset context property
            ReflectionContextState.setGettingByKeyProperty(context,false);
        }
        Collection c = (Collection) target;

        //get the bean that this collection is a property of
        Class lastBeanClass = ReflectionContextState.getLastBeanClassAccessed(context);

        //get the property name that this collection uses
        String lastPropertyClass = ReflectionContextState.getLastBeanPropertyAccessed(context);

        //if one or the other is null, assume that it isn't
        //set up correctly so just return whatever the
        //superclass would
        if (lastBeanClass == null || lastPropertyClass == null) {
            ReflectionContextState.updateCurrentPropertyPath(context, key);
            return super.getProperty(context, target, key);
        }
        
        
        //get the key property to index the
        //collection with from the ObjectTypeDeterminer
        String keyProperty = objectTypeDeterminer
                .getKeyProperty(lastBeanClass, lastPropertyClass);

        //get the collection class of the
        Class collClass = objectTypeDeterminer.getElementClass(lastBeanClass, lastPropertyClass, key);

        Class keyType = null;
        Class toGetTypeFrom = (collClass != null) ? collClass : c.iterator().next().getClass();
        try {
            keyType = OgnlRuntime.getPropertyDescriptor(toGetTypeFrom, keyProperty).getPropertyType();
        } catch (Exception exc) {
            throw new OgnlException("Error getting property descriptor: " + exc.getMessage());
        }


        if (ReflectionContextState.isCreatingNullObjects(context)) {
            Map collMap = getSetMap(context, c, keyProperty, collClass);
            if (key.toString().equals(KEY_PROPERTY_FOR_CREATION)) {
                //this should return the XWorkList
                //for this set that contains new entries
                //then the ListPropertyAccessor will be called
                //to access it in the next sequence
                return collMap.get(null);
            }
            Object realKey = xworkConverter.convertValue(context, key, keyType);
            Object value = collMap.get(realKey);
            if (value == null
                    && ReflectionContextState.isCreatingNullObjects(context)
                    && objectTypeDeterminer
                    .shouldCreateIfNew(lastBeanClass,lastPropertyClass,c,keyProperty,false)) {
                	//create a new element and 
                    //set the value of keyProperty
                    //to be the given value
                	try {
                	    value=objectFactory.buildBean(collClass, context);
                	    
                	    //set the value of the keyProperty
                	    _accessor.setProperty(context,value,keyProperty,realKey);
                	    
                	    //add the new object to the collection 
                	    c.add(value);
                	    
                	    //add to the Map if accessed later
                	    collMap.put(realKey, value);
                	    
                	    
                	}	catch (Exception exc) {
                	    throw new OgnlException("Error adding new element to collection", exc);
                	    
                	}
                
            }
            return value;
        } else {
            if (key.toString().equals(KEY_PROPERTY_FOR_CREATION)) {
                return null;
            }
            //with getting do iteration
            //don't assume for now it is
            //optimized to create the Map
            //and unlike setting, there is
            //no easy key for the Set
            Object realKey = xworkConverter.convertValue(context, key, keyType);
            return getPropertyThroughIteration(context, c, keyProperty, realKey);
        }
    }

    /*
      * Gets an indexed Map by a given key property with the key being
      * the value of the property and the value being the
      */
    private Map getSetMap(Map context, Collection collection, String property, Class valueClass)
            throws OgnlException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getting set Map");
        }
        
        String path = ReflectionContextState.getCurrentPropertyPath(context);
        Map map = ReflectionContextState.getSetMap(context,
                path);

        if (map == null) {
            if (LOG.isDebugEnabled()) {
        	LOG.debug("creating set Map");
            }
            
            map = new HashMap();
            map.put(null, new SurrugateList(collection));
            for (Object currTest : collection) {
                Object currKey = _accessor.getProperty(context, currTest, property);
                if (currKey != null) {
                    map.put(currKey, currTest);
                }
            }
            ReflectionContextState.setSetMap(context, map, path);
        }
        return map;
    }

    /*
      * gets a bean with the given
      */
    public Object getPropertyThroughIteration(Map context, Collection collection, String property, Object key)
            throws OgnlException {
        //TODO
        for (Object currTest : collection) {
            if (_accessor.getProperty(context, currTest, property).equals(key)) {
                return currTest;
            }
        }
        //none found
        return null;
    }

    @Override
    public void setProperty(Map arg0, Object arg1, Object arg2, Object arg3)
            throws OgnlException {
        
        super.setProperty(arg0, arg1, arg2, arg3);
    }
}

/**
 * @author Gabe
 */
class SurrugateList extends ArrayList {

    private Collection surrugate;

    public SurrugateList(Collection surrugate) {
        this.surrugate = surrugate;
    }

    @Override
    public void add(int arg0, Object arg1) {
        if (arg1 != null) {
            surrugate.add(arg1);
        }
        super.add(arg0, arg1);
    }

    @Override
    public boolean add(Object arg0) {
        if (arg0 != null) {
            surrugate.add(arg0);
        }
        return super.add(arg0);
    }

    @Override
    public boolean addAll(Collection arg0) {
        surrugate.addAll(arg0);
        return super.addAll(arg0);
    }

    @Override
    public boolean addAll(int arg0, Collection arg1) {
        surrugate.addAll(arg1);
        return super.addAll(arg0, arg1);
    }

    @Override
    public Object set(int arg0, Object arg1) {
        if (arg1 != null) {
            surrugate.add(arg1);
        }
        return super.set(arg0, arg1);
    }
}
