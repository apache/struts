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

public enum RPCErrorCode {
    MISSING_METHOD(100, "'method' parameter is missing in request"),
    MISSING_ID(100, "'id' parameter is missing in request"),
    INVALID_PROCEDURE_CALL(0, "Invalid procedure call"),
    METHOD_NOT_FOUND(101, "Procedure not found"),
    PARAMETERS_MISMATCH(102, "Parameters count in request does not patch parameters count on method"),
    EXCEPTION(103, "An exception was thrown"),
    SMD_DISABLED(104, "SMD is disabled");

    private int code;
    private String message;

    RPCErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return this.message;
    }
}
