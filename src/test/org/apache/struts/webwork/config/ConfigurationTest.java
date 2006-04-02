/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.config;

import org.apache.struts.webwork.StrutsTestCase;
import org.apache.struts.webwork.StrutsConstants;
import com.opensymphony.xwork.util.LocalizedTextUtil;

import java.util.Iterator;
import java.util.Locale;


/**
 * Unit test for {@link ConfigurationTest}.
 *
 * @author Jason Carreira
 */
public class ConfigurationTest extends StrutsTestCase {

    public void testConfiguration() {
        assertEquals("12345", Configuration.getString(StrutsConstants.STRUTS_MULTIPART_MAXSIZE));
        assertEquals("\temp", Configuration.getString(StrutsConstants.STRUTS_MULTIPART_SAVEDIR));

        assertEquals("test,org/apache/struts/webwork/othertest", Configuration.getString( StrutsConstants.STRUTS_CUSTOM_PROPERTIES));
        assertEquals("testvalue", Configuration.getString("testkey"));
        assertEquals("othertestvalue", Configuration.getString("othertestkey"));

        Locale locale = Configuration.getLocale();
        assertEquals("de", locale.getLanguage());

        int count = getKeyCount();
        assertEquals(27, count);
    }

    public void testDefaultResourceBundlesLoaded() {
        assertEquals("testmessages,testmessages2", Configuration.getString(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES));
        assertEquals("This is a test message", LocalizedTextUtil.findDefaultText("default.testmessage", Locale.getDefault()));
        assertEquals("This is another test message", LocalizedTextUtil.findDefaultText("default.testmessage2", Locale.getDefault()));
    }

    public void testReplaceDefaultMessages() {
        Locale.setDefault(Locale.US); // force to US locale as we also have _de and _da properties
        
        LocalizedTextUtil.clearDefaultResourceBundles();
        LocalizedTextUtil.addDefaultResourceBundle("org/apache/struts/webwork/struts-messages");
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
            count++;
        }

        return count;
    }
}
