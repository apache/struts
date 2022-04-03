/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.conversion;

import org.apache.struts2.StrutsException;

/**
 * TypeConversionException should be thrown by any TypeConverters which fail to convert values
 *
 * @author Jason Carreira
 *         Created Oct 3, 2003 12:18:33 AM
 */
public class TypeConversionException extends StrutsException {

    /**
     * Constructs a <code>StrutsException</code> with no detail message.
     */
    public TypeConversionException() {
    }

    /**
     * Constructs a <code>StrutsException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public TypeConversionException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>StrutsException</code> with no detail  message.
     * @param cause the cause
     */
    public TypeConversionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>StrutsException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     * @param cause the cause
     */
    public TypeConversionException(String s, Throwable cause) {
        super(s, cause);
    }
}
