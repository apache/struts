/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.components;

import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;
import org.apache.struts2.components.template.TemplateEngineManager;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.config.Settings;
import org.apache.struts2.views.util.ContextUtil;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.util.OgnlValueStack;

/**
 * UIBean is the standard superclass of all Struts UI componentns.
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
 *          <td>requiredposition</td>
 *          <td>xhtml</td>
 *          <td>String</td>
 *          <td>define required label position of form element (left/right), default to right</td>
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
 *          <td>ondbclick</td>
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
 *  	<td>tooltip</td>
 *  	<td>String</td>
 *  	<td>none</td>
 *  	<td>Set the tooltip of this particular component</td>
 *  </tr>
 *  <tr>
 *      <td>jsTooltipEnabled</td>
 *      <td>String</td>
 *      <td>false</td>
 *      <td>Enable js tooltip rendering</td>
 *  </tr>
 *    <tr>
 *   	<td>tooltipIcon</td>
 *   	<td>String</td>
 *   	<td>/struts/static/tooltip/tooltip.gif</td>
 *   	<td>The url to the tooltip icon</td>
 *   <tr>
 *   	<td>tooltipDelay</td>
 *   	<td>String</td>
 *   	<td>500</td>
 *   	<td>Tooltip shows up after the specified timeout (miliseconds). A behavior similar to that of OS based tooltips.</td>
 *   </tr>
 * </table>
 *
 * <!-- END SNIPPET: tooltipattributes -->
 *
 *
 * <!-- START SNIPPET: tooltipdescription -->
 *
 * Every Form UI component (in xhtml / css_xhtml or any others that extends of them) could
 * have tooltip assigned to a them. The Form component's tooltip related attribute once
 * defined will be applicable to all form UI component that is created under it unless
 * explicitly overriden by having the Form UI component itself defined that tooltip attribute.
 *
 * <p/>
 *
 * In Example 1, the textfield will inherit the tooltipDelay adn tooltipIcon attribte from
 * its containing form. In other words, although it doesn't defined a tooltipAboveMousePointer
 * attribute, it will have that attributes inherited from its containing form.
 *
 * <p/>
 *
 * In Example 2, the the textfield will inherite both the tooltipDelay and
 * tooltipIcon attribute from its containing form but tooltipDelay
 * attribute is overriden at the textfield itself. Hence, the textfield actually will
 * have tooltipIcon defined as /myImages/myIcon.gif, inherited from its containing form and
 * tooltipDelay defined as 5000, due to overriden at the textfield itself.
 *
 * <p/>
 *
 * Example 3, 4 and 5 shows different way of setting the tooltipConfig attribute.<br/>
 * <b>Example 3:</b>Set tooltip config through body of param tag<br/>
 * <b>Example 4:</b>Set tooltip config through value attribute of param tag<br/>
 * <b>Example 5:</b>Set tooltip config through tooltipConfig attribute of component tag<br/>
 *
 * <!-- END SNIPPET: tooltipdescription -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: tooltipexample -->
 *
 * &lt;!-- Example 1: --&gt;
 * &lt;s:form
 * 			tooltipConfig="#{'tooltipDelay':'500',
 *                           'tooltipIcon='/myImages/myIcon.gif'}" .... &gt;
 *   ....
 *     &lt;s:textfield label="Customer Name" tooltip="Enter the customer name" .... /&gt;
 *   ....
 * &lt;/s:form&gt;
 *
 * &lt;!-- Example 2: --&gt;
 * &lt;s:form
 *         tooltipConfig="#{'tooltipDelay':'500',
 *          				'tooltipIcon':'/myImages/myIcon.gif'}" ... &gt;
 *   ....
 *     &lt;s:textfield label="Address"
 *          tooltip="Enter your address"
 *          tooltipConfig="#{'tooltipDelay':'5000'}" /&gt;
 *   ....
 * &lt;/s:form&gt;
 *
 *
 * &lt;-- Example 3: --&gt;
 * &lt;s:textfield
 *        label="Customer Name"
 *	      tooltip="One of our customer Details'"&gt;
 *        &lt;s:param name="tooltipConfig"&gt;
 *             tooltipDelay = 500 |
 *             tooltipIcon = /myImages/myIcon.gif 
 *        &lt;/s:param&gt;
 * &lt;/s:textfield&gt;
 *
 *
 * &lt;-- Example 4: --&gt;
 * &lt;s:textfield
 *	        label="Customer Address"
 *	        tooltip="Enter The Customer Address" &gt;
 *	        &lt;s:param
 *              name="tooltipConfig"
 *              value="#{'tooltipDelay':'500',
 *                       'tooltipIcon':'/myImages/myIcon.gif'}" /&gt;
 * &lt;/s:textfield&gt;
 *
 *
 * &lt;-- Example 5: --&gt;
 * &lt;s:textfield
 *          label="Customer Telephone Number"
 *          tooltip="Enter customer Telephone Number"
 *          tooltipConfig="#{'tooltipDelay':'500',
 *                           'tooltipIcon':'/myImages/myIcon.gif'}" /&gt;
 *
 * <!-- END SNIPPET: tooltipexample -->
 * </pre>
 *
 */
