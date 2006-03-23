package com.opensymphony.webwork.views;

import com.opensymphony.webwork.components.template.*;
import com.opensymphony.webwork.config.Configuration;
import junit.framework.TestCase;

/**
 * TemplateEngineManagerTest
 * Date: Sep 28, 2004 12:22:05 PM
 *
 * @author jcarreira
 */
public class TemplateEngineManagerTest extends TestCase {
    public void testTemplateTypeFromTemplateNameAndDefaults() {
        Configuration.setConfiguration(new Configuration() {
            public boolean isSetImpl(String name) {
                return name.equals(TemplateEngineManager.DEFAULT_TEMPLATE_TYPE_CONFIG_KEY);
            }

            public Object getImpl(String aName) throws IllegalArgumentException {
                if (aName.equals(TemplateEngineManager.DEFAULT_TEMPLATE_TYPE_CONFIG_KEY)) {
                    return "jsp";
                }
                return null;
            }
        });
        TemplateEngine engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo"), null);
        assertTrue(engine instanceof JspTemplateEngine);
        engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo.vm"), null);
        assertTrue(engine instanceof VelocityTemplateEngine);
    }

    public void testTemplateTypeOverrides() {
        TemplateEngine engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo"), "ftl");
        assertTrue(engine instanceof FreemarkerTemplateEngine);
        engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo.vm"), "ftl");
        assertTrue(engine instanceof VelocityTemplateEngine);
        engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo.ftl"), "");
        assertTrue(engine instanceof FreemarkerTemplateEngine);
    }

    public void testTemplateTypeUsesDefaultWhenNotSetInConfiguration() {
        TemplateEngine engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo"), null);
        Template template = new Template("/template", "simple", "foo." + TemplateEngineManager.DEFAULT_TEMPLATE_TYPE);
        TemplateEngine defaultTemplateEngine = TemplateEngineManager.getTemplateEngine(template, null);
        assertTrue(engine.getClass().equals(defaultTemplateEngine.getClass()));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        Configuration.setConfiguration(null);
    }
}
