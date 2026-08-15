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

import org.apache.struts2.StrutsInternalTestCase;

/**
 * Covers the {@code struts-beans.xml} registration of {@link SecurityMemberAccessConfig}, which
 * {@link SecurityMemberAccessConfigSharingTest} cannot: that test extends {@link org.apache.struts2.XWorkTestCase}
 * directly, whose container is built from {@code StrutsDefaultConfigurationProvider} alone and never loads
 * {@code struts-beans.xml}. Production, via {@link org.apache.struts2.dispatcher.Dispatcher#init()}, never adds
 * that provider and relies entirely on the {@code struts-beans.xml} entry.
 * <p>
 * {@link StrutsInternalTestCase} boots a real {@link org.apache.struts2.dispatcher.Dispatcher}, so its container
 * is wired the way production's is. Without this test, the singleton scope of the {@code struts-beans.xml}
 * entry — the entire point of WW-5675 sharing parsed configuration across {@link SecurityMemberAccess}
 * instances — could regress to {@code scope="prototype"} with the whole suite staying green.
 */
public class SecurityMemberAccessConfigProductionRegistrationTest extends StrutsInternalTestCase {

    public void testConfigBeanIsASingletonInTheProductionContainer() {
        SecurityMemberAccessConfig first = container.getInstance(SecurityMemberAccessConfig.class);
        assertNotNull("SecurityMemberAccessConfig is not registered in the production container", first);
        assertSame("SecurityMemberAccessConfig is not a singleton in the production container",
                first, container.getInstance(SecurityMemberAccessConfig.class));
    }
}
