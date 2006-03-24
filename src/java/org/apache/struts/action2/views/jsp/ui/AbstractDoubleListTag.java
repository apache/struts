/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.DoubleListUIBean;
import org.apache.struts.action2.components.DoubleSelect;


/**
 * @author <a href="mailto:m.bogaert@memenco.com">Mathias Bogaert</a>
 * @author tm_jee
 * @version $Date: 2006/01/07 15:08:37 $ $Id: AbstractDoubleListTag.java,v 1.22 2006/01/07 15:08:37 tmjee Exp $
 */
public abstract class AbstractDoubleListTag extends AbstractRequiredListTag {
	
    protected String doubleList;
    protected String doubleListKey;
    protected String doubleListValue;
    protected String doubleName;
    protected String doubleValue;
    protected String formName;
    
    protected String emptyOption;
    protected String headerKey;
    protected String headerValue;
    protected String multiple;
    protected String size;
    
    protected String doubleId;
    protected String doubleDisabled;
    protected String doubleMultiple;
    protected String doubleSize;
    protected String doubleHeaderKey;
    protected String doubleHeaderValue;
    protected String doubleEmptyOption;
    
    protected String doubleCssClass;
    protected String doubleCssStyle;
    
    protected String doubleOnclick;
    protected String doubleOndblclick;
    protected String doubleOnmousedown;
    protected String doubleOnmouseup;
    protected String doubleOnmouseover;
    protected String doubleOnmousemove;
    protected String doubleOnmouseout;
    protected String doubleOnfocus;
    protected String doubleOnblur;
    protected String doubleOnkeypress;
    protected String doubleOnkeydown;
    protected String doubleOnkeyup;
    protected String doubleOnselect;
    protected String doubleOnchange;

    protected void populateParams() {
        super.populateParams();

        DoubleListUIBean bean = ((DoubleListUIBean) this.component);
        bean.setDoubleList(doubleList);
        bean.setDoubleListKey(doubleListKey);
        bean.setDoubleListValue(doubleListValue);
        bean.setDoubleName(doubleName);
        bean.setDoubleValue(doubleValue);
        bean.setFormName(formName);
        
        bean.setDoubleId(doubleId);
        bean.setDoubleDisabled(doubleDisabled);
        bean.setDoubleMultiple(doubleMultiple);
        bean.setDoubleSize(doubleSize);
        bean.setDoubleHeaderKey(doubleHeaderKey);
        bean.setDoubleHeaderValue(doubleHeaderValue);
        bean.setDoubleEmptyOption(doubleEmptyOption);
        
        bean.setDoubleCssClass(doubleCssClass);
        bean.setDoubleCssStyle(doubleCssStyle);
        
        bean.setDoubleOnclick(doubleOnclick);
        bean.setDoubleOndblclick(doubleOndblclick);
        bean.setDoubleOnmousedown(doubleOnmousedown);
        bean.setDoubleOnmouseup(doubleOnmouseup);
        bean.setDoubleOnmouseover(doubleOnmouseover);
        bean.setDoubleOnmousemove(doubleOnmousemove);
        bean.setDoubleOnmouseout(doubleOnmouseout);
        bean.setDoubleOnfocus(doubleOnfocus);
        bean.setDoubleOnblur(doubleOnblur);
        bean.setDoubleOnkeypress(doubleOnkeypress);
        bean.setDoubleOnkeydown(doubleOnkeydown);
        bean.setDoubleOnkeyup(doubleOnkeyup);
        bean.setDoubleOnselect(doubleOnselect);
        bean.setDoubleOnchange(doubleOnchange);
        
        bean.setEmptyOption(emptyOption);
        bean.setHeaderKey(headerKey);
        bean.setHeaderValue(headerValue);
        bean.setMultiple(multiple);
        bean.setSize(size);
    }

    public void setDoubleList(String list) {
        this.doubleList = list;
    }

    public void setDoubleListKey(String listKey) {
        this.doubleListKey = listKey;
    }

    public void setDoubleListValue(String listValue) {
        this.doubleListValue = listValue;
    }

    public void setDoubleName(String aName) {
        doubleName = aName;
    }