public abstract class UIBean extends Component {
    private static final Log LOG = LogFactory.getLog(UIBean.class);

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    public UIBean(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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

    protected String cssClass;
    protected String cssStyle;
    protected String disabled;
    protected String label;
    protected String labelPosition;
    protected String requiredposition;
    protected String name;
    protected String required;
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


    public boolean end(Writer writer, String body) {
        evaluateParams();
        try {
            super.end(writer, body, false);
            mergeTemplate(writer, buildTemplateName(template, getDefaultTemplate()));
        } catch (Exception e) {
            LOG.error("error when rendering", e);
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
        final TemplateEngine engine = TemplateEngineManager.getTemplateEngine(template, templateSuffix);
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
            templateDir = (String) stack.findValue("#attr.templateDir");
        }

        // Default template set
        if ((templateDir == null) || (templateDir.equals(""))) {
            templateDir = Settings.get(StrutsConstants.STRUTS_UI_TEMPLATEDIR);
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
            theme = (String) stack.findValue("#attr.theme");
        }

        // Default theme set
        if ((theme == null) || (theme.equals(""))) {
            theme = Settings.get(StrutsConstants.STRUTS_UI_THEME);
        }

        return theme;
    }

    public void evaluateParams() {
        addParameter("templateDir", getTemplateDir());
        addParameter("theme", getTheme());

        String name = null;

        if (this.name != null) {
            name = findString(this.name);
            addParameter("name", name);
        }

        if (label != null) {
            addParameter("label", findString(label));
        }

        if (labelPosition != null) {
            addParameter("labelposition", findString(labelPosition));
        }

        if (requiredposition != null) {
            addParameter("requiredposition", findString(requiredposition));
        }

        if (required != null) {
            addParameter("required", findValue(required, Boolean.class));
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
                        String expr = name;
                        if (altSyntax()) {
                            expr = "%{" + expr + "}";
                        }

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
                List tags = (List) form.getParameters().get("tagNames");
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

        		for (Iterator i = overallTooltipConfigMap.entrySet().iterator(); i.hasNext(); ) {
        			Map.Entry entry = (Map.Entry) i.next();
        			addParameter((String) entry.getKey(), entry.getValue());
        		}
        	}
        	else {
        		LOG.warn("No ancestor Form found, javascript based tooltip will not work, however standard HTML tooltip using alt and title attribute will still work ");
        	}
        }
        evaluateExtraParams();

    }

    protected String escape(String name) {
        // escape any possible values that can make the ID painful to work with in JavaScript
        if (name != null) {
            return name.replaceAll("[\\.\\[\\]]", "_");
        } else {
            return "";
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
    		LOG.warn("Cannot find an Ancestor form, custom onsubmit is NOT enabled");
    	}
    }

    protected Map getTooltipConfig(UIBean component) {
    	Object tooltipConfigObj = component.getParameters().get("tooltipConfig");
    	Map tooltipConfig = new LinkedHashMap();

    	if (tooltipConfigObj instanceof Map) {
    		// we get this if its configured using
    		// 1] UI component's tooltipConfig attribute  OR
    		// 2] <param name="tooltip" value="" /> param tag value attribute

    		tooltipConfig = new LinkedHashMap((Map)tooltipConfigObj);
    	} else if (tooltipConfigObj instanceof String) {

    		// we get this if its configured using
    		// <param name="tooltipConfig"> ... </param> tag's body
    		String tooltipConfigStr = (String) tooltipConfigObj;
    		String[] tooltipConfigArray = tooltipConfigStr.split("\\|");

    		for (int a=0; a<tooltipConfigArray.length; a++) {
    			String[] configEntry = ((String)tooltipConfigArray[a].trim()).split("=");
    			String key = configEntry[0].trim();
    			String value = null;
    			if (configEntry.length > 1) {
    				value = configEntry[1].trim();
    				tooltipConfig.put(key, value.toString());
    			}
    			else {
    				LOG.warn("component "+component+" tooltip config param "+key+" has no value defined, skipped");
    			}
    		}
    	}
    	return tooltipConfig;
    }

