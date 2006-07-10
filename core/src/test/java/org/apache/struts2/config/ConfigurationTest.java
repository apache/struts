/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.config;

import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.StrutsConstants;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

import java.util.Iterator;
import java.util.Locale;


/**
 * Unit test for {@link ConfigurationTest}.
 *
 */
public class ConfigurationTest extends StrutsTestCase {

    public void testConfiguration() {
        assertEquals("12345", Configuration.getString(StrutsConstants.STRUTS_MULTIPART_MAXSIZE));
        assertEquals("\temp", Configuration.getString(StrutsConstants.STRUTS_MULTIPART_SAVEDIR));

        assertEquals("test,org/apache/struts2/othertest", Configuration.getString( StrutsConstants.STRUTS_CUSTOM_PROPERTIES));
        assertEquals("testvalue", Configuration.getString("testkey"));
        assertEquals("othertestvalue", Configuration.getString("othertestkey"));

        Locale locale = Configuration.getLocale();
        assertEquals("de", locale.getLanguage());

        int count = getKeyCount();
        assertEquals(28, count);
    }

    public void testDefaultResourceBundlesLoaded() {
        assertEquals("testmessages,testmessages2", Configuration.getString(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES));
        assertEquals("This is a test message", LocalizedTextUtil.findDefaultText("default.testmessage", Locale.getDefault()));
        assertEquals("This is another test message", LocalizedTextUtil.findDefaultText("default.testmessage2", Locale.getDefault()));
    }

    public void testReplaceDefaultMessages() {
        Locale.setDefault(Locale.US); // force to US locale as we also have _de and _da properties
        
        LocalizedTextUtil.clearDefaultResourceBundles();
        LocalizedTextUtil.addDefaultResourceBundle("org/apache/struts2/struts-messages");
        assertEquals("The form has already been processed or no token was supplied, please try again.", LocalizedTextUtil.findDefaultText("struts.messages.invalid.token", Locale.getDefault()));
        Configuration.reset();

        assertEquals("testmessages,testmessages2", Configuration.getString(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES));
        assertEquals("Replaced message for token tag", LocalizedTextUtil.findDefaultText("struts.messages.invalid.token", Locale.getDefault()));
    }

    public void testSetConfiguration() {
        Configuration.setConfiguration(new TestConfiguration());
        
        String keyName = "a.long.property.key.name";
        assertEquals(keyName, Configuration.getString(keyName));
        assertEquals(2, getKeyCount());
    }

    private int getKeyCount() {
        int count = 0;
        Iterator keyNames = Configuration.list();

        while (keyNames.hasNext()) {
        	keyNames.next();
            count++;
        }

        return count;
    }
}
