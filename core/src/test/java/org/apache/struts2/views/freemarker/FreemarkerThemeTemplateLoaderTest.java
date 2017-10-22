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
package org.apache.struts2.views.freemarker;

import freemarker.cache.TemplateLoader;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.mockito.Matchers;

import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FreemarkerThemeTemplateLoaderTest extends StrutsInternalTestCase {

    public void testThemeExpansionToken() throws Exception {
        // given
        FreemarkerThemeTemplateLoader loader = new FreemarkerThemeTemplateLoader();
        loader.setUIThemeExpansionToken("~~~");

        TemplateEngine engine = mock(TemplateEngine.class);
        loader.setTemplateEngine(engine);

        TemplateLoader parent = mock(TemplateLoader.class);
        when(parent.findTemplateSource("template/foo/bar/text.ftl")).thenReturn(new Object());

        loader.init(parent);

        // when
        Object actual = loader.findTemplateSource("template/~~~foo/bar/text.ftl");

        // then
        assertThat(actual).isNotNull();
    }

    public void testThemeExpansionTokenWithParent() throws Exception {
        // given
        FreemarkerThemeTemplateLoader loader = new FreemarkerThemeTemplateLoader();
        loader.setUIThemeExpansionToken("~~~");

        TemplateEngine engine = mock(TemplateEngine.class);
        Map<String, String> props = new HashMap<String, String>();
        props.put("parent", "foo/foo");
        when(engine.getThemeProps(Matchers.argThat(new IsEqual<Template>(new Template("template", "foo/bar", "text.ftl"))))).thenReturn(props);
        loader.setTemplateEngine(engine);

        TemplateLoader parent = mock(TemplateLoader.class);
        when(parent.findTemplateSource("template/foo/bar/text.ftl")).thenReturn(null);
        when(parent.findTemplateSource("template/foo/foo/text.ftl")).thenReturn(new Object());

        loader.init(parent);

        // when
        Object actual = loader.findTemplateSource("template/~~~foo/bar/text.ftl");

        // then
        assertThat(actual).isNotNull();
    }

}