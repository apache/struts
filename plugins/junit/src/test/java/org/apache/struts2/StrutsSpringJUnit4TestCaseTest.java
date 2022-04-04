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
package org.apache.struts2;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletException;
import java.io.UnsupportedEncodingException;

@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations={"classpath*:applicationContext.xml"})
public class StrutsSpringJUnit4TestCaseTest extends StrutsSpringJUnit4TestCase<JUnitTestAction> {

	@Test
    public void getActionMapping() {
        ActionMapping mapping = getActionMapping("/test/testAction.action");
        Assert.assertNotNull(mapping);
        Assert.assertEquals("/test", mapping.getNamespace());
        Assert.assertEquals("testAction", mapping.getName());
    }

	@Test
    public void getActionProxy() throws Exception {
        //set parameters before calling getActionProxy
        request.setParameter("name", "FD");
        
        ActionProxy proxy = getActionProxy("/test/testAction.action");
        Assert.assertNotNull(proxy);

        JUnitTestAction action = (JUnitTestAction) proxy.getAction();
        Assert.assertNotNull(action);

        String result = proxy.execute();
        Assert.assertEquals(Action.SUCCESS, result);
        Assert.assertEquals("FD", action.getName());
    }

	@Test
    public void executeAction() throws ServletException, UnsupportedEncodingException {
        String output = executeAction("/test/testAction.action");
        Assert.assertEquals("Hello", output);
    }

	@Test
    public void getValueFromStack() throws ServletException, UnsupportedEncodingException {
        request.setParameter("name", "FD");
        executeAction("/test/testAction.action");
        String name = (String) findValueAfterExecute("name");
        Assert.assertEquals("FD", name);
    }
}
