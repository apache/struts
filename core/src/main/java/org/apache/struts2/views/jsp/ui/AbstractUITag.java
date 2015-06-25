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

package org.apache.struts2.views.jsp.ui;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.util.ComponentUtils;
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
    protected String labelposition;
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
    protected Map<String, Object> dynamicAttributes = new HashMap<>();

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
        uiBean.setLabelposition(labelposition);
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

    public void setId(String id) {
        this.id = id;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

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

    public void setLabelposition(String labelPosition) {
        this.labelposition = labelPosition;
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

    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        if (ComponentUtils.altSyntax(getStack()) && ComponentUtils.isExpression(value)) {
            dynamicAttributes.put(localName, String.valueOf(ObjectUtils.defaultIfNull(findValue(value.toString()), value)));
        } else {
            dynamicAttributes.put(localName, value);
        }
    }

}
