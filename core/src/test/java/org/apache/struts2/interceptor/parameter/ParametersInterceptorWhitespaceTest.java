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
package org.apache.struts2.interceptor.parameter;

import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.conversion.impl.ConversionData;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ParametersInterceptorWhitespaceTest extends XWorkTestCase {

    private PaddedValuesAction action;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadButSet(Map.of(StrutsConstants.STRUTS_PARAMETERS_REQUIRE_ANNOTATIONS, "true"));
        action = new PaddedValuesAction();
    }

    public void testStringTargetsKeepSurroundingWhitespace() {
        apply(Map.of(
                "name", new String[]{" summer2026 "},
                "password", new String[]{" secret "},
                "text", new String[]{" chars "},
                "tags", new String[]{" a ", " b "},
                "names", new String[]{" x ", " y "},
                "indexed[0]", new String[]{" first "},
                "attributes['key']", new String[]{" value "},
                "unannotated", new String[]{" ignored "}
        ));

        assertThat(action.getName()).isEqualTo(" summer2026 ");
        assertThat(action.getPassword()).isEqualTo(" secret ");
        assertThat(action.getText()).hasToString(" chars ");
        assertThat(action.getTags()).containsExactly(" a ", " b ");
        assertThat(action.getNames()).containsExactly(" x ", " y ");
        assertThat(action.getIndexed()).containsExactly(" first ");
        assertThat(action.getAttributes()).containsEntry("key", " value ");
        assertThat(action.getUnannotated()).isNull();
        assertThat(conversionErrors()).isEmpty();
    }

    public void testObjectTargetReceivesUntrimmedValue() {
        apply(Map.of("raw", new String[]{" untouched "}));

        Object raw = action.getRaw();
        Object value = raw instanceof String[] values ? values[0] : raw;
        assertThat(value).isEqualTo(" untouched ");
        assertThat(conversionErrors()).isEmpty();
    }

    public void testUnconvertiblePaddedValueIsReportedAsConversionError() {
        apply(Map.of("count", new String[]{" abc "}, "name", new String[]{"bound"}));

        assertThat(action.getName()).isEqualTo("bound");
        assertThat(action.getCount()).isNull();
        assertThat(conversionErrors()).containsOnlyKeys("count");
    }

    public void testNonStringTargetsAreTrimmedBeforeConversion() {
        apply(Map.of(
                "count", new String[]{" 42 "},
                "amount", new String[]{" 7 "},
                "flag", new String[]{" true "},
                "numbers", new String[]{" 1 ", " 2 "}
        ));

        assertThat(action.getCount()).isEqualTo(42);
        assertThat(action.getAmount()).isEqualTo(7);
        assertThat(action.getFlag()).isTrue();
        assertThat(action.getNumbers()).containsExactly(1, 2);
        assertThat(conversionErrors()).isEmpty();
    }

    public void testBlankBoxedBooleanBecomesNull() {
        apply(Map.of("flag", new String[]{"   "}, "name", new String[]{"bound"}));

        assertThat(action.getName()).isEqualTo("bound");
        assertThat(action.getFlag()).isNull();
        assertThat(conversionErrors()).isEmpty();
    }

    public void testEmptyBoxedBooleanBecomesNull() {
        apply(Map.of("flag", new String[]{""}, "name", new String[]{"bound"}));

        assertThat(action.getName()).isEqualTo("bound");
        assertThat(action.getFlag()).isNull();
        assertThat(conversionErrors()).isEmpty();
    }

    private void apply(Map<String, Object> params) {
        ParametersInterceptor interceptor = new ParametersInterceptor();
        container.inject(interceptor);

        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.push(action);
        ActionContext.of(stack.getContext()).withContainer(container).withValueStack(stack).bind();

        interceptor.applyParameters(action, stack, HttpParameters.create(params).build());
    }

    private Map<String, ConversionData> conversionErrors() {
        return ActionContext.getContext().getConversionErrors();
    }

    public static class PaddedValuesAction {
        private String name;
        private String password;
        private CharSequence text;
        private Object raw;
        private String[] tags;
        private List<String> names;
        private List<String> indexed = new ArrayList<>();
        private Map<String, String> attributes = new HashMap<>();
        private Integer count;
        private int amount;
        private Boolean flag;
        private List<Integer> numbers;
        private String unannotated;

        public String getUnannotated() {
            return unannotated;
        }

        public void setUnannotated(String unannotated) {
            this.unannotated = unannotated;
        }

        public String getName() {
            return name;
        }

        @StrutsParameter
        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        @StrutsParameter
        public void setPassword(String password) {
            this.password = password;
        }

        public CharSequence getText() {
            return text;
        }

        @StrutsParameter
        public void setText(CharSequence text) {
            this.text = text;
        }

        public Object getRaw() {
            return raw;
        }

        @StrutsParameter
        public void setRaw(Object raw) {
            this.raw = raw;
        }

        public String[] getTags() {
            return tags;
        }

        @StrutsParameter
        public void setTags(String[] tags) {
            this.tags = tags;
        }

        public List<String> getNames() {
            return names;
        }

        @StrutsParameter
        public void setNames(List<String> names) {
            this.names = names;
        }

        @StrutsParameter(depth = 1)
        public List<String> getIndexed() {
            return indexed;
        }

        @StrutsParameter(depth = 1)
        public Map<String, String> getAttributes() {
            return attributes;
        }

        public Integer getCount() {
            return count;
        }

        @StrutsParameter
        public void setCount(Integer count) {
            this.count = count;
        }

        public int getAmount() {
            return amount;
        }

        @StrutsParameter
        public void setAmount(int amount) {
            this.amount = amount;
        }

        public Boolean getFlag() {
            return flag;
        }

        @StrutsParameter
        public void setFlag(Boolean flag) {
            this.flag = flag;
        }

        public List<Integer> getNumbers() {
            return numbers;
        }

        @StrutsParameter
        public void setNumbers(List<Integer> numbers) {
            this.numbers = numbers;
        }
    }
}
