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
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.UIBean;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for all UI tags.
 */
public abstract class AbstractUITag extends ComponentTagSupport implements DynamicAttributes {
    protected String cssClass;
    protected String cssErrorClass;
    protected String cssStyle;
    protected String cssErrorStyle;
    protected String title;
    protected String disabled;
    protected String label;
    protected String labelSeparator;
    protected String labelPosition;
    protected String requiredPosition;
    protected String errorPosition;
    protected String name;
    protected String requiredLabel;
    protected String tabindex;
    protected String value;
    protected String template;
    protected String theme;
    protected String templateDir;
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
    protected String accesskey;
    protected String id;

    protected String key;

    // tooltip attributes
    protected String tooltip;
    protected String tooltipConfig;
    protected String javascriptTooltip;
    protected String tooltipDelay;
    protected String tooltipCssClass;
    protected String tooltipIconPath;

    // dynamic attributes.
    protected Map<String, String> dynamicAttributes = new HashMap<>();

    @Override
    protected void populateParams() {
        super.populateParams();

        UIBean uiBean = (UIBean) component;
        uiBean.setCssClass(cssClass);
        uiBean.setCssStyle(cssStyle);
        uiBean.setCssErrorClass(cssErrorClass);
        uiBean.setCssErrorStyle(cssErrorStyle);
        uiBean.setTitle(title);
        uiBean.setDisabled(disabled);
        uiBean.setLabel(label);
        uiBean.setLabelSeparator(labelSeparator);
        uiBean.setLabelPosition(labelPosition);
        uiBean.setRequiredPosition(requiredPosition);
        uiBean.setErrorPosition(errorPosition);
        uiBean.setName(name);
        uiBean.setRequiredLabel(requiredLabel);
        uiBean.setTabindex(tabindex);
        uiBean.setValue(value);
        uiBean.setTemplate(template);
        uiBean.setTheme(theme);
        uiBean.setTemplateDir(templateDir);
        uiBean.setOnclick(onclick);
        uiBean.setOndblclick(ondblclick);
        uiBean.setOnmousedown(onmousedown);
        uiBean.setOnmouseup(onmouseup);
        uiBean.setOnmouseover(onmouseover);
        uiBean.setOnmousemove(onmousemove);
        uiBean.setOnmouseout(onmouseout);
        uiBean.setOnfocus(onfocus);
        uiBean.setOnblur(onblur);
        uiBean.setOnkeypress(onkeypress);
        uiBean.setOnkeydown(onkeydown);
        uiBean.setOnkeyup(onkeyup);
        uiBean.setOnselect(onselect);
        uiBean.setOnchange(onchange);
        uiBean.setTooltip(tooltip);
        uiBean.setTooltipConfig(tooltipConfig);
        uiBean.setJavascriptTooltip(javascriptTooltip);
        uiBean.setTooltipCssClass(tooltipCssClass);
        uiBean.setTooltipDelay(tooltipDelay);
        uiBean.setTooltipIconPath(tooltipIconPath);
        uiBean.setAccesskey(accesskey);
        uiBean.setKey(key);
        uiBean.setId(id);

        uiBean.setDynamicAttributes(dynamicAttributes);
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * @deprecated Use {@link #setCssClass(String)} instead
     */
    @Deprecated
    public void setClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public void setStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public void setCssErrorClass(String cssErrorClass) {
        this.cssErrorClass = cssErrorClass;
    }

    public void setCssErrorStyle(String cssErrorStyle) {
        this.cssErrorStyle = cssErrorStyle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Deprecated since 2.5.27
     * @deprecated use {@link #setLabelPosition(String)} instead
     */
    @Deprecated
    public void setLabelposition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

    public void setLabelPosition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

    public void setRequiredPosition(String requiredPosition) {
        this.requiredPosition = requiredPosition;
    }

	public void setErrorPosition(String errorPosition) {
		this.errorPosition = errorPosition;
	}

    public void setName(String name) {
        this.name = name;
    }

    public void setRequiredLabel(String requiredLabel) {
        this.requiredLabel = requiredLabel;
    }

    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }

    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }

    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }

    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }

    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    public void setOnblur(String onblur) {
        this.onblur = onblur;
    }

    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    public void setOnselect(String onselect) {
        this.onselect = onselect;
    }

    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public void setTooltipConfig(String tooltipConfig) {
        this.tooltipConfig = tooltipConfig;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setJavascriptTooltip(String javascriptTooltip) {
        this.javascriptTooltip = javascriptTooltip;
    }

    public void setTooltipCssClass(String tooltipCssClass) {
        this.tooltipCssClass = tooltipCssClass;
    }

    public void setTooltipDelay(String tooltipDelay) {
        this.tooltipDelay = tooltipDelay;
    }

    public void setTooltipIconPath(String tooltipIconPath) {
        this.tooltipIconPath = tooltipIconPath;
    }

    public void setLabelSeparator(String labelSeparator) {
        this.labelSeparator = labelSeparator;
    }

    @Override
    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        dynamicAttributes.put(localName, String.valueOf(value));
    }

    @Override
    /**
     * Must declare the setter at the descendant Tag class level in order for the tag handler to locate the method.
     */
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
       if (getPerformClearTagStateForTagPoolingServers() == false) {
            return;  // If flag is false (default setting), do not perform any state clearing.
        }
        super.clearTagStateForTagPoolingServers();
        this.cssClass = null;
        this.cssErrorClass = null;
        this.cssStyle = null;
        this.cssErrorStyle = null;
        this.title = null;
        this.disabled = null;
        this.label = null;
        this.labelSeparator = null;
        this.labelPosition = null;
        this.requiredPosition = null;
        this.errorPosition = null;
        this.name = null;
        this.requiredLabel = null;
        this.tabindex = null;
        this.value = null;
        this.template = null;
        this.theme = null;
        this.templateDir = null;
        this.onclick = null;
        this.ondblclick = null;
        this.onmousedown = null;
        this.onmouseup = null;
        this.onmouseover = null;
        this.onmousemove = null;
        this.onmouseout = null;
        this.onfocus = null;
        this.onblur = null;
        this.onkeypress = null;
        this.onkeydown = null;
        this.onkeyup = null;
        this.onselect = null;
        this.onchange = null;
        this.accesskey = null;
        this.id = null;
        this.key = null;
        this.tooltip = null;
        this.tooltipConfig = null;
        this.javascriptTooltip = null;
        this.tooltipDelay = null;
        this.tooltipCssClass = null;
        this.tooltipIconPath = null;
        this.dynamicAttributes.clear();
    }

}
