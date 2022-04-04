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

package org.apache.struts2.views.velocity;

import org.apache.velocity.VelocityContext;

import com.opensymphony.xwork2.util.ValueStack;


/**
 */
public class StrutsVelocityContext extends VelocityContext {

    private static final long serialVersionUID = 8497212428904436963L;
    ValueStack stack;
    VelocityContext[] chainedContexts;


    public StrutsVelocityContext(ValueStack stack) {
        this(null, stack);
    }

    public StrutsVelocityContext(VelocityContext[] chainedContexts, ValueStack stack) {
        this.chainedContexts = chainedContexts;
        this.stack = stack;
    }


    public boolean internalContainsKey(Object key) {
        boolean contains = super.internalContainsKey(key);

        // first let's check to see if we contain the requested key
        if (contains) {
            return true;
        }

        // if not, let's search for the key in the ognl value stack
        if (stack != null) {
            Object o = stack.findValue(key.toString());

            if (o != null) {
                return true;
            }

            o = stack.getContext().get(key.toString());
            if (o != null) {
                return true;
            }
        }

        // if we still haven't found it, le's search through our chained contexts
        if (chainedContexts != null) {
            for (int index = 0; index < chainedContexts.length; index++) {
                if (chainedContexts[index].containsKey(key)) {
                    return true;
                }
            }
        }

        // nope, i guess it's really not here
        return false;
    }

    public Object internalGet(String key) {
        // first, let's check to see if have the requested value
        if (super.internalContainsKey(key)) {
            return super.internalGet(key);
        }

        // still no luck?  let's look against the value stack
        if (stack != null) {
            Object object = stack.findValue(key);

            if (object != null) {
                return object;
            }

            object = stack.getContext().get(key);
            if (object != null) {
                return object;
            }

        }

        // finally, if we're chained to other contexts, let's look in them
        if (chainedContexts != null) {
            for (int index = 0; index < chainedContexts.length; index++) {
                if (chainedContexts[index].containsKey(key)) {
                    return chainedContexts[index].internalGet(key);
                }
            }
        }

        // nope, i guess it's really not here
        return null;
    }
}
