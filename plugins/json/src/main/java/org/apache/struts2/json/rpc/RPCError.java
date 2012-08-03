/*
 * $Id$
 *
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
package org.apache.struts2.json.rpc;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/*
 * Used to serialize RPC Errors
 */
public class RPCError {
    private static final Logger LOG = LoggerFactory.getLogger(RPCError.class);

    private int code;
    private String name;
    private String message;
    private String stack;

    public RPCError() {
    }

    public RPCError(String message, int code) {
        this.code = code;
        this.message = message;

        LOG.error(message);
    }

    public RPCError(String message, RPCErrorCode code) {
        this(message, code.code());
    }

    public RPCError(Throwable t, int code, boolean debug) {
        while (t.getCause() != null) {
            t = t.getCause();
        }

        this.code = code;
        this.message = t.getMessage();
        this.name = t.getClass().getName();

        if (debug) {
            StringWriter s = new StringWriter();
            PrintWriter w = new PrintWriter(s);
            t.printStackTrace(w);
            w.flush();
            this.stack = s.toString();
        }

        LOG.error(t.getMessage(), t);
    }

    public RPCError(Throwable t, RPCErrorCode code, boolean debug) {
        this(t, code.code(), debug);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }
}
