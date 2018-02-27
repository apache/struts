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
package org.apache.struts2;

import com.opensymphony.xwork2.ActionProxy;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: maurizio cucchiara
 * Date: 8/15/11
 * Time: 7:04 PM
 */
public class StrutsJUnit4TestCaseTest extends StrutsJUnit4TestCase<JUnitTestAction>{
    @Test
    public void testExecuteActionAgainstCustomStrutsConfigFile() throws Exception {
        String output = executeAction("/test/testAction-2.action");
        Assert.assertEquals("Test-2", output);
    }

    @Test
    public void testSessionInitialized() throws Exception {
        ActionProxy proxy = getActionProxy("/test/testAction-2.action");
        Assert.assertNotNull("invocation session should being initialized",
                proxy.getInvocation().getInvocationContext().getSession());
    }

    @Override
    protected String getConfigPath() {
        return "struts-test.xml";
    }
}
