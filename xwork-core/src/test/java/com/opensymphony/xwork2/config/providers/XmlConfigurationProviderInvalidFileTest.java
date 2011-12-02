/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;


/**
 * XmlConfigurationProviderInvalidFileTest
 *
 * @author Jason Carreira
 *         Created Sep 6, 2003 2:36:10 PM
 */
public class XmlConfigurationProviderInvalidFileTest extends ConfigurationTestBase {

    public void testInvalidFileThrowsException() {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-invalid-file.xml";

        try {
            ConfigurationProvider provider = buildConfigurationProvider(filename);
            fail();
        } catch (ConfigurationException e) {
            // this is what we expect
        }
    }
}
