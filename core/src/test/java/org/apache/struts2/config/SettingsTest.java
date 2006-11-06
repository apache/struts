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

import java.util.Iterator;
import java.util.Locale;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsTestCase;

import com.opensymphony.xwork2.util.LocalizedTextUtil;


/**
 * Unit test for {@link SettingsTest}.
 *
 */
public class SettingsTest extends StrutsTestCase {

    public void testSettings() {
        assertEquals("get", Settings.get(StrutsConstants.STRUTS_URL_INCLUDEPARAMS));
        assertEquals("12345", Settings.get(StrutsConstants.STRUTS_MULTIPART_MAXSIZE));
        assertEquals("\temp", Settings.get(StrutsConstants.STRUTS_MULTIPART_SAVEDIR));

        assertEquals("test,org/apache/struts2/othertest", Settings.get( StrutsConstants.STRUTS_CUSTOM_PROPERTIES));
        assertEquals("testvalue", Settings.get("testkey"));
        assertEquals("othertestvalue", Settings.get("othertestkey"));

        Locale locale = Settings.getLocale();
        assertEquals("de", locale.getLanguage());

        int count = getKeyCount();
        assertEquals(32, count);
    }

    public void testDefaultResourceBundlesLoaded() {
        assertEquals("testmessages,testmessages2", Settings.get(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES));
        assertEquals("This is a test message", LocalizedTextUtil.findDefaultText("default.testmessage", Locale.getDefault()));
        assertEquals("This is another test message", LocalizedTextUtil.findDefaultText("default.testmessage2", Locale.getDefault()));
    }

    public void testReplaceDefaultMessages() {
        Locale.setDefault(Locale.US); // force to US locale as we also have _de and _da properties

        LocalizedTextUtil.clearDefaultResourceBundles();
        LocalizedTextUtil.addDefaultResourceBundle("org/apache/struts2/struts-messages");
        assertEquals("The form has already been processed or no token was supplied, please try again.", LocalizedTextUtil.findDefaultText("struts.messages.invalid.token", Locale.getDefault()));
        Settings.reset();

        assertEquals("testmessages,testmessages2", Settings.get(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES));
        assertEquals("Replaced message for token tag", LocalizedTextUtil.findDefaultText("struts.messages.invalid.token", Locale.getDefault()));
    }

    public void testSetSettings() {
        Settings.setInstance(new TestSettings());

        String keyName = "a.long.property.key.name";
        assertEquals(keyName, Settings.get(keyName));
        assertEquals(2, getKeyCount());
    }

    private int getKeyCount() {
        int count = 0;
        Iterator keyNames = Settings.list();

        while (keyNames.hasNext()) {
            keyNames.next();
            count++;
        }

        return count;
    }
}
