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

import ognl.ClassResolver;
import ognl.MemberAccess;
import ognl.TypeConverter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
public class StrutsContextTest {

    @Test
    public void shouldCreateContextWithRequiredMemberAccess() {
        MemberAccess<StrutsContext> memberAccess = mock(MemberAccess.class);
        var context = new StrutsContext(memberAccess);

        assertThat(context).isNotNull();
        assertThat(context.getMemberAccess()).isSameAs(memberAccess);
    }

    @Test
    public void shouldCreateContextWithAllComponents() {
        MemberAccess<StrutsContext> memberAccess = mock(MemberAccess.class);
        ClassResolver<StrutsContext> classResolver = mock(ClassResolver.class);
        TypeConverter<StrutsContext> typeConverter = mock(TypeConverter.class);

        var context = new StrutsContext(memberAccess, classResolver, typeConverter);

        assertThat(context.getMemberAccess()).isSameAs(memberAccess);
        assertThat(context.getClassResolver()).isSameAs(classResolver);
        assertThat(context.getTypeConverter()).isSameAs(typeConverter);
    }

    @Test
    public void shouldSupportRootObject() {
        MemberAccess<StrutsContext> memberAccess = mock(MemberAccess.class);
        var root = new Object();
        var context = new StrutsContext(memberAccess);
        context.withRoot(root);

        assertThat(context.getRoot()).isSameAs(root);
    }

    @Test
    public void shouldImplementMapInterface() {
        MemberAccess<StrutsContext> memberAccess = mock(MemberAccess.class);
        var context = new StrutsContext(memberAccess);

        context.put("testKey", "testValue");
        assertThat(context.get("testKey")).isEqualTo("testValue");
    }
}
