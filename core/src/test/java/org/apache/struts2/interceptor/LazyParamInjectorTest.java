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
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LazyParamInjectorTest extends StrutsInternalTestCase {

    public static class Holder extends DisableParams {
        private String name;
        private Long size;
        private final List<String> unresolvedCalls = new ArrayList<>();

        public void setName(String name) { this.name = name; }
        public void setSize(Long size) { this.size = size; }
        public String getName() { return name; }
        public Long getSize() { return size; }
        public List<String> getUnresolvedCalls() { return unresolvedCalls; }

        @Override
        public void unresolved(String paramName) { unresolvedCalls.add(paramName); }
    }

    public static class Bean {
        public String getLabel() { return "resolved-label"; }
        public Long getLimit() { return 4096L; }
        public String getBlank() { return ""; }
        public String getNotANumber() { return "5MB"; }
    }

    private ActionContext context;
    private WithLazyParams.LazyParamInjector injector;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.push(new Bean());
        context = ActionContext.of(stack.getContext()).withContainer(container).withValueStack(stack).bind();
        injector = new WithLazyParams.LazyParamInjector(stack);
        container.inject(injector);
    }

    @Override
    protected void tearDown() throws Exception {
        ActionContext.clear();
        super.tearDown();
    }

    public void testResolvesExpressionsIntoTheHolder() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "${label}");

        Holder holder = injector.resolveInto(new Holder(), params, context);

        assertThat(holder.getName()).isEqualTo("resolved-label");
        assertThat(holder.getUnresolvedCalls()).isEmpty();
    }

    public void testAppliesOgnlTypeConversion() {
        Map<String, String> params = new HashMap<>();
        params.put("size", "${limit}");

        Holder holder = injector.resolveInto(new Holder(), params, context);

        assertThat(holder.getSize()).isEqualTo(4096L);
    }

    public void testPassesLiteralValuesThrough() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "plain-text");

        Holder holder = injector.resolveInto(new Holder(), params, context);

        assertThat(holder.getName()).isEqualTo("plain-text");
        assertThat(holder.getUnresolvedCalls()).isEmpty();
    }

    public void testUnresolvableExpressionSkipsWriteAndNotifiesHolder() {
        Holder seeded = new Holder();
        seeded.setName("seeded-value");

        Map<String, String> params = new HashMap<>();
        params.put("name", "${noSuchProperty}");

        Holder holder = injector.resolveInto(seeded, params, context);

        assertThat(holder.getName()).isEqualTo("seeded-value");
        assertThat(holder.getUnresolvedCalls()).containsExactly("name");
    }

    public void testExpressionResolvingToEmptyIsTreatedAsUnresolved() {
        Holder seeded = new Holder();
        seeded.setName("seeded-value");

        Map<String, String> params = new HashMap<>();
        params.put("name", "${blank}");

        Holder holder = injector.resolveInto(seeded, params, context);

        assertThat(holder.getName()).isEqualTo("seeded-value");
        assertThat(holder.getUnresolvedCalls()).containsExactly("name");
    }

    public void testValueThatCannotBeConvertedSkipsWriteAndNotifiesHolder() {
        Map<String, String> params = new HashMap<>();
        params.put("size", "${notANumber}");

        Holder holder = injector.resolveInto(new Holder(), params, context);

        assertThat(holder.getSize()).isNull();
        assertThat(holder.getUnresolvedCalls()).containsExactly("size");
    }

    public void testResolvesDisabledOntoDisableParams() {
        Map<String, String> params = new HashMap<>();
        params.put("disabled", "true");

        Holder holder = injector.resolveInto(new Holder(), params, context);

        assertThat(holder.isDisabled()).isTrue();
    }

    public void testUnknownParamDoesNotFailTheInvocationButNotifiesHolder() {
        Map<String, String> params = new HashMap<>();
        params.put("noSuchParam", "whatever");

        Holder holder = injector.resolveInto(new Holder(), params, context);

        assertThat(holder.getName()).isNull();
        assertThat(holder.getSize()).isNull();
        assertThat(holder.getUnresolvedCalls()).containsExactly("noSuchParam");
    }
}
