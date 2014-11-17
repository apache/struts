/*
 * $Id$
 *
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

package org.apache.struts2.components;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;
import org.apache.struts2.components.template.TemplateEngineManager;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.util.TextProviderHelper;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.util.ContextUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * UIBean is the standard superclass of all Struts UI components.
 * It defines common Struts and html properties all UI components should present for usage.
 *
 * <!-- START SNIPPET: templateRelatedAttributes -->
 *
 * <table border="1">
 *    <thead>
 *       <tr>
 *          <td>Attribute</td>
 *          <td>Theme</td>
 *          <td>Data Types</td>
 *          <td>Description</td>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>templateDir</td>
 *          <td>n/a</td>
 *          <td>String</td>
 *          <td>define the template directory</td>
 *       </td>
 *       <tr>
 *          <td>theme</td>
 *          <td>n/a</td>
 *          <td>String</td>
 *          <td>define the theme name</td>
 *       </td>
 *       <tr>
 *          <td>template</td>
 *          <td>n/a</td>
 *          <td>String</td>
 *          <td>define the template name</td>
 *       </td>
 *       <tr>
 *          <td>themeExpansionToken</td>
 *          <td>n/a</td>
 *          <td>String</td>
 *          <td>special token (defined with struts.ui.theme.expansion.token) used to search for template in parent theme
 *          (don't use it separately!)</td>
 *       </td>
 *       <tr>
 *          <td>expandTheme</td>
 *          <td>n/a</td>
 *          <td>String</td>
 *          <td>concatenation of themeExpansionToken and theme which tells internal template loader mechanism
 *          to try load template from current theme and then from parent theme (and parent theme, and so on)
 *          when used with &lt;#include/&gt; directive</td>
 *       </td>
 *    </tbody>
 * </table>
 *
 * <!-- END SNIPPET: templateRelatedAttributes -->
 *
 * <p/>
 *
 * <!-- START SNIPPET: generalAttributes -->
 *
 * <table border="1">
 *    <thead>
 *       <tr>
 *          <td>Attribute</td>
 *          <td>Theme</td>
 *          <td>Data Types</td>
 *          <td>Description</td>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>cssClass</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>define html class attribute</td>
 *       </tr>
 *       <tr>
 *          <td>cssStyle</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>define html style attribute</td>
 *       </tr>
 *       <tr>
 *          <td>cssClass</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>error class attribute</td>
 *       </tr>
 *       <tr>
 *          <td>cssStyle</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>error style attribute</td>
 *       </tr>
 *       <tr>
 *          <td>title</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>define html title attribute</td>
 *       </tr>
 *       <tr>
 *          <td>disabled</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>define html disabled attribute</td>
 *       </tr>
 *       <tr>
 *          <td>label</td>
 *          <td>xhtml</td>
 *          <td>String</td>
 *          <td>define label of form element</td>
 *       </tr>
 *       <tr>
 *          <td>labelPosition</td>
 *          <td>xhtml</td>
 *          <td>String</td>
 *          <td>define label position of form element (top/left), default to left</td>
 *       </tr>
 *       <tr>
 *          <td>requiredPosition</td>
 *          <td>xhtml</td>
 *          <td>String</td>
 *          <td>define required label position of form element (left/right), default to right</td>
 *       </tr>
  *       <tr>
 *          <td>errorPosition</td>
 *          <td>xhtml</td>
 *          <td>String</td>
 *          <td>define error position of form element (top|bottom), default to top</td>
 *       </tr>
 *       <tr>
 *          <td>name</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>Form Element's field name mapping</td>
 *       </tr>
 *       <tr>
 *          <td>required</td>
 *          <td>xhtml</td>
 *          <td>Boolean</td>
 *          <td>add * to label (true to add false otherwise)</td>
 *       </tr>
 *       <tr>
 *          <td>tabIndex</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>define html tabindex attribute</td>
 *       </tr>
 *       <tr>
 *          <td>value</td>
 *          <td>simple</td>
 *          <td>Object</td>
 *          <td>define value of form element</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <!-- END SNIPPET: generalAttributes -->
 *
 * <p/>
 *
 * <!-- START SNIPPET: javascriptRelatedAttributes -->
 *
 * <table border="1">
 *    <thead>
 *       <tr>
 *          <td>Attribute</td>
 *          <td>Theme</td>
 *          <td>Data Types</td>
 *          <td>Description</td>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>onclick</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onclick attribute</td>
 *       </tr>
 *       <tr>
 *          <td>ondblclick</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript ondbclick attribute</td>
 *       </tr>
 *       <tr>
 *          <td>onmousedown</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onmousedown attribute</td>
 *       </tr>
 *       <tr>
 *          <td>onmouseup</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onmouseup attribute</td>
 *       </tr>
 *       <tr>
 *          <td>onmouseover</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onmouseover attribute</td>
 *       </tr>
 *       <tr>
 *          <td>onmouseout</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onmouseout attribute</td>
 *       </tr>
 *       <tr>
 *          <td>onfocus</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onfocus attribute</td>
 *       </tr>
 *       <tr>
 *          <td>onblur</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onblur attribute</td>
 *       </tr>
 *       <tr>
 *          <td>onkeypress</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onkeypress attribute</td>
 *       </tr>
 *       <tr>
 *          <td>onkeyup</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onkeyup attribute</td>
 *       </tr>
 *       <tr>
 *          <td>onkeydown</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onkeydown attribute</td>
 *       </tr>
 *       <tr>
 *          <td>onselect</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onselect attribute</td>
 *       </tr>
 *       <tr>
 *          <td>onchange</td>
 *          <td>simple</td>
 *          <td>String</td>
 *          <td>html javascript onchange attribute</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <!-- END SNIPPET: javascriptRelatedAttributes -->
 *
 * <p/>
 *
 * <!-- START SNIPPET: tooltipattributes -->
 *
 * <table border="1">
 *  <tr>
 *     <td>Attribute</td>
 *     <td>Data Type</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *  </tr>
 *  <tr>
 *      <td>tooltip</td>
 *      <td>String</td>
 *      <td>none</td>
 *      <td>Set the tooltip of this particular component</td>
 *  </tr>
 *  <tr>
 *      <td>jsTooltipEnabled</td>
 *      <td>String</td>
 *      <td>false</td>
 *      <td>Enable js tooltip rendering</td>
 *  </tr>
 *    <tr>
 *      <td>tooltipIcon</td>
 *      <td>String</td>
 *      <td>/struts/static/tooltip/tooltip.gif</td>
 *      <td>The url to the tooltip icon</td>
 *   <tr>
 *      <td>tooltipDelay</td>
 *      <td>String</td>
 *      <td>500</td>
 *      <td>Tooltip shows up after the specified timeout (miliseconds). A behavior similar to that of OS based tooltips.</td>
 *   </tr>
 *   <tr>
 *      <td>key</td>
 *      <td>simple</td>
 *      <td>String</td>
 *      <td>The name of the property this input field represents.  This will auto populate the name, label, and value</td>
 *   </tr>
 * </table>
 *
 * <!-- END SNIPPET: tooltipattributes -->
 *
 *
 * <!-- START SNIPPET: tooltipdescription -->
 * <b>tooltipConfig is deprecated, use individual tooltip configuration attributes instead </b>
 *
 * Every Form UI component (in xhtml / css_xhtml or any other that extends them) can
 * have tooltips assigned to them. The Form component's tooltip related attribute, once
 * defined, will be applied to all form UI components that are created under it unless
 * explicitly overriden by having the Form UI component itself defined with their own tooltip attribute.
 *
 * <p/>
 *
 * In Example 1, the textfield will inherit the tooltipDelay and tooltipIconPath attribte from
 * its containing form. In other words, although it doesn't define a tooltipIconPath
 * attribute, it will have that attribute inherited from its containing form.
 *
 * <p/>
 *
 * In Example 2, the  textfield will inherite both the tooltipDelay and
 * tooltipIconPath attribute from its containing form, but the tooltipDelay
 * attribute is overriden at the textfield itself. Hence, the textfield actually will
 * have its tooltipIcon defined as /myImages/myIcon.gif, inherited from its containing form, and
 * tooltipDelay defined as 5000.
 *
 * <p/>
 *
 * Example 3, 4 and 5 show different ways of setting the tooltip configuration attribute.<br/>
 * <b>Example 3:</b> Set tooltip config through the body of the param tag<br/>
 * <b>Example 4:</b> Set tooltip config through the value attribute of the param tag<br/>
 * <b>Example 5:</b> Set tooltip config through the tooltip attributes of the component tag<br/>
 *
 * <!-- END SNIPPET: tooltipdescription -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: tooltipexample -->
 *
 * &lt;!-- Example 1: --&gt;
 * &lt;s:form
 *          tooltipDelay="500"
 *          tooltipIconPath="/myImages/myIcon.gif" .... &gt;
 *   ....
 *     &lt;s:textfield label="Customer Name" tooltip="Enter the customer name" .... /&gt;
 *   ....
 * &lt;/s:form&gt;
 *
 * &lt;!-- Example 2: --&gt;
 * &lt;s:form
 *          tooltipDelay="500"
 *          tooltipIconPath="/myImages/myIcon.gif" .... &gt;
 *   ....
 *     &lt;s:textfield label="Address"
 *          tooltip="Enter your address"
 *          tooltipDelay="5000" /&gt;
 *   ....
 * &lt;/s:form&gt;
 *
 *
 * &lt;-- Example 3: --&gt;
 * &lt;s:textfield
 *        label="Customer Name"
 *        tooltip="One of our customer Details"&gt;
 *        &lt;s:param name="tooltipDelay"&gt;
 *             500
 *        &lt;/s:param&gt;
 *        &lt;s:param name="tooltipIconPath"&gt;
 *             /myImages/myIcon.gif
 *        &lt;/s:param&gt;
 * &lt;/s:textfield&gt;
 *
 *
 * &lt;-- Example 4: --&gt;
 * &lt;s:textfield
 *          label="Customer Address"
 *          tooltip="Enter The Customer Address" &gt;
 *          &lt;s:param
 *              name="tooltipDelay"
 *              value="500" /&gt;
 * &lt;/s:textfield&gt;
 *
 *
 * &lt;-- Example 5: --&gt;
 * &lt;s:textfield
 *          label="Customer Telephone Number"
 *          tooltip="Enter customer Telephone Number"
 *          tooltipDelay="500"
 *          tooltipIconPath="/myImages/myIcon.gif" /&gt;
 *
 * <!-- END SNIPPET: tooltipexample -->
 * </pre>
 *
 */
