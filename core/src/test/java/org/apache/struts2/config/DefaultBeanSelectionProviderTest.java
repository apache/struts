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

import java.util.Locale;

import org.apache.struts2.StrutsConstants;

import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;

import junit.framework.TestCase;

public class DefaultBeanSelectionProviderTest extends TestCase {

    public void testRegister() {
        LocalizedTextUtil.clearDefaultResourceBundles();
        LocalizedTextUtil.addDefaultResourceBundle("org/apache/struts2/struts-messages");
        assertEquals("The form has already been processed or no token was supplied, please try again.", LocalizedTextUtil.findDefaultText("struts.messages.invalid.token", Locale.getDefault()));
        
        LocatableProperties props = new LocatableProperties();
        props.setProperty(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES, "testmessages,testmessages2");
        props.setProperty(StrutsConstants.STRUTS_LOCALE, "US");
        
        new DefaultBeanSelectionProvider().register(new ContainerBuilder(), props);

        assertEquals("Replaced message for token tag", LocalizedTextUtil.findDefaultText("struts.messages.invalid.token", Locale.getDefault()));
    }

}
