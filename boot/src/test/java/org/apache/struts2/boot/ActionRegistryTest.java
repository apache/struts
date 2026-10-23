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
package org.apache.struts2.boot;

import org.apache.struts2.boot.ActionDefinition.ResultDefinition;
import org.apache.struts2.boot.ActionDefinition.ResultDefinition.Kind;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.interceptor.httpmethod.HttpMethod;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ActionRegistryTest {

    @Test
    public void testTopLevelActionGoesToEmptyNamespace() {
        ActionRegistry r = new ActionRegistry();
        r.action("hello", Object.class).success("hello.ftl");
        assertThat(r.namespaces()).extracting(NamespaceActions::namespace).containsExactly("");
        ActionDefinition a = r.namespaces().iterator().next().actions().get(0);
        assertThat(a.methodName()).isEqualTo("execute");
        assertThat(a.results()).containsEntry("success", new ResultDefinition(Kind.TEMPLATE, "hello.ftl"));
    }

    @Test
    public void testNamespaceDeclaredTwiceMerges() {
        ActionRegistry r = new ActionRegistry();
        r.namespace("/admin", n -> n.action("a", Object.class));
        r.namespace("/admin", n -> n.action("b", Object.class));
        assertThat(r.namespaces()).hasSize(1);
        assertThat(r.namespaces().iterator().next().actions()).extracting(ActionDefinition::name).containsExactly("a", "b");
    }

    @Test
    public void testDuplicateAcrossRepeatedNamespaceThrows() {
        ActionRegistry r = new ActionRegistry();
        r.namespace("/admin", n -> n.action("a", Object.class));
        assertThatThrownBy(() -> r.namespace("/admin", n -> n.action("a", Object.class)))
                .isInstanceOf(ConfigurationException.class).hasMessageContaining("/admin").hasMessageContaining("a");
    }

    @Test
    public void testDuplicateTopLevelThrows() {
        ActionRegistry r = new ActionRegistry();
        r.action("hello", Object.class);
        assertThatThrownBy(() -> r.action("hello", Object.class))
                .isInstanceOf(ConfigurationException.class).hasMessageContaining("hello");
    }

    @Test
    public void testSlashInActionNameThrows() {
        ActionRegistry r = new ActionRegistry();
        assertThatThrownBy(() -> r.action("users/save", Object.class))
                .isInstanceOf(ConfigurationException.class).hasMessageContaining("namespace");
    }

    @Test
    public void testNonFtlTemplateThrows() {
        ActionRegistry r = new ActionRegistry();
        assertThatThrownBy(() -> r.action("hello", Object.class).success("hello.jsp"))
                .isInstanceOf(ConfigurationException.class).hasMessageContaining("FreeMarker");
    }

    @Test
    public void testFluentCallsAreRecorded() {
        ActionDefinition a = new ActionRegistry().action("save", Object.class)
                .method("save").httpMethods(HttpMethods.POST)
                .result("input", "form.ftl").redirectAction("success", "users").interceptors("paramsPrepareParamsStack");
        assertThat(a.methodName()).isEqualTo("save");
        assertThat(a.httpMethodSet()).containsExactly(HttpMethod.POST);
        assertThat(a.results()).containsEntry("success", new ResultDefinition(Kind.REDIRECT_ACTION, "users"));
        assertThat(a.interceptorStack()).contains("paramsPrepareParamsStack");
    }
}
