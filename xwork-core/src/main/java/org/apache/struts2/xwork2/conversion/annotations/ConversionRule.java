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
package org.apache.struts2.xwork2.conversion.annotations;

/**
 * <code>ConversionRule</code>
 *
 * @author Rainer Hermanns
 * @version $Id: ConversionRule.java 1209415 2011-12-02 11:24:48Z lukaszlenart $
 */
public enum ConversionRule {

    PROPERTY, COLLECTION, MAP, KEY, KEY_PROPERTY, ELEMENT, CREATE_IF_NULL;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}

