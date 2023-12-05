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
package org.apache.struts2.ognl;

import com.opensymphony.xwork2.config.ConfigurationProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ProviderAllowlistTest {

    @Rule
    public final MockitoRule mockito = MockitoJUnit.rule();

    private ProviderAllowlist providerAllowlist;

    @Mock
    private ConfigurationProvider provider1;

    @Mock
    private ConfigurationProvider provider2;

    @Before
    public void setUp() throws Exception {
        providerAllowlist = new ProviderAllowlist();
    }

    @Test
    public void registerAllowlist() {
        providerAllowlist.registerAllowlist(provider1, new HashSet<>(asList(String.class, Integer.class)));
        providerAllowlist.registerAllowlist(provider2, new HashSet<>(asList(Double.class, Integer.class)));

        assertThat(providerAllowlist.getProviderAllowlist()).containsExactlyInAnyOrder(String.class, Integer.class, Double.class);
    }

    @Test
    public void registerAllowlist_twice() {
        providerAllowlist.registerAllowlist(provider1, new HashSet<>(asList(String.class, Integer.class)));
        providerAllowlist.registerAllowlist(provider1, new HashSet<>(asList(Double.class, Integer.class)));

        assertThat(providerAllowlist.getProviderAllowlist()).containsExactlyInAnyOrder(Integer.class, Double.class);
    }

    @Test
    public void clearAllowlist() {
        providerAllowlist.registerAllowlist(provider1, new HashSet<>(asList(String.class, Integer.class)));
        providerAllowlist.registerAllowlist(provider2, new HashSet<>(asList(Double.class, Integer.class)));

        providerAllowlist.clearAllowlist(provider1);

        assertThat(providerAllowlist.getProviderAllowlist()).containsExactlyInAnyOrder(Integer.class, Double.class);
    }

    @Test
    public void clearAllowlist_both() {
        providerAllowlist.registerAllowlist(provider1, new HashSet<>(asList(String.class, Integer.class)));
        providerAllowlist.registerAllowlist(provider2, new HashSet<>(asList(Double.class, Integer.class)));

        providerAllowlist.clearAllowlist(provider1);
        providerAllowlist.clearAllowlist(provider2);

        assertThat(providerAllowlist.getProviderAllowlist()).isEmpty();
    }
}
