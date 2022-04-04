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

import java.net.MalformedURLException;
import java.net.URL;

public class FreeMarkerManagerTest extends ITBaseTest {
    public void testCustomManager() {
        beginAt("/freemarker/customFreemarkerManagerDemo.action");

        String date = getElementTextByXPath("//*[@id='todaysDate']");
        assertNotNull(date);
        assertTrue(date.length() > 0);

        String time = getElementTextByXPath("//*[@id='timeNow']");
        assertNotNull(time);
        assertTrue(time.length() > 0);
    }

    public void testTags() {
        beginAt("/freemarker/standardTags.action");
        assertElementPresent("test_name");
        assertElementPresent("test_");
    }
}
