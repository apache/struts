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
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.StrutsInternalTestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Test case for BackgroundProcessTest.
 */
public class BackgroundProcessTest extends StrutsInternalTestCase {

    public void testSerializeDeserialize() throws Exception {
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setResultCode("BackgroundProcessTest.testSerializeDeserialize");
        invocation.setInvocationContext(ActionContext.getContext());

        BackgroundProcess bp = new BackgroundProcess("BackgroundProcessTest.testSerializeDeserialize", invocation
                , Thread.MIN_PRIORITY);

        bp.exception = new Exception();
        bp.done = true;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(bp);
        oos.close();
        assertTrue("should have serialized data", baos.size() > 0);
        byte b[] = baos.toByteArray();
        baos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        BackgroundProcess deserializedBp = (BackgroundProcess) ois.readObject();
        ois.close();
        bais.close();

        assertNull("invocation should not be serialized", deserializedBp.invocation);
        assertNull("exception should not be serialized", deserializedBp.exception);
        assertEquals(bp.result, deserializedBp.result);
        assertEquals(bp.done, deserializedBp.done);
    }
}