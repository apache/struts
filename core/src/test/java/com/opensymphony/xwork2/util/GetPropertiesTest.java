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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Gabe
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GetPropertiesTest extends XWorkTestCase {

    public void testGetCollectionProperties()  {
        doGetCollectionPropertiesTest(new ArrayList());
        doGetCollectionPropertiesTest(new HashSet());
        
    }
    
    public void doGetCollectionPropertiesTest(Collection c) {
        ValueStack vs = ActionContext.getContext().getValueStack();
        Foo foo = new Foo();
        foo.setBarCollection(c);
        vs.push(foo);
        assertEquals(Boolean.TRUE, vs.findValue("barCollection.isEmpty"));
        assertEquals(Boolean.TRUE, vs.findValue("barCollection.empty"));
        assertEquals(new Integer(0), vs.findValue("barCollection.size"));
        assertTrue(vs.findValue("barCollection.iterator") instanceof java.util.Iterator);
    }
}
