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
package org.apache.struts2.interceptor;

import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.ognl.ProviderAllowlist;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression for WW-5659: lazy interceptor params must still resolve when the OGNL allowlist is
 * enforced.
 * <p>
 * {@link WithLazyParams.LazyParamInjector} writes resolved values onto a per-invocation
 * {@link InterceptorParams} holder with OGNL. The holder is never named in any configuration, so
 * unlike the interceptor it is not allowlisted by
 * {@code XmlDocConfigurationProvider.allowAndLoadClass}; without an explicit registration
 * {@code SecurityMemberAccess} refuses the write and the fail-closed handling marks every param
 * unresolved, which rejects every upload.
 * <p>
 * Every other core test runs with {@code struts.allowlist.enable=false} (see
 * {@code StrutsTestCaseHelper}), so this is the only core coverage of that production setting for
 * this feature - the showcase {@code DynamicFileUploadTest} integration test is the other one.
 */
public class LazyParamsAllowlistTest extends StrutsInternalTestCase {

    /**
     * Stands in for the action whose state drives the upload rules. Allowlisted explicitly below,
     * as a real action class would be by the configuration provider that loads it.
     */
    public static class UploadRules {

        public String getAllowedMimeTypes() {
            return "text/plain";
        }

        public Long getMaxFileSize() {
            return 2048L;
        }
    }

    @Override
    protected void setUp() throws Exception {
        PrepareOperations.clearDevModeOverride();
        Map<String, String> params = new HashMap<>();
        // the shipped default, which StrutsTestCaseHelper otherwise turns off for tests
        params.put(StrutsConstants.STRUTS_ALLOWLIST_ENABLE, "true");
        // only the value-stack root needs help; the params holder must be allowlisted by the framework
        params.put(StrutsConstants.STRUTS_ALLOWLIST_CLASSES, UploadRules.class.getName());
        initDispatcher(params);
    }

    public void testAllowlistIsActuallyEnforced() {
        assertThat(container.getInstance(String.class, StrutsConstants.STRUTS_ALLOWLIST_ENABLE)).isEqualTo("true");
    }

    public void testParamsHolderHierarchyIsAllowlistedAtConfigurationTime() {
        ProviderAllowlist providerAllowlist = container.getInstance(ProviderAllowlist.class);

        assertThat(providerAllowlist.getProviderAllowlist()).contains(
                UploadPolicy.class,      // the holder itself, the OGNL target
                DisableParams.class,     // declares setDisabled
                InterceptorParams.class  // declares unresolved
        );
    }

    /**
     * The regression proper: a {@code ${...}} param must reach the holder, not be swallowed by the
     * allowlist and reported as unresolved.
     */
    public void testLazyParamsResolveOntoTheHolderWithAllowlistEnabled() {
        ActionFileUploadInterceptor interceptor = new ActionFileUploadInterceptor();
        container.inject(interceptor);

        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(new UploadRules());
        ActionContext context = ActionContext.of(valueStack.getContext())
                .withContainer(container)
                .withValueStack(valueStack)
                .bind();
        try {
            WithLazyParams.LazyParamInjector injector = new WithLazyParams.LazyParamInjector(valueStack);
            container.inject(injector);

            Map<String, String> params = new HashMap<>();
            params.put("allowedTypes", "${allowedMimeTypes}");
            params.put("maximumSize", "${maxFileSize}");

            UploadPolicy policy = injector.resolveInto(interceptor.newLazyParams(), params, context);

            assertThat(policy.getUnresolvedParams()).isEmpty();
            assertThat(policy.isUnresolved()).isFalse();
            assertThat(policy.getAllowedTypes()).containsExactly("text/plain");
            assertThat(policy.getMaximumSize()).isEqualTo(2048L);
        } finally {
            ActionContext.clear();
        }
    }
}
