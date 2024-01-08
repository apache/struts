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
package org.apache.struts2.views.velocity;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Extends the default {@link VelocityContext} to ensure that the {@link #getKeys()} method returns all keys from the
 * current context and the chained context.
 *
 * @since 6.4.0
 */
public class ChainedVelocityContext extends VelocityContext {

    public ChainedVelocityContext(Context delegate) {
        super(delegate);
    }

    @Override
    public String[] getKeys() {
        Set<String> allKeys = new HashSet<>(Arrays.asList(internalGetKeys()));
        if (getChainedContext() != null) {
            allKeys.addAll(Arrays.asList(getChainedContext().getKeys()));
        }
        return allKeys.toArray(new String[0]);
    }
}
