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

import org.apache.struts2.ognl.DefaultOgnlBeanInfoCacheFactory;
import org.apache.struts2.ognl.DefaultOgnlExpressionCacheFactory;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.ognl.StrutsOgnlGuard;
import org.apache.struts2.ognl.StrutsProxyCacheFactory;
import org.apache.struts2.ognl.ThreadAllowlist;
import org.apache.struts2.util.StrutsProxyService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.struts2.ognl.OgnlCacheFactory.CacheType.LRU;
import static org.assertj.core.api.Assertions.assertThat;

public class OgnlParameterAllowlisterTest {

    private OgnlParameterAllowlister allowlister;
    private RecordingThreadAllowlist threadAllowlist;

    @Before
    public void setUp() {
        threadAllowlist = new RecordingThreadAllowlist();
        allowlister = new OgnlParameterAllowlister();
        var ognlUtil = new OgnlUtil(
                new DefaultOgnlExpressionCacheFactory<>(String.valueOf(1000), LRU.toString()),
                new DefaultOgnlBeanInfoCacheFactory<>(String.valueOf(1000), LRU.toString()),
                new StrutsOgnlGuard());
        allowlister.setOgnlUtil(ognlUtil);
        allowlister.setProxyService(new StrutsProxyService(new StrutsProxyCacheFactory<>("1000", "basic")));
        allowlister.setThreadAllowlist(threadAllowlist);
    }

    @After
    public void tearDown() {
        threadAllowlist.clear();
    }

    @Test
    public void depthZero_isNoOp() {
        var target = new TargetWithAnnotatedNestedBean();
        allowlister.primeAllowlistForPath("simple", target);
        assertThat(threadAllowlist.classes).isEmpty();
    }

    @Test
    public void nestedProperty_allowlistsPropertyType() {
        var target = new TargetWithAnnotatedNestedBean();
        allowlister.primeAllowlistForPath("nested.field", target);
        assertThat(threadAllowlist.classes).contains(NestedBean.class);
    }

    @Test
    public void parameterizedReturn_allowlistsTypeArguments() {
        var target = new TargetWithAnnotatedNestedBean();
        allowlister.primeAllowlistForPath("things[0].field", target);
        assertThat(threadAllowlist.classes).contains(List.class, NestedBean.class);
    }

    @Test
    public void publicField_isAllowlistedWhenNoGetter() {
        var target = new TargetWithAnnotatedPublicField();
        allowlister.primeAllowlistForPath("publicNested.field", target);
        assertThat(threadAllowlist.classes).contains(NestedBean.class);
    }

    @Test
    public void unmatchedRoot_isNoOp() {
        var target = new TargetWithAnnotatedNestedBean();
        allowlister.primeAllowlistForPath("unknownRoot.field", target);
        assertThat(threadAllowlist.classes).isEmpty();
    }

    @Test
    public void unannotatedNested_isNoOp() {
        var target = new TargetWithUnannotatedNested();
        allowlister.primeAllowlistForPath("unannotated.field", target);
        assertThat(threadAllowlist.classes).isEmpty();
    }

    public static class TargetWithAnnotatedNestedBean {
        private NestedBean nested;
        private List<NestedBean> things;

        @StrutsParameter(depth = 1)
        public NestedBean getNested() { return nested; }
        public void setNested(NestedBean nested) { this.nested = nested; }

        @StrutsParameter(depth = 2)
        public List<NestedBean> getThings() { return things; }
        public void setThings(List<NestedBean> things) { this.things = things; }
    }

    public static class TargetWithAnnotatedPublicField {
        @StrutsParameter(depth = 1)
        public NestedBean publicNested;
    }

    public static class TargetWithUnannotatedNested {
        private NestedBean unannotated;
        public NestedBean getUnannotated() { return unannotated; }
        public void setUnannotated(NestedBean v) { this.unannotated = v; }
    }

    public static class NestedBean {
        private String field;
        public String getField() { return field; }
        public void setField(String f) { this.field = f; }
    }

    private static final class RecordingThreadAllowlist extends ThreadAllowlist {
        final Set<Class<?>> classes = new HashSet<>();

        @Override
        public void allowClass(Class<?> clazz) {
            classes.add(clazz);
            super.allowClass(clazz);
        }

        @Override
        public void allowClassHierarchy(Class<?> clazz) {
            classes.add(clazz);
            super.allowClassHierarchy(clazz);
        }

        void clear() {
            classes.clear();
            clearAllowlist();
        }
    }
}
