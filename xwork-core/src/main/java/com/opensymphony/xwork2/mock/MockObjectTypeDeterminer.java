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
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import java.util.Map;

/**
 * Mocks the function of an ObjectTypeDeterminer for testing purposes.
 *
 * @author Gabe
 */
public class MockObjectTypeDeterminer implements ObjectTypeDeterminer {

    private Class keyClass;
    private Class elementClass;
    private String keyProperty;
    private boolean shouldCreateIfNew;

    public MockObjectTypeDeterminer() {}


    /**
     * @param keyClass
     * @param elementClass
     * @param keyProperty
     * @param shouldCreateIfNew
     */
    public MockObjectTypeDeterminer(Class keyClass, Class elementClass,
                                    String keyProperty, boolean shouldCreateIfNew) {
        super();
        this.keyClass = keyClass;
        this.elementClass = elementClass;
        this.keyProperty = keyProperty;
        this.shouldCreateIfNew = shouldCreateIfNew;
    }

    public Class getKeyClass(Class parentClass, String property) {
        return getKeyClass();
    }

    public Class getElementClass(Class parentClass, String property, Object key) {
        return getElementClass();
    }

    public String getKeyProperty(Class parentClass, String property) {
        return getKeyProperty();
    }

    public boolean shouldCreateIfNew(Class parentClass, String property,
                                     Object target, String keyProperty, boolean isIndexAccessed) {
        try {
            System.out.println("ognl:"+OgnlRuntime.getPropertyAccessor(Map.class)+" this:"+this);
        } catch (OgnlException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isShouldCreateIfNew();
    }

    /**
     * @return Returns the elementClass.
     */
    public Class getElementClass() {
        return elementClass;
    }
    /**
     * @param elementClass The elementClass to set.
     */
    public void setElementClass(Class elementClass) {
        this.elementClass = elementClass;
    }
    /**
     * @return Returns the keyClass.
     */
    public Class getKeyClass() {
        return keyClass;
    }
    /**
     * @param keyClass The keyClass to set.
     */
    public void setKeyClass(Class keyClass) {
        this.keyClass = keyClass;
    }
    /**
     * @return Returns the keyProperty.
     */
    public String getKeyProperty() {
        return keyProperty;
    }
    /**
     * @param keyProperty The keyProperty to set.
     */
    public void setKeyProperty(String keyProperty) {
        this.keyProperty = keyProperty;
    }
    /**
     * @return Returns the shouldCreateIfNew.
     */
    public boolean isShouldCreateIfNew() {
        return shouldCreateIfNew;
    }
    /**
     * @param shouldCreateIfNew The shouldCreateIfNew to set.
     */
    public void setShouldCreateIfNew(boolean shouldCreateIfNew) {
        this.shouldCreateIfNew = shouldCreateIfNew;
    }
}