    /**
     * Create HTML id element for the component and populate this component parmaeter
     * map.
     * 
     * The order is as follows :-
     * <ol>
     *   <li>This component id attribute</li>
     *   <li>[containing_form_id]_[this_component_name]</li>
     *   <li>[this_component_name]</li>
     * </ol>
     * 
     * @param form
     */
    protected void populateComponentHtmlId(Form form) {
    	if (id != null) {
            // this check is needed for backwards compatibility with 2.1.x
            if (altSyntax()) {
                addParameter("id", findString(id));
            } else {
                addParameter("id", id);
            }
        } else if (form != null) {
            addParameter("id", form.getParameters().get("id") + "_" + escape(name));
        } else {
            addParameter("id", escape(name));
        }
    }
    

    /**
     * The template directory.
     * @s.tagattribute required="false"
     */
    public void setTemplateDir(String templateDir) {
    	this.templateDir = templateDir;
    }

    /**
     * The theme (other than default) to use for rendering the element
     * @s.tagattribute required="false"
      */
    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTemplate() {
        return template;
    }

    /**
     * The template (other than default) to use for rendering the element
     * @s.tagattribute required="false"
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * The css class to use for element
     * @s.tagattribute required="false"
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * The css style definitions for element ro use
     * @s.tagattribute required="false"
     */
    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    /**
     * Set the html title attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the html disabled attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    /**
     * Label expression used for rendering a element specific label
     * @s.tagattribute required="false"
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * deprecated.
     * @s.tagattribute required="false" default="left"
     * @deprecated please use {@link #setLabelposition(String)} instead
     */
    public void setLabelPosition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

    /**
     * define label position of form element (top/left)
     * @s.tagattribute required="false"
     */
    public void setLabelposition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

    /**
     * define required position of required form element (left|right)
     * @s.tagattribute required="false"
     */
    public void setRequiredposition(String requiredposition) {
        this.requiredposition = requiredposition;
    }

    /**
     * The name to set for element
     * @s.tagattribute required="false"
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * If set to true, the rendered element will indicate that input is required
     * @s.tagattribute  required="false" type="Boolean" default="false"
     */
    public void setRequired(String required) {
        this.required = required;
    }

    /**
     * Set the html tabindex attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }

    /**
     * Preset the value of input element.
     * @s.tagattribute required="false"
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Set the html onclick attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    /**
     * Set the html ondblclick attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }

    /**
     * Set the html onmousedown attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }

    /**
     * Set the html onmouseup attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }

    /**
     * Set the html onmouseover attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    /**
     * Set the html onmousemove attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }

    /**
     * Set the html onmouseout attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

    /**
     * Set the html onfocus attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    /**
     * Set the html onblur attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnblur(String onblur) {
        this.onblur = onblur;
    }

    /**
     * Set the html onkeypress attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    /**
     * Set the html onkeydown attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    /**
     * Set the html onkeyup attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    /**
     * Set the html onselect attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnselect(String onselect) {
        this.onselect = onselect;
    }

    /**
     * Set the html onchange attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }
    
    /**
     * Set the html accesskey attribute on rendered html element
     * @s.tagattribute required="false"
     */
    public void setAccesskey(String accesskey) {
    	this.accesskey = accesskey;
    }

    /**
     * Set the tooltip of this particular component
     * @s.tagattribute required="false" type="String" default=""
     */
    public void setTooltip(String tooltip) {
    	this.tooltip = tooltip;
    }

    /**
     * Set the tooltip configuration
     * @s.tagattribute required="false" type="String" default=""
     */
    public void setTooltipConfig(String tooltipConfig) {
    	this.tooltipConfig = tooltipConfig;
    }
}
