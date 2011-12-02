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

package org.apache.struts2.util;

import java.io.CharArrayWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.opensymphony.xwork2.util.ValueStack;


/**
 * Struts velocity related util.
 *
 */
public class VelocityStrutsUtil extends StrutsUtil {

    private Context ctx;
    private VelocityEngine velocityEngine;

    public VelocityStrutsUtil(VelocityEngine engine, Context ctx, ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.ctx = ctx;
        this.velocityEngine = engine;
    }

    public String evaluate(String expression) throws IOException, ResourceNotFoundException, MethodInvocationException, ParseErrorException {
        CharArrayWriter writer = new CharArrayWriter();
        velocityEngine.evaluate(ctx, writer, "Error parsing " + expression, expression);

        return writer.toString();
    }

}
