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
package org.apache.struts2.components;

import org.apache.struts2.views.jsp.AbstractTagTest;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.StrutsException;

public class AnotherActionComponentTest extends AbstractTagTest  {

    public void testRethrowException() throws Exception {
        request.setupGetServletPath(TestConfigurationProvider.TEST_NAMESPACE + "/"
                + "foo.action" );
        ActionComponent ac = new ActionComponent(stack, request, response) ;
        container.inject(ac);
        ac.setNamespace(TestConfigurationProvider.TEST_NAMESPACE);
        ac.setName(TestConfigurationProvider.TEST_ACTION_NAME + "!executeThrowsException");
        ac.setRethrowException(true);
        boolean exceptionCaught = false;
        try {
            ac.executeAction();
        }
        catch (Exception e) {
            if (e instanceof StrutsException)
                exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    public void testDoesNotThrowException() throws Exception {
        request.setupGetServletPath(TestConfigurationProvider.TEST_NAMESPACE + "/"
                + "foo.action" );
        ActionComponent ac = new ActionComponent(stack, request, response) ;
        container.inject(ac);
        ac.setNamespace(TestConfigurationProvider.TEST_NAMESPACE);
        ac.setName(TestConfigurationProvider.TEST_ACTION_NAME+ "!executeThrowsException");
        ac.setRethrowException(false);
        boolean exceptionCaught = false;
        try {
            ac.executeAction();
        }
        catch (Exception e) {
            if (e instanceof StrutsException)
                exceptionCaught = true;
        }
        assertTrue(! exceptionCaught);    
    }
}
