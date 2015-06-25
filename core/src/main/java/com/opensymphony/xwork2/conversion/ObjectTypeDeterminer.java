/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.conversion;

/**
 * Determines what the key and and element class of a Map or Collection should be. For Maps, the elements are the
 * values. For Collections, the elements are the elements of the collection.
 * <p/>
 * See the implementations for javadoc description for the methods as they are dependent on the concrete implementation.
 *
 * @author Gabriel Zimmerman
 */
public interface ObjectTypeDeterminer {

    public Class getKeyClass(Class parentClass, String property);

    public Class getElementClass(Class parentClass, String property, Object key);

    public String getKeyProperty(Class parentClass, String property);
    
    public boolean shouldCreateIfNew(Class parentClass,  String property,  Object target, String keyProperty, boolean isIndexAccessed);

}