public abstract class UIBean extends Component {
    private static final Logger LOG = LoggerFactory.getLogger(UIBean.class);

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    public UIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack);
        this.request = request;
        this.response = response;
        this.templateSuffix = ContextUtil.getTemplateSuffix(stack.getContext());
    }

    // The templateSuffic to use, overrides the default one if not null.
    protected String templateSuffix;

    // The template to use, overrides the default one.
    protected String template;

    // templateDir and theme attributes
    protected String templateDir;
    protected String theme;

    // shortcut, sets label, name, and value
    protected String key;

    protected String id;
    protected String cssClass;
    protected String cssStyle;
    protected String cssErrorClass;
    protected String cssErrorStyle;
    protected String disabled;
    protected String label;
    protected String labelPosition;
    protected String labelSeparator;
    protected String requiredPosition;
    protected String errorPosition;
    protected String name;
    protected String requiredLabel;
    protected String tabindex;
    protected String value;
    protected String title;

    // HTML scripting events attributes
    protected String onclick;
    protected String ondblclick;
    protected String onmousedown;
    protected String onmouseup;
    protected String onmouseover;
    protected String onmousemove;
    protected String onmouseout;
    protected String onfocus;
    protected String onblur;
    protected String onkeypress;
    protected String onkeydown;
    protected String onkeyup;
    protected String onselect;
    protected String onchange;

    // common html attributes
    protected String accesskey;

    // javascript tooltip attribute
    protected String tooltip;
    protected String tooltipConfig;
    protected String javascriptTooltip;
    protected String tooltipDelay;
    protected String tooltipCssClass;
    protected String tooltipIconPath;

    // dynamic attributes
    protected Map<String,Object> dynamicAttributes = new HashMap<String,Object>();

    protected String defaultTemplateDir;
    protected String defaultUITheme;
    protected String uiThemeExpansionToken;
    protected TemplateEngineManager templateEngineManager;

    @Inject(StrutsConstants.STRUTS_UI_TEMPLATEDIR)
    public void setDefaultTemplateDir(String dir) {
        this.defaultTemplateDir = dir;
    }

    @Inject(StrutsConstants.STRUTS_UI_THEME)
    public void setDefaultUITheme(String theme) {
        this.defaultUITheme = theme;
    }

    @Inject(StrutsConstants.STRUTS_UI_THEME_EXPANSION_TOKEN)
    public void setUIThemeExpansionToken(String uiThemeExpansionToken) {
        this.uiThemeExpansionToken = uiThemeExpansionToken;
    }

    @Inject
    public void setTemplateEngineManager(TemplateEngineManager mgr) {
        this.templateEngineManager = mgr;
    }

    public boolean end(Writer writer, String body) {
        evaluateParams();
        try {
            super.end(writer, body, false);
            mergeTemplate(writer, buildTemplateName(template, getDefaultTemplate()));
        } catch (Exception e) {
            throw new StrutsException(e);
        }
        finally {
            popComponentStack();
        }

        return false;
    }

    /**
     * A contract that requires each concrete UI Tag to specify which template should be used as a default.  For
     * example, the CheckboxTab might return "checkbox.vm" while the RadioTag might return "radio.vm".  This value
     * <strong>not</strong> begin with a '/' unless you intend to make the path absolute rather than relative to the
     * current theme.
     *
     * @return The name of the template to be used as the default.
     */
    protected abstract String getDefaultTemplate();

    protected Template buildTemplateName(String myTemplate, String myDefaultTemplate) {
        String template = myDefaultTemplate;

        if (myTemplate != null) {
            template = findString(myTemplate);
        }

        String templateDir = getTemplateDir();
        String theme = getTheme();

        return new Template(templateDir, theme, template);

    }

    protected void mergeTemplate(Writer writer, Template template) throws Exception {
        final TemplateEngine engine = templateEngineManager.getTemplateEngine(template, templateSuffix);
        if (engine == null) {
            throw new ConfigurationException("Unable to find a TemplateEngine for template " + template);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Rendering template " + template);
        }

        final TemplateRenderingContext context = new TemplateRenderingContext(template, writer, getStack(), getParameters(), this);
        engine.renderTemplate(context);
    }

    public String getTemplateDir() {
        String templateDir = null;

        if (this.templateDir != null) {
            templateDir = findString(this.templateDir);
        }

        // If templateDir is not explicitly given,
        // try to find attribute which states the dir set to use
        if ((templateDir == null) || (templateDir.equals(""))) {
            templateDir = stack.findString("#attr.templateDir");
        }

        // Default template set
        if ((templateDir == null) || (templateDir.equals(""))) {
            templateDir = defaultTemplateDir;
        }

        // Defaults to 'template'
        if ((templateDir == null) || (templateDir.equals(""))) {
            templateDir = "template";
        }

        return templateDir;
    }

    public String getTheme() {
        String theme = null;

        if (this.theme != null) {
            theme = findString(this.theme);
        }

        if ( theme == null || theme.equals("") ) {
            Form form = (Form) findAncestor(Form.class);
            if (form != null) {
                theme = form.getTheme();
            }
        }

        // If theme set is not explicitly given,
        // try to find attribute which states the theme set to use
        if ((theme == null) || (theme.equals(""))) {
            theme = stack.findString("#attr.theme");
        }

        // Default theme set
        if ((theme == null) || (theme.equals(""))) {
            theme = defaultUITheme;
        }

        return theme;
    }

    public void evaluateParams() {
        String templateDir = getTemplateDir();
        String theme = getTheme();
        
        addParameter("templateDir", templateDir);
        addParameter("theme", theme);
        addParameter("template", template != null ? findString(template) : getDefaultTemplate());
        addParameter("dynamicAttributes", dynamicAttributes);
        addParameter("themeExpansionToken", uiThemeExpansionToken);
        addParameter("expandTheme", uiThemeExpansionToken + theme);

        String name = null;
        String providedLabel = null;

        if (this.key != null) {

            if(this.name == null) {
                this.name = key;
            }

            if(this.label == null) {
                // lookup the label from a TextProvider (default value is the key)
                providedLabel = TextProviderHelper.getText(key, key, stack);
            }

        }

        if (this.name != null) {
            name = findString(this.name);
            addParameter("name", name);
        }

        if (label != null) {
            addParameter("label", findString(label));
        } else {
            if (providedLabel != null) {
                // label found via a TextProvider
                addParameter("label", providedLabel);
            }
        }

        if (labelSeparator != null) {
            addParameter("labelseparator", findString(labelSeparator));
        }

        if (labelPosition != null) {
            addParameter("labelposition", findString(labelPosition));
        }

        if (requiredPosition != null) {
            addParameter("requiredPosition", findString(requiredPosition));
        }

        if (errorPosition != null) {
            addParameter("errorposition", findString(errorPosition));
        }
        
        if (requiredLabel != null) {
            addParameter("required", findValue(requiredLabel, Boolean.class));
        }

        if (disabled != null) {
            addParameter("disabled", findValue(disabled, Boolean.class));
        }

        if (tabindex != null) {
            addParameter("tabindex", findString(tabindex));
        }

        if (onclick != null) {
            addParameter("onclick", findString(onclick));
        }

        if (ondblclick != null) {
            addParameter("ondblclick", findString(ondblclick));
        }

        if (onmousedown != null) {
            addParameter("onmousedown", findString(onmousedown));
        }

        if (onmouseup != null) {
            addParameter("onmouseup", findString(onmouseup));
        }

        if (onmouseover != null) {
            addParameter("onmouseover", findString(onmouseover));
        }

        if (onmousemove != null) {
            addParameter("onmousemove", findString(onmousemove));
        }

        if (onmouseout != null) {
            addParameter("onmouseout", findString(onmouseout));
        }

        if (onfocus != null) {
            addParameter("onfocus", findString(onfocus));
        }

        if (onblur != null) {
            addParameter("onblur", findString(onblur));
        }

        if (onkeypress != null) {
            addParameter("onkeypress", findString(onkeypress));
        }

        if (onkeydown != null) {
            addParameter("onkeydown", findString(onkeydown));
        }

        if (onkeyup != null) {
            addParameter("onkeyup", findString(onkeyup));
        }

        if (onselect != null) {
            addParameter("onselect", findString(onselect));
        }

        if (onchange != null) {
            addParameter("onchange", findString(onchange));
        }

        if (accesskey != null) {
            addParameter("accesskey", findString(accesskey));
        }

        if (cssClass != null) {
            addParameter("cssClass", findString(cssClass));
        }

        if (cssStyle != null) {
            addParameter("cssStyle", findString(cssStyle));
        }

        if (cssErrorClass != null) {
            addParameter("cssErrorClass", findString(cssErrorClass));
        }

        if (cssErrorStyle != null) {
            addParameter("cssErrorStyle", findString(cssErrorStyle));
        }

        if (title != null) {
            addParameter("title", findString(title));
        }


        // see if the value was specified as a parameter already
        if (parameters.containsKey("value")) {
            parameters.put("nameValue", parameters.get("value"));
        } else {
            if (evaluateNameValue()) {
                final Class valueClazz = getValueClassType();

                if (valueClazz != null) {
                    if (value != null) {
                        addParameter("nameValue", findValue(value, valueClazz));
                    } else if (name != null) {
                        String expr = completeExpressionIfAltSyntax(name);

                        addParameter("nameValue", findValue(expr, valueClazz));
                    }
                } else {
                    if (value != null) {
                        addParameter("nameValue", findValue(value));
                    } else if (name != null) {
                        addParameter("nameValue", findValue(name));
                    }
                }
            }
        }

        final Form form = (Form) findAncestor(Form.class);

        // create HTML id element
        populateComponentHtmlId(form);

        if (form != null ) {
            addParameter("form", form.getParameters());

            if ( name != null ) {
                // list should have been created by the form component
                List<String> tags = (List<String>) form.getParameters().get("tagNames");
                tags.add(name);
            }
        }


        // tooltip & tooltipConfig
        if (tooltipConfig != null) {
            addParameter("tooltipConfig", findValue(tooltipConfig));
        }
        if (tooltip != null) {
            addParameter("tooltip", findString(tooltip));

            Map tooltipConfigMap = getTooltipConfig(this);

            if (form != null) { // inform the containing form that we need tooltip javascript included
                form.addParameter("hasTooltip", Boolean.TRUE);

                // tooltipConfig defined in component itseilf will take precedence
                // over those defined in the containing form
                Map overallTooltipConfigMap = getTooltipConfig(form);
                overallTooltipConfigMap.putAll(tooltipConfigMap); // override parent form's tooltip config

                for (Object o : overallTooltipConfigMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    addParameter((String) entry.getKey(), entry.getValue());
                }
            }
            else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("No ancestor Form found, javascript based tooltip will not work, however standard HTML tooltip using alt and title attribute will still work ");
                }
            }

            //TODO: this is to keep backward compatibility, remove once when tooltipConfig is dropped
            String  jsTooltipEnabled = (String) getParameters().get("jsTooltipEnabled");
            if (jsTooltipEnabled != null)
                this.javascriptTooltip = jsTooltipEnabled;

            //TODO: this is to keep backward compatibility, remove once when tooltipConfig is dropped
            String tooltipIcon = (String) getParameters().get("tooltipIcon");
            if (tooltipIcon != null)
                this.addParameter("tooltipIconPath", tooltipIcon);
            if (this.tooltipIconPath != null)
                this.addParameter("tooltipIconPath", findString(this.tooltipIconPath));

            //TODO: this is to keep backward compatibility, remove once when tooltipConfig is dropped
            String tooltipDelayParam = (String) getParameters().get("tooltipDelay");
            if (tooltipDelayParam != null)
                this.addParameter("tooltipDelay", tooltipDelayParam);
            if (this.tooltipDelay != null)
                this.addParameter("tooltipDelay", findString(this.tooltipDelay));

            if (this.javascriptTooltip != null) {
                Boolean jsTooltips = (Boolean) findValue(this.javascriptTooltip, Boolean.class);
                //TODO use a Boolean model when tooltipConfig is dropped
                this.addParameter("jsTooltipEnabled", jsTooltips.toString());

                if (form != null)
                    form.addParameter("hasTooltip", jsTooltips);
                if (this.tooltipCssClass != null)
                    this.addParameter("tooltipCssClass", findString(this.tooltipCssClass));
            }


        }

        evaluateExtraParams();
    }

	protected String escape(String name) {
        // escape any possible values that can make the ID painful to work with in JavaScript
        if (name != null) {
            return name.replaceAll("[\\/\\.\\[\\]]", "_");
        } else {
            return null;
        }
    }

    /**
     * Ensures an unescaped attribute value cannot be vulnerable to XSS attacks
     *
     * @param val The value to check
     * @return The escaped value
     */
    protected String ensureAttributeSafelyNotEscaped(String val) {
        if (val != null) {
            return val.replaceAll("\"", "&#34;");
        } else {
            return null;
        }
    }

    protected void evaluateExtraParams() {
    }

    protected boolean evaluateNameValue() {
        return true;
    }

    protected Class getValueClassType() {
        return String.class;
    }

    public void addFormParameter(String key, Object value) {
        Form form = (Form) findAncestor(Form.class);
        if (form != null) {
            form.addParameter(key, value);
        }
    }

    protected void enableAncestorFormCustomOnsubmit() {
        Form form = (Form) findAncestor(Form.class);
        if (form != null) {
            form.addParameter("customOnsubmitEnabled", Boolean.TRUE);
        } else {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("Cannot find an Ancestor form, custom onsubmit is NOT enabled");
            }
        }
    }

    protected Map getTooltipConfig(UIBean component) {
        Object tooltipConfigObj = component.getParameters().get("tooltipConfig");
        Map<String, String> tooltipConfig = new LinkedHashMap<String, String>();

        if (tooltipConfigObj instanceof Map) {
            // we get this if its configured using
            // 1] UI component's tooltipConfig attribute  OR
            // 2] <param name="tooltip" value="" /> param tag value attribute

            tooltipConfig = new LinkedHashMap<String, String>((Map)tooltipConfigObj);
        } else if (tooltipConfigObj instanceof String) {

            // we get this if its configured using
            // <param name="tooltipConfig"> ... </param> tag's body
            String tooltipConfigStr = (String) tooltipConfigObj;
            String[] tooltipConfigArray = tooltipConfigStr.split("\\|");

            for (String aTooltipConfigArray : tooltipConfigArray) {
                String[] configEntry = aTooltipConfigArray.trim().split("=");
                String key = configEntry[0].trim();
                String value;
                if (configEntry.length > 1) {
                    value = configEntry[1].trim();
                    tooltipConfig.put(key, value);
                } else {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("component " + component + " tooltip config param " + key + " has no value defined, skipped");
                    }
                }
            }
        }
        if (component.javascriptTooltip != null)
            tooltipConfig.put("jsTooltipEnabled", component.javascriptTooltip);
        if (component.tooltipIconPath != null)
            tooltipConfig.put("tooltipIcon", component.tooltipIconPath);
        if (component.tooltipDelay != null)
            tooltipConfig.put("tooltipDelay", component.tooltipDelay);
        return tooltipConfig;
    }

    /**
     * Create HTML id element for the component and populate this component parameter
     * map. Additionally, a parameter named escapedId is populated which contains the found id value filtered by
     * {@link #escape(String)}, needed eg. for naming Javascript identifiers based on the id value.
     *
     * The order is as follows :-
     * <ol>
     *   <li>This component id attribute</li>
     *   <li>[containing_form_id]_[this_component_name]</li>
     *   <li>[this_component_name]</li>
     * </ol>
     *
     * @param form enclosing form tag
     */
    protected void populateComponentHtmlId(Form form) {
        String tryId;
        String generatedId;
        if (id != null) {
            // this check is needed for backwards compatibility with 2.1.x
            tryId = findStringIfAltSyntax(id);
        } else if (null == (generatedId = escape(name != null ? findString(name) : null))) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cannot determine id attribute for [#0], consider defining id, name or key attribute!", this);
            }
            tryId = null;
        } else if (form != null) {
            tryId = form.getParameters().get("id") + "_" + generatedId;
        } else {
            tryId = generatedId;
        }
        
        //fix for https://issues.apache.org/jira/browse/WW-4299
        //do not assign value to id if tryId is null
        if (tryId != null) {
          addParameter("id", tryId);
          addParameter("escapedId", escape(tryId));
        }
    }

    /**
     * Get's the id for referencing element.
     * @return the id for referencing element.
     */
    public String getId() {
        return id;
    }

    @StrutsTagAttribute(description="HTML id attribute")
    public void setId(String id) {
        if (id != null) {
            this.id = findString(id);
        }
    }

    @StrutsTagAttribute(description="The template directory.")
    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    @StrutsTagAttribute(description="The theme (other than default) to use for rendering the element")
    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTemplate() {
        return template;
    }

    @StrutsTagAttribute(description="The template (other than default) to use for rendering the element")
    public void setTemplate(String template) {
        this.template = template;
    }

    @StrutsTagAttribute(description="The css class to use for element")
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    @StrutsTagAttribute(description="The css class to use for element - it's an alias of cssClass attribute.")
    public void setClass(String cssClass) {
        this.cssClass = cssClass;
    }

    @StrutsTagAttribute(description="The css style definitions for element to use")
    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    @StrutsTagAttribute(description="The css style definitions for element to use - it's an alias of cssStyle attribute.")
    public void setStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    @StrutsTagAttribute(description="The css error class to use for element")
    public void setCssErrorClass(String cssErrorClass) {
        this.cssErrorClass = cssErrorClass;
    }

    @StrutsTagAttribute(description="The css error style definitions for element to use")
    public void setCssErrorStyle(String cssErrorStyle) {
        this.cssErrorStyle = cssErrorStyle;
    }

    @StrutsTagAttribute(description="Set the html title attribute on rendered html element")
    public void setTitle(String title) {
        this.title = title;
    }

    @StrutsTagAttribute(description="Set the html disabled attribute on rendered html element")
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    @StrutsTagAttribute(description="Label expression used for rendering an element specific label")
    public void setLabel(String label) {
        this.label = label;
    }

    @StrutsTagAttribute(description="String that will be appended to the label", defaultValue=":")
    public void setLabelSeparator(String labelseparator) {
        this.labelSeparator = labelseparator;
    }

    @StrutsTagAttribute(description="Define label position of form element (top/left)")
    public void setLabelposition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

    @StrutsTagAttribute(description="Define required position of required form element (left|right)")
    public void setRequiredPosition(String requiredPosition) {
        this.requiredPosition = requiredPosition;
    }

    @StrutsTagAttribute(description="Define error position of form element (top|bottom)")
    public void setErrorPosition(String errorPosition) {
        this.errorPosition = errorPosition;
    }
    
    @StrutsTagAttribute(description="The name to set for element")
    public void setName(String name) {
        this.name = name;
    }

    @StrutsTagAttribute(description="If set to true, the rendered element will indicate that input is required", type="Boolean", defaultValue="false")
    public void setRequiredLabel(String requiredLabel) {
        this.requiredLabel = requiredLabel;
    }

    @StrutsTagAttribute(description="Set the html tabindex attribute on rendered html element")
    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }

    @StrutsTagAttribute(description="Preset the value of input element.")
    public void setValue(String value) {
        this.value = value;
    }

    @StrutsTagAttribute(description="Set the html onclick attribute on rendered html element")
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    @StrutsTagAttribute(description="Set the html ondblclick attribute on rendered html element")
    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }

    @StrutsTagAttribute(description="Set the html onmousedown attribute on rendered html element")
    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }

    @StrutsTagAttribute(description="Set the html onmouseup attribute on rendered html element")
    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }

    @StrutsTagAttribute(description="Set the html onmouseover attribute on rendered html element")
    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    @StrutsTagAttribute(description="Set the html onmousemove attribute on rendered html element")
    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }

    @StrutsTagAttribute(description="Set the html onmouseout attribute on rendered html element")
    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

    @StrutsTagAttribute(description="Set the html onfocus attribute on rendered html element")
    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    @StrutsTagAttribute(description=" Set the html onblur attribute on rendered html element")
    public void setOnblur(String onblur) {
        this.onblur = onblur;
    }

    @StrutsTagAttribute(description="Set the html onkeypress attribute on rendered html element")
    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    @StrutsTagAttribute(description="Set the html onkeydown attribute on rendered html element")
    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    @StrutsTagAttribute(description="Set the html onkeyup attribute on rendered html element")
    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    @StrutsTagAttribute(description="Set the html onselect attribute on rendered html element")
    public void setOnselect(String onselect) {
        this.onselect = onselect;
    }

    @StrutsTagAttribute(description="Set the html onchange attribute on rendered html element")
    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    @StrutsTagAttribute(description="Set the html accesskey attribute on rendered html element")
    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    @StrutsTagAttribute(description="Set the tooltip of this particular component")
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @StrutsTagAttribute(description="Deprecated. Use individual tooltip configuration attributes instead.")
    public void setTooltipConfig(String tooltipConfig) {
        this.tooltipConfig = tooltipConfig;
    }

    @StrutsTagAttribute(description="Set the key (name, value, label) for this particular component")
    public void setKey(String key) {
        this.key = key;
    }

    @StrutsTagAttribute(description="Use JavaScript to generate tooltips", type="Boolean", defaultValue="false")
    public void setJavascriptTooltip(String javascriptTooltip) {
        this.javascriptTooltip = javascriptTooltip;
    }

    @StrutsTagAttribute(description="CSS class applied to JavaScrip tooltips", defaultValue="StrutsTTClassic")
    public void setTooltipCssClass(String tooltipCssClass) {
        this.tooltipCssClass = tooltipCssClass;
    }

    @StrutsTagAttribute(description="Delay in milliseconds, before showing JavaScript tooltips ",
        defaultValue="Classic")
    public void setTooltipDelay(String tooltipDelay) {
        this.tooltipDelay = tooltipDelay;
    }

    @StrutsTagAttribute(description="Icon path used for image that will have the tooltip")
    public void setTooltipIconPath(String tooltipIconPath) {
        this.tooltipIconPath = tooltipIconPath;
    }

	public void setDynamicAttributes(Map<String, Object> tagDynamicAttributes) {
        for (String key : tagDynamicAttributes.keySet()) {
            if (!isValidTagAttribute(key)) {
                dynamicAttributes.put(key, tagDynamicAttributes.get(key));
            }
        }
    }

	@Override
	/**
	 * supports dynamic attributes for freemarker ui tags
	 * @see https://issues.apache.org/jira/browse/WW-3174
     * @see https://issues.apache.org/jira/browse/WW-4166
	 */
    public void copyParams(Map params) {
        super.copyParams(params);
        for (Object o : params.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = (String) entry.getKey();
            if(!isValidTagAttribute(key) && !key.equals("dynamicAttributes"))
                dynamicAttributes.put(key, entry.getValue());
        }
    }

}
