package org.apache.struts2.views.java.simple;

import org.apache.struts2.components.UIBean;

public abstract class AbstractCommonAttributesTest extends AbstractTest {
    public void testRenderTextFieldScriptingAttrs() throws Exception {
        UIBean tag = getUIBean();

        applyScriptingAttrs(tag);

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();

        assertScriptingAttrs(output);
    }

    public void testRenderTextFieldCommonAttrs() throws Exception {
        UIBean tag = getUIBean();


        applyCommonAttrs(tag);

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();

        assertCommonAttrs(output);
    }

    public void testRenderTextFieldDynamicAttrs() throws Exception {
        UIBean tag = getUIBean();

        applyDynamicAttrs(tag);

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();

        assertDynamicAttrs(output);
    }
}
