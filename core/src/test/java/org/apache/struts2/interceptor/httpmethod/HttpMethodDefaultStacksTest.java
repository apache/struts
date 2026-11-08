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
package org.apache.struts2.interceptor.httpmethod;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.config.entities.InterceptorStackConfig;
import org.apache.struts2.config.entities.PackageConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Every bundled core stack that binds parameters also enforces HTTP method restrictions. */
public class HttpMethodDefaultStacksTest extends StrutsInternalTestCase {

    public void testEveryStackWithParamsAlsoHasHttpMethod() {
        PackageConfig pc = configuration.getPackageConfig("struts-default");
        Map<String, InterceptorStackConfig> stacks = allStacks(pc);
        assertTrue("expected to find bundled interceptor-stacks", stacks.size() > 5);

        List<String> violations = new ArrayList<>();
        for (Map.Entry<String, InterceptorStackConfig> e : stacks.entrySet()) {
            String name = e.getKey();
            List<String> names = e.getValue().getInterceptors().stream().map(InterceptorMapping::getName).toList();
            if (names.contains("params") && !names.contains("httpMethod")) {
                violations.add(name);
            }
        }
        assertTrue("stacks binding params but not enforcing httpMethod: " + violations, violations.isEmpty());
    }

    private Map<String, InterceptorStackConfig> allStacks(PackageConfig pc) {
        Map<String, Object> all = pc.getAllInterceptorConfigs();
        Map<String, InterceptorStackConfig> out = new java.util.LinkedHashMap<>();
        for (Map.Entry<String, Object> e : all.entrySet()) {
            if (e.getValue() instanceof InterceptorStackConfig stackConfig) {
                out.put(e.getKey(), stackConfig);
            }
        }
        return out;
    }
}
