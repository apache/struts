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
package com.opensymphony.xwork2.conversion;

import com.opensymphony.xwork2.XWorkException;


/**
 * TypeConversionException should be thrown by any TypeConverters which fail to convert values
 *
 * @author Jason Carreira
 *         Created Oct 3, 2003 12:18:33 AM
 */
public class TypeConversionException extends XWorkException {

    /**
     * Constructs a <code>XWorkException</code> with no detail  message.
     */
    public TypeConversionException() {
    }

    /**
     * Constructs a <code>XWorkException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public TypeConversionException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>XWorkException</code> with no detail  message.
     */
    public TypeConversionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>XWorkException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public TypeConversionException(String s, Throwable cause) {
        super(s, cause);
    }
}
