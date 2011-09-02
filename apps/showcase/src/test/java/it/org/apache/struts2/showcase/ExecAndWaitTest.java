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
package it.org.apache.struts2.showcase;

public class ExecAndWaitTest extends ITBaseTest {
    public void testNodelay() throws InterruptedException {
        beginAt("/wait/example1.jsp");

        setTextField("time", "7000");
        submit();
        assertTextPresent("We are processing your request. Please wait.");

        //hit it again
        beginAt("/wait/longProcess1.action?time=1000");
        assertTextPresent("We are processing your request. Please wait.");
    }
}
