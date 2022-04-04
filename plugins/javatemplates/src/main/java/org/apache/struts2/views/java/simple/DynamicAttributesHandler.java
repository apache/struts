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

package org.apache.struts2.views.java.simple;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.struts2.util.ComponentUtils;
import org.apache.struts2.views.java.Attributes;

import java.io.IOException;
import java.util.Map;

/**
 * Adds dynamic attributes
 */
public class DynamicAttributesHandler extends AbstractTagHandler {

    /* (non-Javadoc)
     * @see org.apache.struts2.views.java.simple.AbstractTagHandler#start(java.lang.String, org.apache.struts2.views.java.Attributes)
     */
    @Override
    public void start(String name, Attributes a) throws IOException {
        processDynamicAttributes(a);
        super.start(name, a);
    }

    protected void processDynamicAttributes(Attributes a) {
        Map<String, String> dynamicAttributes = (Map<String, String>) context.getParameters().get("dynamicAttributes");
        for (Map.Entry<String, String> entry : dynamicAttributes.entrySet()) {
            if (altSyntax && ComponentUtils.isExpression(entry.getValue())) {
                String value = ObjectUtils.defaultIfNull(findString(entry.getValue()), entry.getValue());
                a.put(entry.getKey(), value);
            } else {
                a.put(entry.getKey(), entry.getValue());
            }
        }
    }

}
