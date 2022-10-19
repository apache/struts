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
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.config.PropertiesConfigurationProvider;

import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InitOperationsTest extends StrutsInternalTestCase {

    public void testExcludePatterns() {
        // given
        loadConfigurationProviders(new PropertiesConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                props.setProperty(StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN, "/ns1/.*\\.json,/ns2/.*\\.json");
            }
        });

        Dispatcher mockDispatcher = mock(Dispatcher.class);
        when(mockDispatcher.getContainer()).thenReturn(container);

        // when
        InitOperations init = new InitOperations();
        List<Pattern> patterns = init.buildExcludedPatternsList(mockDispatcher);

        // then
        assertThat(patterns).extracting(Pattern::toString).containsOnly(
            "/ns1/.*\\.json",
            "/ns2/.*\\.json"
        );
    }

    public void testExcludePatternsUsingCustomSeparator() {
        // given
        loadConfigurationProviders(new PropertiesConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                props.setProperty(StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN, "/ns1/[a-z]{1,10}.json///ns2/[a-z]{1,10}.json");
                props.setProperty(StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN_SEPARATOR, "//");
            }
        });

        Dispatcher mockDispatcher = mock(Dispatcher.class);
        when(mockDispatcher.getContainer()).thenReturn(container);

        // when
        InitOperations init = new InitOperations();

        String separator = container.getInstance(String.class, StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN_SEPARATOR);
        List<Pattern> patterns = init.buildExcludedPatternsList(mockDispatcher);

        // then
        assertThat(separator).isNotBlank().isEqualTo("//");
        assertThat(patterns).extracting(Pattern::toString).containsOnly(
            "/ns1/[a-z]{1,10}.json",
            "/ns2/[a-z]{1,10}.json"
        );
    }
}
