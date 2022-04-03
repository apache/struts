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
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.location.LocationImpl;

/**
 * ActionConfigTest
 */
public class ActionConfigTest extends XWorkTestCase {

    public void testToString() {
        ActionConfig cfg = new ActionConfig.Builder("", "bob", "foo.Bar")
                .methodName("execute")
                .location(new LocationImpl(null, "foo/xwork.xml", 10, 12))
                .build();

        assertTrue("Wrong toString(): "+cfg.toString(), 
            "{ActionConfig bob (foo.Bar.execute()) - foo/xwork.xml:10:12 - allowedMethods=[LiteralAllowedMethod{allowedMethod='execute'}]}".equals(cfg.toString()));
    }
    
    public void testToStringWithNoMethod() {
        ActionConfig cfg = new ActionConfig.Builder("", "bob", "foo.Bar")
                .location(new LocationImpl(null, "foo/xwork.xml", 10, 12))
                .build();
        
        assertTrue("Wrong toString(): "+cfg.toString(),
            "{ActionConfig bob (foo.Bar) - foo/xwork.xml:10:12 - allowedMethods=[]}".equals(cfg.toString()));
    }
}
