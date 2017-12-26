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

import com.mockobjects.servlet.MockHttpServletRequest;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.StrutsInternalTestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Test case for BackgroundProcessTest.
 */
public class BackgroundProcessTest extends StrutsInternalTestCase {

    public void testSerializeDeserialize() throws Exception {
        final NotSerializableException expectedException = new NotSerializableException(new MockHttpServletRequest());
        final Semaphore lock = new Semaphore(1);
        lock.acquire();
        MockActionInvocationWithActionInvoker invocation = new MockActionInvocationWithActionInvoker(new Callable<String>() {
            @Override
            public String call() throws Exception {
                lock.release();
                throw expectedException;
            }
        });
        invocation.setInvocationContext(ActionContext.getContext());

        BackgroundProcess bp = new BackgroundProcess("BackgroundProcessTest.testSerializeDeserialize", invocation
                , Thread.MIN_PRIORITY);
        if(!lock.tryAcquire(1500L, TimeUnit.MILLISECONDS)) {
            lock.release();
            fail("background thread did not release lock on timeout");
        }
        lock.release();

        bp.result = "BackgroundProcessTest.testSerializeDeserialize";
        bp.done = true;
        Thread.sleep(1000);//give a chance to background thread to set exception
        assertEquals(expectedException, bp.exception);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(bp);
        oos.close();
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


    private class MockActionInvocationWithActionInvoker extends MockActionInvocation {
        private Callable<String> actionInvoker;

        MockActionInvocationWithActionInvoker(Callable<String> actionInvoker){
            this.actionInvoker = actionInvoker;
        }

        @Override
        public String invokeActionOnly() throws Exception {
            return actionInvoker.call();
        }
    }

    private class NotSerializableException extends Exception {
        private MockHttpServletRequest notSerializableField;
        NotSerializableException(MockHttpServletRequest notSerializableField) {
            this.notSerializableField = notSerializableField;
        }
    }
}