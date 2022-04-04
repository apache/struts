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