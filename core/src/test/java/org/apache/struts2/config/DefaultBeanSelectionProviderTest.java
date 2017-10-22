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
package org.apache.struts2.config;

import java.util.Locale;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import org.apache.struts2.StrutsConstants;

import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;

public class DefaultBeanSelectionProviderTest extends XWorkTestCase {

    public void testRegister() {
        LocalizedTextProvider localizedTextProvider = container.getInstance(LocalizedTextProvider.class);

        assertEquals("The form has already been processed or no token was supplied, please try again.", localizedTextProvider.findDefaultText("struts.messages.invalid.token", Locale.getDefault()));
        
        loadConfigurationProviders(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                props.setProperty(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES, "testmessages,testmessages2");
                props.setProperty(StrutsConstants.STRUTS_LOCALE, "US");
            }
        });

        localizedTextProvider = container.getInstance(LocalizedTextProvider.class);

        assertEquals("Replaced message for token tag", localizedTextProvider.findDefaultText("struts.messages.invalid.token", Locale.getDefault()));
    }

}
