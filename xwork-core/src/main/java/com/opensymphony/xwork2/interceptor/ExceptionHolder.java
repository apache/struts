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
     * Gets the holded exception
     *
     * @return  the holded exception
     */
    public Exception getException() {
        return this.exception;
    }

    /**
     * Gets the holded exception stacktrace using {@link Exception#printStackTrace()}.
     *
     * @return  stacktrace
     */
    public String getExceptionStack() {
        String exceptionStack = null;

        if (getException() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            try {
                getException().printStackTrace(pw);
                exceptionStack = sw.toString();
            }
            finally {
                try {
                    sw.close();
                    pw.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return exceptionStack;
    }
    
}
