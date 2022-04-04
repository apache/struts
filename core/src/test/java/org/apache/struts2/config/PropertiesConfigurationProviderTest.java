/*
 * $Id: SettingsTest.java 651946 2008-04-27 13:41:38Z apetrelli $
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

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import junit.framework.TestCase;
import org.apache.struts2.StrutsConstants;

import java.util.Locale;


/**
 * Unit test for {@link SettingsTest}.
 *
 */
public class PropertiesConfigurationProviderTest extends TestCase {

    public void testRegister_DifferentLocale() {

        ContainerBuilder builder = new ContainerBuilder();
        builder.constant("foo", "bar");
        builder.constant("struts.locale", "DE_de");

        PropertiesConfigurationProvider prov = new PropertiesConfigurationProvider();
        prov.register(builder, new LocatableProperties());

        Container container = builder.create(true);

        String localeStr = container.getInstance(String.class, StrutsConstants.STRUTS_LOCALE);
        Locale locale = LocalizedTextUtil.localeFromString(localeStr, Locale.FRANCE);

        assertNotNull(locale);
        assertEquals("DE", locale.getCountry());
        assertEquals("de", locale.getLanguage());

    }

    public void testRegister_NoLocale() {

        ContainerBuilder builder = new ContainerBuilder();
        builder.constant("foo", "bar");

        PropertiesConfigurationProvider prov = new PropertiesConfigurationProvider();
        prov.register(builder, new LocatableProperties());

        Container container = builder.create(true);

        String localeStr = container.getInstance(String.class, StrutsConstants.STRUTS_LOCALE);
        Locale locale = LocalizedTextUtil.localeFromString(localeStr, Locale.getDefault());

        assertNotNull(locale);
        Locale vmLocale = Locale.getDefault();
        assertEquals(locale, vmLocale);
    }

    public void testDefaultSettings() throws Exception {
        // given
        PropertiesConfigurationProvider prov = new PropertiesConfigurationProvider();
        LocatableProperties props = new LocatableProperties();
        prov.register(new ContainerBuilder(), props);

        // when
        Object encoding = props.get(StrutsConstants.STRUTS_I18N_ENCODING);

        // then
        assertEquals("ISO-8859-1", encoding);
    }

}
