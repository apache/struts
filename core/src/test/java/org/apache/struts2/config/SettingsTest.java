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

package org.apache.struts2.config;

import com.opensymphony.xwork2.util.LocalizedTextUtil;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsInternalTestCase;

import java.util.Iterator;
import java.util.Locale;


/**
 * Unit test for {@link SettingsTest}.
 *
 */
public class SettingsTest extends StrutsInternalTestCase {

    public void testSettings() {
        Settings settings = new DefaultSettings();

        assertEquals("12345", settings.get(StrutsConstants.STRUTS_MULTIPART_MAXSIZE));
        assertEquals("\temp", settings.get(StrutsConstants.STRUTS_MULTIPART_SAVEDIR));

        assertEquals("test,org/apache/struts2/othertest", settings.get( StrutsConstants.STRUTS_CUSTOM_PROPERTIES));
        assertEquals("testvalue", settings.get("testkey"));
        assertEquals("othertestvalue", settings.get("othertestkey"));

        int count = getKeyCount(settings);
        assertEquals(12, count);
    }

    public void testDefaultResourceBundlesLoaded() {
        Settings settings = new DefaultSettings();

        assertEquals("testmessages,testmessages2", settings.get(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES));
        assertEquals("This is a test message", LocalizedTextUtil.findDefaultText("default.testmessage", Locale.getDefault()));
        assertEquals("This is another test message", LocalizedTextUtil.findDefaultText("default.testmessage2", Locale.getDefault()));
    }

    public void testSetSettings() {
        Settings settings = new TestSettings();

        String keyName = "a.long.property.key.name";
        assertEquals(keyName, settings.get(keyName));
        assertEquals(2, getKeyCount(settings));
    }

    private int getKeyCount(Settings settings) {
        int count = 0;
        Iterator keyNames = settings.list();

        while (keyNames.hasNext()) {
            keyNames.next();
            count++;
        }

        return count;
    }
}
