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

public class ComponentTagExampleTest extends ITBaseTest {
    public void test() {
        beginAt("/tags/ui/componentTagExample.jsp");
        assertTextPresent("Freemarker Custom Template - parameter 'paramName' - paramValue1");
        assertTextPresent("Freemarker Custom Template - parameter 'paramName' - paramValue4");
        assertTextPresent("JSP Custom Template - parameter 'paramName' - paramValue2");
        assertTextPresent("JSP Custom Template - parameter 'paramName' - paramValue3");
    }
}
