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
package it.org.apache.struts2.showcase.staticcontent;

import it.org.apache.struts2.showcase.ITBaseTest;

import java.io.IOException;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;

public class StaticContentTest extends ITBaseTest {

    public void testInvalidRersources1() throws IOException {
        try {
            beginAt("/struts..");
            fail("Previous request should have failed");
        } catch (TestingEngineResponseException ex) {
            // ok
        }
    }

    public void testInvalidRersources2() throws IOException {
        try {
            beginAt("/struts/..%252f");
            fail("Previous request should have failed");
        } catch (TestingEngineResponseException ex) {
            // ok
        }
    }

    /*public void testInvalidRersources3() throws IOException {
        try {
            beginAt("/struts/..%252f..%252f..%252fWEB-INF/classes/org/apache/struts2/showcase/action/EmployeeAction.class/");
            fail("Previous request should have failed");
        } catch (TestingEngineResponseException ex) {
            // ok
        }
    }*/
}