    public void setDoubleValue(String doubleValue) {
        this.doubleValue = doubleValue;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

	public String getDoubleCssClass() {
		return doubleCssClass;
	}

	public void setDoubleCssClass(String doubleCssClass) {
		this.doubleCssClass = doubleCssClass;
	}

	public String getDoubleCssStyle() {
		return doubleCssStyle;
	}

	public void setDoubleCssStyle(String doubleCssStyle) {
		this.doubleCssStyle = doubleCssStyle;
	}

	public String getDoubleDisabled() {
		return doubleDisabled;
	}

	public void setDoubleDisabled(String doubleDisabled) {
		this.doubleDisabled = doubleDisabled;
	}

	public String getDoubleEmptyOption() {
		return doubleEmptyOption;
	}

	public void setDoubleEmptyOption(String doubleEmptyOption) {
		this.doubleEmptyOption = doubleEmptyOption;
	}

	public String getDoubleHeaderKey() {
		return doubleHeaderKey;
	}

	public void setDoubleHeaderKey(String doubleHeaderKey) {
		this.doubleHeaderKey = doubleHeaderKey;
	}

	public String getDoubleHeaderValue() {
		return doubleHeaderValue;
	}

	public void setDoubleHeaderValue(String doubleHeaderValue) {
		this.doubleHeaderValue = doubleHeaderValue;
	}

	public String getDoubleId() {
		return doubleId;
	}

	public void setDoubleId(String doubleId) {
		this.doubleId = doubleId;
	}

	public String getDoubleMultiple() {
		return doubleMultiple;
	}

	public void setDoubleMultiple(String doubleMultiple) {
		this.doubleMultiple = doubleMultiple;
	}

	public String getDoubleOnblur() {
		return doubleOnblur;
	}

	public void setDoubleOnblur(String doubleOnblur) {
		this.doubleOnblur = doubleOnblur;
	}

	public String getDoubleOnchange() {
		return doubleOnchange;
	}

	public void setDoubleOnchange(String doubleOnchange) {
		this.doubleOnchange = doubleOnchange;
	}

	public String getDoubleOnclick() {
		return doubleOnclick;
	}

	public void setDoubleOnclick(String doubleOnclick) {
		this.doubleOnclick = doubleOnclick;
	}

	public String getDoubleOndblclick() {
		return doubleOndblclick;
	}

	public void setDoubleOndblclick(String doubleOndblclick) {
		this.doubleOndblclick = doubleOndblclick;
	}

	public String getDoubleOnfocus() {
		return doubleOnfocus;
	}

	public void setDoubleOnfocus(String doubleOnfocus) {
		this.doubleOnfocus = doubleOnfocus;
	}

	public String getDoubleOnkeydown() {
		return doubleOnkeydown;
	}

	public void setDoubleOnkeydown(String doubleOnkeydown) {
		this.doubleOnkeydown = doubleOnkeydown;
	}

	public String getDoubleOnkeypress() {
		return doubleOnkeypress;
	}

	public void setDoubleOnkeypress(String doubleOnkeypress) {
		this.doubleOnkeypress = doubleOnkeypress;
	}

	public String getDoubleOnkeyup() {
		return doubleOnkeyup;
	}

	public void setDoubleOnkeyup(String doubleOnkeyup) {
		this.doubleOnkeyup = doubleOnkeyup;
	}

	public String getDoubleOnmousedown() {
		return doubleOnmousedown;
	}

	public void setDoubleOnmousedown(String doubleOnmousedown) {
		this.doubleOnmousedown = doubleOnmousedown;
	}

	public String getDoubleOnmousemove() {
		return doubleOnmousemove;
	}

	public void setDoubleOnmousemove(String doubleOnmousemove) {
		this.doubleOnmousemove = doubleOnmousemove;
	}

	public String getDoubleOnmouseout() {
		return doubleOnmouseout;
	}

	public void setDoubleOnmouseout(String doubleOnmouseout) {
		this.doubleOnmouseout = doubleOnmouseout;
	}

	public String getDoubleOnmouseover() {
		return doubleOnmouseover;
	}

	public void setDoubleOnmouseover(String doubleOnmouseover) {
		this.doubleOnmouseover = doubleOnmouseover;
	}

	public String getDoubleOnmouseup() {
		return doubleOnmouseup;
	}

	public void setDoubleOnmouseup(String doubleOnmouseup) {
		this.doubleOnmouseup = doubleOnmouseup;
	}

	public String getDoubleOnselect() {
		return doubleOnselect;
	}

	public void setDoubleOnselect(String doubleOnselect) {
		this.doubleOnselect = doubleOnselect;
	}

	public String getDoubleSize() {
		return doubleSize;
	}

	public void setDoubleSize(String doubleSize) {
		this.doubleSize = doubleSize;
	}

	public String getDoubleList() {
		return doubleList;
	}

	public String getDoubleListKey() {
		return doubleListKey;
	}

	public String getDoubleListValue() {
		return doubleListValue;
	}

	public String getDoubleName() {
		return doubleName;
	}

	public String getDoubleValue() {
		return doubleValue;
	}

	public String getFormName() {
		return formName;
	}
	
	public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
