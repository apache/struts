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
package com.opensymphony.xwork2.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Serializable;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * A simple wrapper around an exception, providing an easy way to print out the stack trace of the exception as well as
 * a way to get a handle on the exception itself.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * @author Matthew E. Porter (matthew dot porter at metissian dot com)
 */
public class ExceptionHolder implements Serializable {

    private static final long serialVersionUID = 1L;
    private Exception exception;

    /**
     * Holds the given exception
     *
     * @param exception  the exception to hold.
     */
    public ExceptionHolder(Exception exception) {
        this.exception = exception;
    }

    /**
     * Gets the held exception
     *
     * @return the held exception
     */
    public Exception getException() {
        return this.exception;
    }

    /**
     * Gets the held exception stack trace using {@link Exception#printStackTrace()}.
     *
     * @return stack trace
     */
    public String getExceptionStack() {
        String exceptionStack = null;

        if (getException() != null) {
            try (StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw)) {
                getException().printStackTrace(pw);
                exceptionStack = sw.toString();
            } catch (IOException e) {
                // Ignore exception generating stack trace.
            }
        }

        return exceptionStack;
    }
    
}
